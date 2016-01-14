package machinosoccerweb.registration.models;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class EmailConf {
  public EmailConf() {

  }

  public EmailConf(String email, String recordId, Date appliedAt) {
    this.email = email;
    this.recordId = recordId;
    this.appliedAt = appliedAt;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String email;

  private String recordId;

  private Date appliedAt;

  private Date confirmedAt = new Date();
}
