package machinosoccerweb.members.repositories;

import machinosoccerweb.members.models.MemberPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberPhotoRepository extends JpaRepository<MemberPhoto, String> {}
