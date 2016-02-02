package machinosoccerweb.members.web;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import machinosoccerweb.members.models.Email;
import machinosoccerweb.members.models.Member;
import machinosoccerweb.members.models.Parent;
import machinosoccerweb.members.repositories.EmailRepository;
import machinosoccerweb.members.repositories.MemberRepository;
import machinosoccerweb.members.repositories.ParentRepository;
import machinosoccerweb.security.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
public class MypageController {
  private final ParentRepository parentRepository;

  private final MemberRepository memberRepository;

  private final EmailRepository emailRepository;

  @Autowired
  public MypageController(ParentRepository parentRepository,
                          MemberRepository memberRepository,
                          EmailRepository emailRepository) {
    this.parentRepository = parentRepository;
    this.memberRepository = memberRepository;
    this.emailRepository = emailRepository;
  }

  @RequestMapping("/mypage")
  public String index(@AuthenticationPrincipal LoginUser loginUser, Model model) {
    Long familyId = loginUser.getFamilyId();
    if (!loginUser.isParentRegistered()) {
      return "redirect:/mypage/contact";
    }

    Parent parent = parentRepository.findOne(familyId);
    model.addAttribute("parent", parent);
    log.debug("loginUser:{}, parent:{}", loginUser, parent);

    List<Member> members = memberRepository.findByFamilyId(familyId);
    model.addAttribute("members", members);

    List<Email> emails = emailRepository.findByFamilyId(familyId);
    model.addAttribute("contacts", emails);

    return "mypage/index";
  }
}
