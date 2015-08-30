package machinosc;

import machinosc.registration.models.EmailConf;
import machinosc.registration.repositories.EmailConfRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.crypto.Mac;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.Optional;

@Controller
@EnableAutoConfiguration
public class EmailConfController
{
    private static final Logger logger = LoggerFactory.getLogger(EmailConfController.class);

    @Value("${machinosoccerweb.hmackey}")
    private String confPhrase;

    @Autowired
    private HmacService hmacService;

    @Autowired
    private EmailConfRepository confRepository;

    @RequestMapping("/confirmed")
    public String confirmed() {
        return "conf";
    }

    @RequestMapping("/confirm-error")
    public String confirmError() {
        return "conferror";
    }

    @RequestMapping("/conf")
    public String conf(@RequestParam("a") String email,
                       @RequestParam("k") String key,
                       @RequestParam("r") String id,
                       @RequestParam("t") String appliedAtHex,
                       Model model) throws GeneralSecurityException {

        Optional<Date> appliedAt = this.parseAppliedDateParam(appliedAtHex);

        Mac mac = hmacService.createHmacSHA256(confPhrase);
        String hash = hmacService.calcHashToHexString(mac, (appliedAtHex+email).getBytes());
        if(appliedAt.isPresent() && hash.equals(key)) {
            EmailConf conf = new EmailConf(email, id, appliedAt.get());
            this.confRepository.saveAndFlush(conf);
            return "redirect:/confirmed";
        } else {
            logger.error("email conf error with given params: address [{}]. hash [{}]. record id: [{}]. applied time: [{}]", email, hash, id, appliedAtHex);

            return "redirect:/confirm-error";
        }
    }

    private Optional<Date> parseAppliedDateParam(String appliedAtHex) {
        try {
            Long appliedTimeMillis = Long.parseLong(appliedAtHex, 16);
            Date appliedAt = new Date(appliedTimeMillis);
            return Optional.of(appliedAt);
        } catch (RuntimeException e) {
            logger.error("[{}]: exception occurred on parsing [{}]", e, appliedAtHex);
            return Optional.empty();
        }
    }
}

