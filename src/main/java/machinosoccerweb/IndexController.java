package machinosoccerweb;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {
  @Value("${machinosoccerweb.registerationUrl}")
  private String registerationUrl;

  @RequestMapping("/")
  public String index(Model model) {
    model.addAttribute("registerUrl", registerationUrl);
    return "index";
  }

  @RequestMapping("/ping")
  public @ResponseBody String ping() {
    return "pong";
  }
}
