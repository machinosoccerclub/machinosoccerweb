package machinosoccerweb.members.models;

import javax.persistence.MappedSuperclass;

import lombok.AllArgsConstructor;
import lombok.Data;

@MappedSuperclass
@Data
@AllArgsConstructor
public class GooglePicasaPhotoEntry {
  public GooglePicasaPhotoEntry() {
    // this is for JPA
  }

  public GooglePicasaPhotoEntry(GooglePicasaPhotoEntry picasaPhotoEntry) {
    this.photoUrl = picasaPhotoEntry.photoUrl;
    this.thumbnailUrl = picasaPhotoEntry.thumbnailUrl;
    this.picasaPhotoEntryId = picasaPhotoEntry.picasaPhotoEntryId;
    this.picasaPhotoEntryEditURI = picasaPhotoEntry.picasaPhotoEntryEditURI;
  }

  private String photoUrl;

  private String thumbnailUrl;

  private String picasaPhotoEntryId;

  private String picasaPhotoEntryEditURI;
}
