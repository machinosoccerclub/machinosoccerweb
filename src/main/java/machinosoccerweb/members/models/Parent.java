package machinosoccerweb.members.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class Parent {
  @Id
  @GeneratedValue
  private Long familyId;

  private String givenName;
  private String familyName;
  private String givenNameKana;
  private String familyNameKana;

  private String phoneNumber1;
  private String phoneNumber2;
}
