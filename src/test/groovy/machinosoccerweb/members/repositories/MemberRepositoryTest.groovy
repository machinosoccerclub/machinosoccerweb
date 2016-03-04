package machinosoccerweb.members.repositories

import machinosoccerweb.Application
import machinosoccerweb.members.models.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import javax.persistence.EntityManager
import javax.transaction.Transactional
import java.time.LocalDate
import java.util.stream.Collectors

@RunWith(SpringJUnit4ClassRunner)
@SpringApplicationConfiguration(Application)
@EnableConfigurationProperties
@ActiveProfiles(profiles = ["development", "unitTest"], inheritProfiles = true)
@Transactional
@Rollback
class MemberRepositoryTest {
  @Autowired
  private MemberRepository memberRepository

  @Autowired
  private ParentRepository parentRepository

  @Autowired
  private MemberPhotoRepository memberPhotoRepository

  @Autowired
  private EntityManager entityManager;

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
          grade: Grade.Elem1, joinedAt: LocalDate.now(), course: Course.Elem1, familyId: familyId)

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
    def memberPhoto = new MemberPhoto('1234560',
        new GooglePicasaPhotoEntry('aaa', 'nnn', 'b', 'm'))
    memberPhotoRepository.saveAndFlush(memberPhoto)

    clearCache(memberRepository.findOne('1234560'))

    def member = memberRepository.findOne('1234560')

    assert memberPhoto == member.photo
  }

  @Test
  public void savePhotoByProperty() {
    String memberNo = '1234561'
    def memberPhoto = new MemberPhoto(memberNo,
        new GooglePicasaPhotoEntry('aaa', 'nnn', 'b', 'm'))

    def member = memberRepository.findOne(memberNo)
    assert member.photo == null

    member.photo = memberPhoto

    memberRepository.saveAndFlush(member)

    assert memberRepository.findOne(memberNo).photo == memberPhoto
  }

  @Test
  void serialNo() {
    Long first = memberRepository.getNextSerialNumber()

    Long second = memberRepository.getNextSerialNumber()

    assert first + 1 == second
  }

  def getSerial = { int n ->
    print "$n,"
    def serial = memberRepository.getNextSerialNumber()
    sleep((n % 3) * 100)
    serial
  }

  @Test
  void parallelSequence() {
    def serialNumbers = (0..<50).parallelStream()
        .map(getSerial)
        .collect(Collectors.toSet())
    // collect to set so that we have a set of unique numbers

    assert serialNumbers.size() == 50
  }

  private void clearCache(Object entity) {
    entityManager.refresh(entity)
    entityManager.getEntityManagerFactory().cache.evictAll()
  }
}
