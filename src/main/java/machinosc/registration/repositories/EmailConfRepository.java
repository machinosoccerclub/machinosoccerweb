package machinosc.registration.repositories;

import machinosc.registration.models.EmailConf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailConfRepository extends JpaRepository<EmailConf, Long> {
}
