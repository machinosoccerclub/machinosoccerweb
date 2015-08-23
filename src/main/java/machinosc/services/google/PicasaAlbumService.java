package machinosc.services.google;

import machinosc.services.google.api.PicasaAlbum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit.RestAdapter;
import retrofit.converter.SimpleXMLConverter;
import retrofit.mime.TypedByteArray;

import java.io.InputStream;

@Service
public class PicasaAlbumService {
  @Autowired
  private OAuth2Service oAuth2Service;

  @Value("${google.api.picasa.userId}")
  private String userId;

  @Value("${google.api.picasa.albumId}")
  private String albumId;

  public void uploadPhoto(String fileName, String description, String mimeType, String[] tags, InputStream photo) {
    uploadPhoto(oAuth2Service.refreshToken(), fileName, description, mimeType, tags, photo);
  }

  public void uploadPhoto(AccessToken token, String fileName, String description, String mimeType, String[] tags, InputStream photo) {
    PicasaAlbum picasaService = createPicasaAlbumService();

    TypedOutputStream outputStream = new TypedOutputStream(fileName, -1L, mimeType, photo);

    Entry entry = picasaService.uploadPhoto(
      token.getAuthzValue(),
      fileName,
      this.userId,
      this.albumId,
      outputStream);

    picasaService.updatePhotoMedaInf(
      token.getAuthzValue(),
      this.userId,
      this.albumId,
      entry.getPhotoId(),
      createMetaDataXmlPart(description, String.join(", ", tags))
    );
  }

  private PicasaAlbum createPicasaAlbumService() {
    RestAdapter picasaRestAdapter = new RestAdapter.Builder()
      .setEndpoint("https://picasaweb.google.com")
      .setLogLevel(RestAdapter.LogLevel.FULL)
      .setConverter(new SimpleXMLConverter())
      .build();
    return picasaRestAdapter.create(PicasaAlbum.class);
  }

  private TypedByteArray createMetaDataXmlPart(String description, String tags) {
    String xml = "<entry xmlns='http://www.w3.org/2005/Atom' " +
      "xmlns:media='http://search.yahoo.com/mrss/'>" +
      "<summary>" + description + "</summary>" +
      "<media:group>" +
      //"<media:description type='plain'>" + description + "</media:description>" +
      "<media:keywords>" + tags + "</media:keywords>" +
      "</media:group>" +
      "</entry>";

    return new TypedByteArray("application/atom+xml", xml.getBytes());
  }
}
