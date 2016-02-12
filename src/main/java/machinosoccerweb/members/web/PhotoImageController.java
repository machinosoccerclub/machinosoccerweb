package machinosoccerweb.members.web;

import java.io.IOException;

import com.google.gdata.util.ServiceException;
import lombok.extern.slf4j.Slf4j;
import machinosoccerweb.members.models.Member;
import machinosoccerweb.members.services.MemberPhotoService;
import machinosoccerweb.security.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static machinosoccerweb.ControllerUtils.ResourceNotFoundException;
import static machinosoccerweb.ControllerUtils.unauthorized;

@Slf4j
@Controller
@RequestMapping("/mypage/member/")
public class PhotoImageController {
  private final MemberPhotoService memberPhotoService;

  @Autowired
  public PhotoImageController(MemberPhotoService memberPhotoService) {
    this.memberPhotoService = memberPhotoService;
  }

  @RequestMapping(value = "{memberNo}/photo", method = RequestMethod.GET)
  public String form(@AuthenticationPrincipal LoginUser loginUser,
                     @PathVariable("memberNo") String memberNo,
                     @ModelAttribute Member member) {
    Member memberFound = ensureMember(memberNo, loginUser);
    BeanUtils.copyProperties(memberFound, member);

    return "mypage/photo";
  }

  @RequestMapping(value = "{memberNo}/photo", method = RequestMethod.POST)
  public String upload(@AuthenticationPrincipal LoginUser loginUser,
                       @PathVariable("memberNo") String memberNo,
                       @RequestParam("photo") MultipartFile file,
                       RedirectAttributes redirectAttributes)
      throws IOException, ServiceException {

    Member member = ensureMember(memberNo, loginUser);

    if (!file.isEmpty()) {
      try {
        memberPhotoService.updatePhoto(member,
            file.getInputStream(), file.getOriginalFilename(), file.getContentType());
        return "redirect:/mypage";
      } catch (Exception googleServiceException) {
        redirectAttributes.addFlashAttribute("error", googleServiceException);
        return "redirect:/mypage/member/" + memberNo + "/photo?error";
      }
    } else {
      redirectAttributes.addFlashAttribute("error", file);
      return "redirect:/mypage/member/" + memberNo + "/photo?error";
    }
  }

  private Member ensureMember(String memberNo, LoginUser loginUser) {
    Member member = memberPhotoService.findMember(memberNo)
        .orElseThrow(ResourceNotFoundException::new);

    if (member.getFamilyId() != loginUser.getFamilyId()) {
      log.error("SECURITY NOTICE ** unauthorized ** : " +
              "loginUser:{} attempts uploading photo for the member: {}",
          loginUser, member);
      throw unauthorized();
    }

    return member;
  }
}
