package machinosoccerweb.security;

import java.util.ArrayList;
import java.util.Collection;

import lombok.Data;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@ToString(exclude = "password")
public class LoginUser implements UserDetails {

  private final String username;
  private final String password;

  private final Collection<? extends GrantedAuthority> authorities;

  private final long userId;

  @Override
  public boolean isAccountNonExpired() {
    return true;
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
}
