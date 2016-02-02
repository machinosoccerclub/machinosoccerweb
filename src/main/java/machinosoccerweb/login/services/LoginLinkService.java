package machinosoccerweb.login.services;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import machinosoccerweb.infra.HmacUtils;
import machinosoccerweb.login.models.LoginLinkRequest;
import machinosoccerweb.login.repositories.LoginLinkRequestRepository;
import machinosoccerweb.members.models.Email;
import machinosoccerweb.members.repositories.EmailRepository;
import machinosoccerweb.security.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoginLinkService {
  private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

  private final HmacUtils hmacUtils;

  private final Clock clock;

  private final LoginLinkRequestRepository loginLinkRequestRepository;

  private final EmailRepository emailRepository;

  @Value("${machinosoccerweb.hmackey}")
  private String confPhrase;

  @Autowired
  public LoginLinkService(HmacUtils hmacUtils,
                          Clock clock,
                          LoginLinkRequestRepository loginLinkRequestRepository,
                          EmailRepository emailRepository) {
    this.hmacUtils = hmacUtils;
    this.clock = clock;
    this.loginLinkRequestRepository = loginLinkRequestRepository;
    this.emailRepository = emailRepository;
  }

  public LoginLinkRequest createLoginLinkRequest(String emailAddress) {
    LocalDate today = LocalDate.now(clock);

    String dateAddr = dateFormatter.format(today) + emailAddress;

    String encodedDateAddr =
        Base64.getEncoder().encodeToString(dateAddr.getBytes(StandardCharsets.UTF_8));

    String signature = hmacUtils.calcHashToHexString(
        hmacUtils.createHmacSha256(confPhrase),
        dateAddr.getBytes(StandardCharsets.UTF_8));

    return new LoginLinkRequest(encodedDateAddr, signature, emailAddress, today);
  }

  public void save(LoginLinkRequest loginLinkRequest) {
    loginLinkRequestRepository.save(loginLinkRequest);
  }

  public Optional<LoginUser> getLoginUser(String param) {
    LoginLinkRequest request = loginLinkRequestRepository.findOne(param);
    if (request == null) {
      log.debug("accessing too old or non-exist request: {}", param);
      return Optional.empty();
    }

    boolean nonExpired = !LocalDate.now(clock).isAfter(request.expiryDate());

    Email email = emailRepository.findOne(request.getEmailAddress());
    return Optional.of(new LoginUser(request, email, nonExpired));
  }
}
