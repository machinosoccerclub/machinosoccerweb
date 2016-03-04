package machinosoccerweb.members.repositories;

import java.util.List;

import machinosoccerweb.members.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
  List<Account> findByFamilyId(Long familyId);
}