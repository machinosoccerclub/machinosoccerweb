package machinosoccerweb.jpa

import machinosoccerweb.members.models.Member
import machinosoccerweb.members.models.MemberPhoto
import machinosoccerweb.members.models.Parent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
public interface JpaMemberRepository extends JpaRepository<Member, String> {
  List<Member> findByFamilyId(Long familyId);
}

@Repository
public interface JpaParentRepository extends JpaRepository<Parent, Long> {
}

@Repository
public interface JpaMemberPhotoRepository extends JpaRepository<MemberPhoto, String> {}