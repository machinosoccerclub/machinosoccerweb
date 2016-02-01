package machinosoccerweb.login.repositories;

import machinosoccerweb.infra.TempCollectionRepository;
import machinosoccerweb.login.models.LoginLinkRequest;
import org.springframework.stereotype.Repository;

@Repository
public class LoginLinkRequestRepository extends TempCollectionRepository<LoginLinkRequest, String> {
  @Override
  protected String convert(long newId) {
    return String.valueOf(newId);
  }
}
