package machinosoccerweb;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ControllerUtils {
  private ControllerUtils() {}

  public static ResourceNotFoundException notFound() {
    return new ResourceNotFoundException();
  }

  public static UnauthorizedException unauthorized() {
    return new UnauthorizedException();
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  public static class ResourceNotFoundException extends RuntimeException {}

  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public static class UnauthorizedException extends RuntimeException {}
}
