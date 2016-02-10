package machinosoccerweb.registration.web;

import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.Optional;

import javax.crypto.Mac;

import machinosoccerweb.infra.HmacUtils;
import machinosoccerweb.registration.models.EmailConf;
import machinosoccerweb.registration.repositories.EmailConfRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@EnableAutoConfiguration
public class EmailConfController {
  private static final Logger logger = LoggerFactory.getLogger(EmailConfController.class);

  @Value("${machinosoccerweb.hmackey}")
  private String confPhrase;

  @Autowired
  private HmacUtils hmacUtils;

  @Autowired
  private EmailConfRepository confRepository;

  @RequestMapping("/confirmed")
  public String confirmed() {
    return "registration/conf";
  }

  @RequestMapping("/confirm-error")
  public String confirmError() {
    return "registration/conferror";
  }

  @RequestMapping("/conf")
  public String conf(@RequestParam("a") String email,
                     @RequestParam("k") String key,
                     @RequestParam("r") String id,
                     @RequestParam("t") String appliedAtHex,
                     Model model) throws GeneralSecurityException {

    Optional<Date> appliedAt = this.parseAppliedDateParam(appliedAtHex);

    Mac mac = hmacUtils.createHmacSha256(confPhrase);
    String hash = hmacUtils.calcHashToHexString(mac, (appliedAtHex + email).getBytes());
    if (appliedAt.isPresent() && hash.equals(key)) {
      EmailConf conf = new EmailConf(email, id, appliedAt.get());
      this.confRepository.saveAndFlush(conf);
      return "redirect:/confirmed";
    } else {
      logger.error("email conf error with given params: " +
              "address [{}]. hash [{}]. record id: [{}]. applied time: [{}]",
          email, hash, id, appliedAtHex);

      return "redirect:/confirm-error";
    }
  }

  private Optional<Date> parseAppliedDateParam(String appliedAtHex) {
    try {
      Long appliedTimeMillis = Long.parseLong(appliedAtHex, 16);
      Date appliedAt = new Date(appliedTimeMillis);
      return Optional.of(appliedAt);
    } catch (NumberFormatException invalidNumberFormat) {
      logger.error("[{}]: exception occurred on parsing [{}]",
          invalidNumberFormat, appliedAtHex);
      return Optional.empty();
    }
  }
}

