package machinosoccerweb.google;

import machinosoccerweb.google.api.PicasawebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit.RestAdapter;
import retrofit.converter.SimpleXMLConverter;
import retrofit.mime.TypedByteArray;

import java.io.InputStream;

@Service
public class Picasaweb {
  @Autowired
  private GoogleOAuth2 googleOAuth2;

  @Value("${google.api.picasa.userId}")
  private String userId;

  @Value("${google.api.picasa.albumId}")
  private String albumId;

  @Value("${retrofit.debug.contentsLogging:false}")
  private boolean contentsLogging;

  public void uploadPhoto(String fileName, String description, String mimeType, String[] tags, InputStream photo) {
    uploadPhoto(googleOAuth2.refreshToken(), fileName, description, mimeType, tags, photo);
  }

  public void uploadPhoto(AccessToken token, String fileName, String description, String mimeType, String[] tags, InputStream photo) {
    PicasawebService picasaService = createPicasaAlbumService();

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

  private PicasawebService createPicasaAlbumService() {
    RestAdapter picasaRestAdapter = setupAdapter(
      new RestAdapter.Builder()
      .setEndpoint("https://picasaweb.google.com")
      .setConverter(new SimpleXMLConverter())
    ).build();
    return picasaRestAdapter.create(PicasawebService.class);
  }

  private RestAdapter.Builder setupAdapter(RestAdapter.Builder adapter) {
    if(contentsLogging) {
      return adapter.setLogLevel(RestAdapter.LogLevel.FULL);
    } else {
      return adapter;
    }
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
