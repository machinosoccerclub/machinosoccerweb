package machinosoccerweb.jpa

import machinosoccerweb.members.models.Member
import machinosoccerweb.members.models.MemberPhoto
import machinosoccerweb.members.models.Parent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
public interface JpaMemberRepository extends JpaRepository<Member, String> {
  List<Member> findByFamilyId(Long familyId);

  @Query(value = "select nextval('sq_member_serial')", nativeQuery = true)
  Long getNextSerialNumber();
}

@Repository
public interface JpaParentRepository extends JpaRepository<Parent, Long> {
}

@Repository
public interface JpaMemberPhotoRepository extends JpaRepository<MemberPhoto, String> {}