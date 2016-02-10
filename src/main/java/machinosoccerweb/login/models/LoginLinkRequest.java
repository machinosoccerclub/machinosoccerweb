package machinosoccerweb.login.models;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@Entity
public class LoginLinkRequest {
  public static final int EXPIRY_DAYS = 2;

  @Id
  private String encodedDateAddress;

  @Column(name = "hash_key")
  private String key;

  private String emailAddress;

  private LocalDate issuedDate;

  public LoginLinkRequest() {
    // this is for JPA framework
  }

  @Transient
  public LocalDate expiryDate() {
    return issuedDate.plusDays(EXPIRY_DAYS);
  }
}
