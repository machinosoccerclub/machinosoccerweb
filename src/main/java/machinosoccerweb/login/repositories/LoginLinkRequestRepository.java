package machinosoccerweb.login.repositories;

import java.time.LocalDate;

import machinosoccerweb.login.models.LoginLinkRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginLinkRequestRepository extends JpaRepository<LoginLinkRequest, String> {
  @Modifying
  @Query("delete LoginLinkRequest r where r.issuedDate < :date")
  int deleteLoginLinkRequestsBefore(@Param("date")LocalDate date);

  @Modifying
  @Query("delete LoginLinkRequest r where r.emailAddress = :emailAddress")
  int deleteByEmailAddress(@Param("emailAddress")String emailAddress);
}
