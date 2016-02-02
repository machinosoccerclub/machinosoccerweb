package machinosoccerweb.members.web;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import machinosoccerweb.infra.validator.Katakana;
import machinosoccerweb.infra.validator.TelephoneNumber;
import machinosoccerweb.members.models.Email;
import machinosoccerweb.members.models.Parent;
import machinosoccerweb.members.repositories.EmailRepository;
import machinosoccerweb.members.repositories.ParentRepository;
import machinosoccerweb.security.LoginUser;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Slf4j
@Controller
public class ContactController {

  private final ParentRepository parentRepository;

  private final EmailRepository emailRepository;

  @Autowired
  public ContactController(ParentRepository parentRepository, EmailRepository emailRepository) {
    this.parentRepository = parentRepository;
    this.emailRepository = emailRepository;
  }

  @RequestMapping(value = "/mypage/contact", method = RequestMethod.GET)
  public String contactForm(@AuthenticationPrincipal LoginUser loginUser,
                            @ModelAttribute("parent") ParentForm parentForm) {

    if (loginUser.isParentRegistered()) {
      Parent parent = parentRepository.findOne(loginUser.getFamilyId());
      BeanUtils.copyProperties(parent, parentForm);
    } else {
      parentForm.setBlank(true);
    }

    return "mypage/contact";
  }

  @RequestMapping(value = "/mypage/contact", method = RequestMethod.POST)
  public String saveContact(@AuthenticationPrincipal LoginUser loginUser,
                            @Validated @ModelAttribute("parent") ParentForm parentForm,
                            BindingResult result) {
    if (result.hasErrors()) {
      log.debug("validation error:{}", result);
      if (!loginUser.isParentRegistered()) {
        parentForm.setBlank(true);
      }

      return "mypage/contact";
    }

    Parent parent = new Parent();
    parent.setFamilyId(loginUser.getFamilyId());
    BeanUtils.copyProperties(parentForm, parent);
    Parent saved = parentRepository.save(parent);

    if (loginUser.isParentRegistered()) {
      return "redirect:/mypage";
    } else {
      Email email = new Email(loginUser.getEmailAddress(), true, true,
          saved.getFamilyId(), Email.Status.AddressConfirmed, "user");
      emailRepository.save(email);

      // todo: need to update the authenticated principal
      return "redirect:/emailConf?a=" + loginUser.getUsername() + "&k=" + loginUser.getPassword();
    }
  }

  @Data
  public static class ParentForm {
    @NotBlank
    private String givenName;
    @NotBlank
    private String familyName;
    @NotBlank
    @Katakana
    private String givenNameKana;
    @NotBlank
    @Katakana
    private String familyNameKana;

    @NotBlank
    @TelephoneNumber
    private String phoneNumber1;
    @TelephoneNumber
    private String phoneNumber2;

    private boolean isBlank;
  }
}
