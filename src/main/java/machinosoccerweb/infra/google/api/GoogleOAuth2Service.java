package machinosoccerweb.infra.google.api;

import machinosoccerweb.infra.google.AccessToken;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public interface GoogleOAuth2Service {

  @FormUrlEncoded
  @POST("/o/oauth2/token")
  AccessToken refreshToken(
          @Field("client_id") String clientId,
          @Field("client_secret") String clientSecret,
          @Field("refresh_token") String refreshToken,
          @Field("grant_type") String grantType
  );
}
