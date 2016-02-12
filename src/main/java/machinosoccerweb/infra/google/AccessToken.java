package machinosoccerweb.infra.google;

public class AccessToken {
  private String accessToken;
  private String tokenType;

  public String getAccessToken() {
    return this.accessToken;
  }

  public String getTokenType() {
    return this.tokenType;
  }

  public String getAuthzValue() {
    return getTokenType() + " " + getAccessToken();
  }
}
