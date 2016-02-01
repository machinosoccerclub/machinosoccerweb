package machinosoccerweb.login.web;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.util.Locale;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import machinosoccerweb.login.models.LoginLinkRequest;
import machinosoccerweb.login.services.LoginLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Slf4j
public class RequestLoginLinkController {
  private static final String ValidEmailPattern = "^[^@]+@[^.]+.[^.]+$";
  // proxy
  private final HttpServletRequest request;

  private final LoginLinkService loginLinkService;

  private final Environment environment;

  private final MessageSource messageSource;

  private final Pattern pattern = Pattern.compile(ValidEmailPattern);

  @Autowired
  public RequestLoginLinkController(HttpServletRequest request,
                                    LoginLinkService loginLinkService,
                                    Environment environment,
                                    MessageSource messageSource) {
    this.request = request;
    this.loginLinkService = loginLinkService;
    this.environment = environment;
    this.messageSource = messageSource;
  }

  @RequestMapping(value = "/requestLoginLink", method = RequestMethod.POST)
  public String sendLoginLink(@RequestParam("email") String email, RedirectAttributes attr)
      throws GeneralSecurityException {
    // validate email address format
    if (!pattern.matcher(email).matches()) {
      attr.addFlashAttribute("email", email);
      attr.addFlashAttribute("error", "invalidEmailAddressFormat");
      return "redirect:/";
    }

    //todo: check if email is registered

    LoginLinkRequest loginLinkRequest = loginLinkService.createLoginLinkRequest(email);
    loginLinkService.save(loginLinkRequest);

    String loginLink = createLoginLink(loginLinkRequest);

    Object[] params = {loginLink, loginLinkRequest.expiryDate(), "xxxxxxsender@oreore.com"};
    String message = messageSource.getMessage("loginLinkEmail", params, Locale.getDefault());
    //todo: send an email contains the required login url.


    attr.addFlashAttribute("email", email);

    if (environment.acceptsProfiles("development")) {
      attr.addFlashAttribute("message", message);
      attr.addFlashAttribute("loginLink", loginLink);
    }
    return "redirect:/loginLinkSent";
  }

  @RequestMapping(value = "/loginLinkSent", method = RequestMethod.GET)
  public String loginLinkSent() {
    return "login/loginLinkSent";
  }

  @RequestMapping("/loginError")
  public String loginError() {
    return "login/loginError";
  }

  private String createLoginLink(LoginLinkRequest loginLinkRequest) {
    log.debug("requestURL:{}, path:{}", request.getRequestURL(), request.getPathInfo());
    try {
      String path = String.format("/emailConf?a=%s&k=%s",
          URLEncoder.encode(loginLinkRequest.getEncodedDateAddress(), "utf-8"),
          loginLinkRequest.getKey());
      return request.getRequestURL().toString().replace("/requestLoginLink", path);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }
}
