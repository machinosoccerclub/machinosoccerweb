package machinosoccerweb.members.models;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;

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

  public enum Status {
    AddressConfirming,
    AddressConfirmed,
    AddressError
  }
}
