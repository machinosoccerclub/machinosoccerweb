package machinosoccerweb.google.api;

import machinosoccerweb.google.Entry;
import retrofit.client.Response;
import retrofit.http.*;
import retrofit.mime.TypedOutput;

public interface PicasawebService {
  @POST("/data/feed/api/user/{userID}/albumid/{albumID}")
  Entry uploadPhoto(
    @retrofit.http.Header("Authorization") String token,
    @retrofit.http.Header("Slug") String fileName,
    @Path("userID") String userId,
    @Path("albumID") String albumId,
    @Body TypedOutput photo
  );

  @Headers({
        "Content-Type: application/atom+xml",
        "GData-Version: 2",
        "If-Match: *"})
  @PUT("/data/entry/api/user/{userID}/albumid/{albumID}/photoid/{photoID}")
  Response updatePhotoMedaInf(
    @retrofit.http.Header("Authorization") String token,
    @Path("userID") String userId,
    @Path("albumID") String albumId,
    @Path("photoID") String photoId,
    @Body TypedOutput photoMetaInf
  );
}
