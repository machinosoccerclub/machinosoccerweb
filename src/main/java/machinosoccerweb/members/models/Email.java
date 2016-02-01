package machinosoccerweb.members.models;

import java.util.Arrays;
import java.util.Collection;
import java.util.StringTokenizer;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Data
@AllArgsConstructor
public class Email {
  @Id
  private String emailAddress;

  private boolean receiveActivitySchedule;

  private boolean receiveApplyNotification;

  private long familyId;

  @Enumerated
  private Status status;

  private String roles;

  @Transient
  public Collection<String> getAuthorities() {
    return Arrays.asList(roles.split(","));
  }

  public enum Status {
    AddressConfirming,
    AddressConfirmed,
    AddressError
  }
}