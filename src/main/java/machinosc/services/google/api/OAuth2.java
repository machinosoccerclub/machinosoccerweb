package machinosc.services.google.api;

import machinosc.services.google.AccessToken;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public interface OAuth2 {

  @FormUrlEncoded
  @POST("/o/oauth2/token")
  AccessToken refreshToken(
          @Field("client_id") String clientId,
          @Field("client_secret") String clientSecret,
          @Field("refresh_token") String refreshToken,
          @Field("grant_type") String grantType
  );
}
