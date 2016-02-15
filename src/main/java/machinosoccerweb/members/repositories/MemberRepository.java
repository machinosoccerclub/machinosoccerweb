package machinosoccerweb.members.repositories;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import machinosoccerweb.infra.TempCollectionRepository;
import machinosoccerweb.members.models.Gender;
import machinosoccerweb.members.models.GooglePicasaPhotoEntry;
import machinosoccerweb.members.models.Grade;
import machinosoccerweb.members.models.Member;
import machinosoccerweb.members.models.MemberPhoto;
import org.springframework.stereotype.Repository;

@Repository
public class MemberRepository extends TempCollectionRepository<Member, String> {
  public MemberRepository() {
    super(initialMember());
  }

  public List<Member> findByFamilyId(long familyId) {
    return super.findAll().stream()
        .filter(m -> m.getFamilyId() == familyId)
        .collect(Collectors.toList());
  }

  public Long getNextSerialNumber() {
    return incrementAndGetId();
  }

  @Override
  protected String convert(long newId) {
    return String.valueOf(newId);
  }

  private static List<Member> initialMember() {
    Member member = new Member();
    member.setMemberNo("1234567");
    member.setFamilyName("鈴木");
    member.setFamilyNameKana("すずき");
    member.setGivenName("花子");
    member.setGivenNameKana("はなこ");
    member.setGender(Gender.Female);
    member.setGrade(Grade.Elem1);
    member.setJoinedAt(LocalDate.now());
    member.setFamilyId(Long.valueOf("user".hashCode()));

    MemberPhoto photo = new MemberPhoto("1234567",
        new GooglePicasaPhotoEntry(
            "https://www.google.co.jp/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png",
            "", "", ""));
    member.setPhoto(photo);

    return Arrays.asList(member);
  }
}
