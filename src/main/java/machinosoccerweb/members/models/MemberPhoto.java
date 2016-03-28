package machinosoccerweb.members.models;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class MemberPhoto extends GooglePicasaPhotoEntry {
  public MemberPhoto() { // this is for JPA
    super();
  }

  public MemberPhoto(Long id, GooglePicasaPhotoEntry picasaPhotoEntry) {
    super(picasaPhotoEntry);
    this.id = id;
  }

  @Id
  private Long id;
}
