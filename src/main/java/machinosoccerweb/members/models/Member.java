package machinosoccerweb.members.models;

import java.time.LocalDate;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import lombok.Data;

@Entity
@Data
public class Member {
  @Id
  private String memberNo;

  private String givenName;
  private String familyName;
  private String givenNameKana;
  private String familyNameKana;

  @Enumerated
  private Grade grade;

  @Enumerated
  private Gender gender;

  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @PrimaryKeyJoinColumn
  private MemberPhoto photo;

  private LocalDate joinedAt;

  private Long familyId;
}
