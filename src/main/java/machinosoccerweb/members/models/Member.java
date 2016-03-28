package machinosoccerweb.members.models;

import java.time.LocalDate;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Transient;

import lombok.Data;

@Entity
@Data
public class Member {
  @Id
  private Long id;

  private String name;
  private String namekana;

  @Enumerated
  private Grade grade;

  @Enumerated
  private Gender gender;

  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @PrimaryKeyJoinColumn
  private MemberPhoto photo;
}
