package machinosoccerweb.google;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import machinosoccerweb.google.api.GoogleOAuth2Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

@Service
public class GoogleOAuth2 {
  @Value("${google.api.clientId}")
  private String googleClientId;

  @Value("${google.api.clientSecret}")
  private String googleClientSecret;

  @Value("${google.api.refreshToken}")
  private String googleRefreshToken;

  public AccessToken refreshToken() {

    GoogleOAuth2Service authService = createRestAdapter().create(GoogleOAuth2Service.class);

    AccessToken tokenResp = authService.refreshToken(
      this.googleClientId,
      this.googleClientSecret,
      this.googleRefreshToken,
      "refresh_token"
    );

    return tokenResp;
  }

  private RestAdapter createRestAdapter() {
    Gson gson = new GsonBuilder()
      .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
      .create();

    return new RestAdapter.Builder()
      .setEndpoint("https://accounts.google.com")
      .setConverter(new GsonConverter(gson))
      .build();
  }
}
