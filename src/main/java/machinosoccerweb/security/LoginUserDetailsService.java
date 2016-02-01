package machinosoccerweb.security;

import java.util.Arrays;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import machinosoccerweb.login.models.LoginLinkRequest;
import machinosoccerweb.login.repositories.LoginLinkRequestRepository;
import machinosoccerweb.login.services.LoginLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoginUserDetailsService implements UserDetailsService {
  private final LoginLinkService loginLinkService;
  private final LoginLinkRequestRepository loginLinkRequestRepository;

  @Autowired
  public LoginUserDetailsService(LoginLinkService loginLinkService,
                                 LoginLinkRequestRepository loginLinkRequestRepository) {
    this.loginLinkService = loginLinkService;
    this.loginLinkRequestRepository = loginLinkRequestRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String param) throws UsernameNotFoundException {
    log.debug("searching dateAddress: {}", param);
    Optional<LoginUser> user = loginLinkService.getLoginUser(param);
    return user.orElseThrow(() -> new UsernameNotFoundException(param));
  }
}
