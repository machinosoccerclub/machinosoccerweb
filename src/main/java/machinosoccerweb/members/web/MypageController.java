package machinosoccerweb.members.web;

import lombok.extern.slf4j.Slf4j;
import machinosoccerweb.security.LoginUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
public class MypageController {
  @RequestMapping("/mypage")
  public String index(@AuthenticationPrincipal LoginUser loginUser) {
    log.debug("{}", loginUser);
    return "mypage/index";
  }
}
