package machinosoccerweb.infra.google;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GooglePicasaPhotoEntry {
  private String photoUrl;
  private String thumbnailUrl;
  private String id;
  private String editUrl;
}
