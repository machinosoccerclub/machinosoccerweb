package machinosoccerweb.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import machinosoccerweb.login.models.LoginLinkRequest;
import machinosoccerweb.members.models.Email;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class LoginUser implements UserDetails {
  private final LoginLinkRequest loginLinkRequest;
  private final Email email;
  private final Collection<? extends GrantedAuthority> authorities;
  private final boolean isAccountNonExpired;

  public LoginUser(LoginLinkRequest loginLinkRequest,
                   Email email,
                   boolean isAccountNonExpired) {
    this.loginLinkRequest = loginLinkRequest;
    this.email = email;
    this.authorities = email != null
        ? email.getAuthorities().stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList()) :
        Arrays.asList(new SimpleGrantedAuthority("user"));
    this.isAccountNonExpired = isAccountNonExpired;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getUsername() {
    return loginLinkRequest.getEncodedDateAddress();
  }

  @Override
  public String getPassword() {
    return loginLinkRequest.getKey();
  }

  @Override
  public boolean isAccountNonExpired() {
    return isAccountNonExpired;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  public Long getFamilyId() {
    return email != null ? email.getFamilyId() : null;
  }

  public boolean isParentRegistered() {
    return getFamilyId() != null;
  }

  public String getEmailAddress() {
    return loginLinkRequest.getEmailAddress();
  }
}
