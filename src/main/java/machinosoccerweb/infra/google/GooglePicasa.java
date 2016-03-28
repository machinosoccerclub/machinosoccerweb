package machinosoccerweb.infra.google;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Comparator;
import java.util.Optional;

import com.google.gdata.client.photos.PicasawebService;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.media.MediaStreamSource;
import com.google.gdata.data.media.mediarss.MediaKeywords;
import com.google.gdata.data.media.mediarss.MediaThumbnail;
import com.google.gdata.data.photos.PhotoEntry;
import com.google.gdata.util.ServiceException;
import com.google.gdata.util.Version;
import lombok.extern.slf4j.Slf4j;
import machinosoccerweb.members.models.GooglePicasaPhotoEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GooglePicasa {
  private final String albumId;

  private final GoogleOAuth2 googleOAuth2;

  private static final String GoogleClientAppName = "machinosoccer-dev";

  @Autowired
  public GooglePicasa(@Value("${google.api.picasa.userId}") String picasaUserId,
                      @Value("${google.api.picasa.albumId}") String picasaAlbumId,
                      GoogleOAuth2 googleOAuth2) {
    albumId = String.format("https://picasaweb.google.com/data/feed/api/user/%s/albumid/%s",
        picasaUserId, picasaAlbumId);
    this.googleOAuth2 = googleOAuth2;
  }

  public GooglePicasaPhotoEntry uploadPhoto(String title, String summary, String[] tags,
                                            InputStream is, String contentType) {
    try {
      return uploadPhotoInternal(title, summary, tags, is, contentType);
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    }
  }

  public GooglePicasaPhotoEntry updatePhoto(GooglePicasaPhotoEntry entry, String title,
                                            InputStream is, String contentType) {
    try {
      return updatePhotoInternal(entry, title, is, contentType);
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    }
  }

  public static Optional<String> normalizeMediaType(String contentType) {
    if (contentType == null) {
      return Optional.empty();
    }

    switch (contentType) {
      case "image/bmp":
      case "image/gif":
      case "image/jpg":
      case "image/png":
        return Optional.of(contentType);

      case "image/jpeg":
        return Optional.of("image/jpg");

      default:
        return Optional.empty();
    }
  }

  private GooglePicasaPhotoEntry updatePhotoInternal(GooglePicasaPhotoEntry entry,
                                                     String title,
                                                     InputStream is, String contentType)
      throws IOException, ServiceException {

    // note: when we update and replace only photo, do below lines
    //
    //PhotoEntry updated =
    //      createPicasaServiceForUpdate().updateMedia(new URL(entry.getPicasaPhotoEntryEditURI()),
    //          PhotoEntry.class, new MediaStreamSource(is, contentType));

    // we are updating photo and meta-data (title).
    PhotoEntry photoEntry = new PhotoEntry();
    photoEntry.setTitle(new PlainTextConstruct(title));

    photoEntry.setMediaSource(new MediaStreamSource(is, contentType));

    PhotoEntry updated =
        createPicasaServiceForUpdate().updateMedia(new URL(entry.getPicasaPhotoEntryEditURI()),
            photoEntry);

    return createEntry(updated);
  }

  private GooglePicasaPhotoEntry uploadPhotoInternal(String title, String summary, String[] tags,
                                                     InputStream is, String contentType)
      throws IOException, ServiceException {

    PhotoEntry photoEntry = new PhotoEntry();
    photoEntry.setTitle(new PlainTextConstruct(title));
    photoEntry.setSummary(new PlainTextConstruct(summary));

    photoEntry.setMediaSource(new MediaStreamSource(is, contentType));

    MediaKeywords keywords = new MediaKeywords();
    keywords.addKeyword(String.join(", ", tags));
    photoEntry.setKeywords(keywords);

    PhotoEntry uploaded = createPicasaService().insert(new URL(albumId), photoEntry);
    return createEntry(uploaded);
  }

  private GooglePicasaPhotoEntry createEntry(PhotoEntry entry) {
    printPhotoEntry(entry);

    String photoUrl = entry.getMediaContents().stream()
        .map(m -> m.getUrl())
        .findFirst()
        .orElseGet(() -> failingImagePath());
    String thumbnailUrl = entry.getMediaThumbnails().stream()
        .sorted(Comparator.comparing(this::thumbnailImageSize).reversed())
        .map(t -> t.getUrl())
        .findFirst()   // we choose the largest one (that's why sorted `reversed`)
        .orElseGet(() -> failingImagePath());

    GooglePicasaPhotoEntry photo = new GooglePicasaPhotoEntry(photoUrl, thumbnailUrl,
        entry.getId(), entry.getMediaEditLink().getHref());

    log.debug("Photo entry: {}", photo);

    return photo;
  }

  private String failingImagePath() {
    log.error("URI for photo or thumbnail is not found in the response ...");
    return "/images/warn.png";
  }

  private int thumbnailImageSize(MediaThumbnail thumbnail) {
    return thumbnail.getHeight() * thumbnail.getWidth();
  }

  private PicasawebService createPicasaServiceForUpdate() {
    PicasawebService picasawebService = createPicasaService();
    picasawebService.setHeader("If-Match", "*");

    return picasawebService;
  }

  private PicasawebService createPicasaService() {
    PicasawebService picasawebService = new PicasawebService(GoogleClientAppName);
    // todo: cache the api auth token
    AccessToken token = googleOAuth2.refreshToken();

    log.debug("obtained access token: {}", token.getAccessToken());

    picasawebService.setHeader("Authorization", token.getAuthzValue());
    picasawebService.setProtocolVersion(new Version(PicasawebService.class, "2"));
    return picasawebService;
  }

  private void printPhotoEntry(PhotoEntry photoEntry) {
    log.debug("PhotoEntry=>  id: {}, editLink: {}, mediaEditLink: `{}`",
        photoEntry.getId(),
        photoEntry.getEditLink().getHref(),
        photoEntry.getMediaEditLink().getHref()
    );

    log.debug("title:{}, desc:{}, tags:{}, photoURI:`{}`",
        photoEntry.getTitle().getPlainText(),
        photoEntry.getDescription().getPlainText(),
        photoEntry.getMediaKeywords().getKeywords(),
        photoEntry.getMediaContents().stream().map(m -> m.getUrl()).findFirst().orElse(null));
    photoEntry.getMediaThumbnails().forEach(this::printThumbnail);
  }

  private void printThumbnail(MediaThumbnail thumbnail) {
    log.debug("thumbnails -- url:{}, height:{}, width:{}",
        thumbnail.getUrl(),
        thumbnail.getHeight(),
        thumbnail.getWidth());
  }
}
