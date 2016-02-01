package machinosoccerweb.members.repositories;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import machinosoccerweb.infra.TempCollectionRepository;
import machinosoccerweb.members.models.Email;
import org.springframework.stereotype.Repository;

@Repository
public class EmailRepository extends TempCollectionRepository<Email, String> {

  public EmailRepository() {
    super(Arrays.asList(
        new Email("user@example.com",
            true, true,
            "user".hashCode(),
            Email.Status.AddressConfirmed,
            "user")));
  }

  @Override
  protected String convert(long newId) {
    return String.valueOf(newId);
  }
}
