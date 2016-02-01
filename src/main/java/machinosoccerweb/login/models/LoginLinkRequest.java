package machinosoccerweb.login.models;

import java.time.LocalDate;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class LoginLinkRequest {
  public static final int EXPIRY_DAYS = 2;

  @Id
  private String encodedDateAddress;

  private String key;

  private String emailAddress;

  //todo: implement converter
  @Temporal(TemporalType.DATE)
  private LocalDate issuedDate;

  public LoginLinkRequest() {
    // this is for JPA framework
  }

  @Transient
  public LocalDate expiryDate() {
    return issuedDate.plusDays(EXPIRY_DAYS);
  }
}
