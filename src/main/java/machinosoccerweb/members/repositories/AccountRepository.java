package machinosoccerweb.members.repositories;

import java.util.Arrays;

import machinosoccerweb.infra.TempCollectionRepository;
import machinosoccerweb.members.models.Account;
import org.springframework.stereotype.Repository;

@Repository
public class AccountRepository extends TempCollectionRepository<Account, String> {

  public AccountRepository() {
    super(Arrays.asList(
        new Account("user@example.com",
            true, true,
            "user".hashCode(),
            Account.Status.AddressConfirmed,
            "user")));
  }

  @Override
  protected String convert(long newId) {
    return String.valueOf(newId);
  }
}
