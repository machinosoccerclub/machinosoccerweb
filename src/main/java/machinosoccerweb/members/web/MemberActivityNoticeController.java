package machinosoccerweb.members.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import machinosoccerweb.members.models.Member;
import machinosoccerweb.members.services.MemberService;
import machinosoccerweb.security.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/mypage/member")
public class MemberActivityNoticeController {
  private final MemberService memberService;

  @Autowired
  public MemberActivityNoticeController(MemberService memberService) {
    this.memberService = memberService;
  }

  @RequestMapping(value = "{memberNo}/activityNotice", method = RequestMethod.GET)
  public String showForm(@AuthenticationPrincipal LoginUser loginUser,
                         @PathVariable("memberNo") String memberNo,
                         Model model) {
    Member member = memberService.ensureAuthorizedMember(memberNo, loginUser);

    model.addAttribute("member", member);
    model.addAttribute("activityNoticeForm", ActivityNoticeForm.from(member));
    return "mypage/activityNotice";
  }

  @RequestMapping(value = "{memberNo}/activityNotice", method = RequestMethod.POST)
  public String updateActivityNotice(@AuthenticationPrincipal LoginUser loginUser,
                                     @PathVariable("memberNo") String memberNo,
                                     @Validated @ModelAttribute("activityNoticeForm")
                                       ActivityNoticeForm form) {
    Member member = memberService.ensureAuthorizedMember(memberNo, loginUser);
    member.setActivityNotice(form.getActivityNotice());
    memberService.save(member);

    return "redirect:/mypage";
  }

  @Data
  @AllArgsConstructor
  public static class ActivityNoticeForm {
    private String activityNotice;

    private String hasNotice;

    public ActivityNoticeForm() {}

    public String getActivityNotice() {
      if ("no".equals(hasNotice)) {
        return "";
      } else {
        return activityNotice;
      }
    }

    public static ActivityNoticeForm from(Member member) {
      return new ActivityNoticeForm(
          member.getActivityNotice(),
          member.hasActivityNotice() ? "yes" : "no");
    }
  }
}
