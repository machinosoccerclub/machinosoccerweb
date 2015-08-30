package machinosc.services.google;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import machinosc.services.google.api.OAuth2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

@Service
public class OAuth2Service {
  @Value("${google.api.clientId}")
  private String googleClientId;

  @Value("${google.api.clientSecret}")
  private String googleClientSecret;

  @Value("${google.api.refreshToken}")
  private String googleRefreshToken;

  public AccessToken refreshToken() {

    OAuth2 authService = createRestAdapter().create(OAuth2.class);

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
