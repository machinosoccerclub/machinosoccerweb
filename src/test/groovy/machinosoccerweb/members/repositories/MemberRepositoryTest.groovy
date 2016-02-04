package machinosoccerweb.members.repositories
import machinosoccerweb.Application
import machinosoccerweb.jpa.JpaMemberPhotoRepository
import machinosoccerweb.jpa.JpaMemberRepository
import machinosoccerweb.jpa.JpaParentRepository
import machinosoccerweb.members.models.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

@RunWith(SpringJUnit4ClassRunner)
@SpringApplicationConfiguration(Application)
@EnableConfigurationProperties
@ActiveProfiles("unitTest")
class MemberRepositoryTest {
  @Autowired
  private JpaMemberRepository memberRepository

  @Autowired
  private JpaParentRepository parentRepository

  @Autowired
  private JpaMemberPhotoRepository memberPhotoRepository

  private Long familyId

  @Before
  public void setup() {
    def parent = new Parent(givenName: 'g1', familyName: 'f1', givenNameKana: 'g1kana',
        familyNameKana: 'f1kana', phoneNumber1: '000-111-222')

    def saved = parentRepository.save(parent)
    familyId = saved.familyId

    (0..<3).each { i ->
      def member = new Member(memberNo: '123456' + i, givenName: 'g1', familyName: 'f1',
          givenNameKana: 'g1kana', familyNameKana: 'f1kana', gender: Gender.Female,
          grade: Grade.Elem1, joinedAt: new Date(), familyId: familyId)

      memberRepository.save(member)
    }
  }

  @Test
  public void findByFamilyId() {
    def members = memberRepository.findByFamilyId(familyId)

    assert 3 == members.size()
    assert ["1234560", "1234561", "1234562"] == members.collect { m -> m.memberNo }
  }

  @Test
  public void savePhotoByPhotoRepository() {
    def memberPhoto = new MemberPhoto('1234560', 'aaa', 'nnn', 'b', 'm')
    memberPhotoRepository.saveAndFlush(memberPhoto)

    def member = memberRepository.findOne('1234560')

    assert memberPhoto == member.photo
  }

  @Test
  public void savePhotoByProperty() {
    String memberNo = '1234561'
    def memberPhoto = new MemberPhoto(memberNo, 'aaa', 'nnn', 'b', 'm')

    def member = memberRepository.findOne(memberNo)
    assert member.photo == null

    member.photo = memberPhoto

    memberRepository.saveAndFlush(member)

    assert memberRepository.findOne(memberNo).photo == memberPhoto
  }
}
