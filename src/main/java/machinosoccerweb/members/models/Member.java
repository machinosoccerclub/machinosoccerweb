package machinosoccerweb.members.models;

import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

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

  @Temporal(TemporalType.DATE)
  @DateTimeFormat(pattern = "yyyy-MM")
  private Date joinedAt;

  private Long familyId;
}
