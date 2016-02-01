package machinosoccerweb;

import machinosoccerweb.security.LoginUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {
  @RequestMapping("/")
  public String index(@AuthenticationPrincipal LoginUser loginUser) {
    if (isAuthenticated(loginUser)) {
      return "redirect:/mypage";
    } else {
      return "index";
    }
  }

  @RequestMapping("/ping")
  @ResponseBody
  public String ping() {
    return "pong";
  }

  private boolean isAuthenticated(LoginUser loginUser) {
    return loginUser != null;
  }
}
