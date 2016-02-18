package machinosoccerweb.members.web;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import machinosoccerweb.infra.validator.Katakana;
import machinosoccerweb.members.models.Gender;
import machinosoccerweb.members.models.Grade;
import machinosoccerweb.members.models.Member;
import machinosoccerweb.members.models.Parent;
import machinosoccerweb.members.repositories.ParentRepository;
import machinosoccerweb.members.services.MemberService;
import machinosoccerweb.security.LoginUser;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Slf4j
@Controller
public class MemberController {
  private final ParentRepository parentRepository;
  private final MemberService memberService;

  @Autowired
  public MemberController(ParentRepository parentRepository, MemberService memberService) {
    this.parentRepository = parentRepository;
    this.memberService = memberService;
  }

  @RequestMapping(value = "/mypage/member/new", method = RequestMethod.GET)
  public String memberForm(@AuthenticationPrincipal LoginUser loginUser,
      @ModelAttribute("member") MemberForm memberForm) {

    if (!loginUser.isParentRegistered()) {
      // illegal window trantision
      return "redirect:/mypage/contact";
    }

    Parent parent = parentRepository.findOne(loginUser.getFamilyId());
    memberForm.setFamilyName(parent.getFamilyName());
    memberForm.setFamilyNameKana(parent.getFamilyNameKana());

    return "mypage/member";
  }

  @RequestMapping(value = "/mypage/member/new", method = RequestMethod.POST)
  public String saveMember(@AuthenticationPrincipal LoginUser loginUser,
      @Validated @ModelAttribute("member") MemberForm memberForm, BindingResult result) {

    if (result.hasErrors()) {
      log.debug("validation error:{}", result);
      return "mypage/member";
    }

    Member member = new Member();
    BeanUtils.copyProperties(memberForm, member);

    member.setFamilyId(loginUser.getFamilyId());
    memberService.save(member);

    return "redirect:/mypage";
  }

  @Data
  private static class MemberForm {

    private String memberNo;

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

    @NotNull
    @Enumerated
    private Grade grade;

    @NotNull
    @Enumerated
    private Gender gender;

    // todo: need validation between 2013-05 to current (inclusive)
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM")
    private Date joinedAtMonth;

    public LocalDate getJoinedAt() {
      return Optional.ofNullable(joinedAtMonth)
          .map(d -> LocalDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault()))
          .map(ldt -> ldt.toLocalDate())
          .orElse(null);
    }
  }
}
