package machinosoccerweb.jpa

import machinosoccerweb.login.models.LoginLinkRequest
import machinosoccerweb.members.models.Member
import machinosoccerweb.members.models.MemberPhoto
import machinosoccerweb.members.models.Parent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

import java.time.LocalDate

@Repository
public interface JpaMemberRepository extends JpaRepository<Member, String> {
  List<Member> findByFamilyId(Long familyId);

  @Query(value = "select nextval('sq_member_serial')", nativeQuery = true)
  Long getNextSerialNumber();
}

@Repository
public interface JpaParentRepository extends JpaRepository<Parent, Long> {}

@Repository
public interface JpaMemberPhotoRepository extends JpaRepository<MemberPhoto, String> {}

@Repository
public interface JpaLoginLinkRequestRepository extends JpaRepository<LoginLinkRequest, String> {
  @Modifying
  @Query("delete LoginLinkRequest r where r.issuedDate < :date")
  int deleteLoginLinkRequestsBefore(@Param("date")LocalDate date);

  @Modifying
  @Query("delete LoginLinkRequest r where r.emailAddress = :emailAddress")
  int deleteByEmailAddress(@Param("emailAddress")String emailAddress);
}