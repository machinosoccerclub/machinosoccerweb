package machinosoccerweb.members.models;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Data
@AllArgsConstructor
public class MemberPhoto {
  @Id
  private String memberNo;

  private String photoUrl;

  private String thumbnailUrl;

  private String picasaPhotoEntryId;

  private String picasaPhotoEntryEditURI;
}