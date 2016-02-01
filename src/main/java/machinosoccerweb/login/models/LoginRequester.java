package machinosoccerweb.login.models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

import lombok.Data;
import org.apache.commons.io.Charsets;

@Data
public class LoginRequester {
  private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

  private final String email;
  private final LocalDate issuedDate;

  public static LoginRequester parse(String requestParamString) {
    String decoded = new String(Base64.getDecoder().decode(requestParamString), Charsets.UTF_8);
    String dateString = decoded.substring(0, 6);
    String email = decoded.substring(6);

    return new LoginRequester(email, LocalDate.parse(dateString, dateFormatter));
  }

  public String toRequestParam() {
    String raw = dateFormatter.format(issuedDate) + email;

    return Base64.getEncoder().encodeToString(raw.getBytes(Charsets.UTF_8));
  }
}
