package machinosoccerweb.members.repositories;

import java.util.List;

import machinosoccerweb.members.models.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {
  List<Member> findByFamilyId(Long familyId);

  @Query(value = "select nextval('sq_member_serial')", nativeQuery = true)
  Long getNextSerialNumber();
}