package machinosoccerweb.registration.repositories;

import machinosoccerweb.registration.models.EmailConf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailConfRepository extends JpaRepository<EmailConf, Integer> {
}
