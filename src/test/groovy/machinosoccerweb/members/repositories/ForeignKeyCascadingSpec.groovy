package machinosoccerweb.members.repositories

import machinosoccerweb.Application
import machinosoccerweb.members.models.Account
import machinosoccerweb.members.models.Course
import machinosoccerweb.members.models.Gender
import machinosoccerweb.members.models.Grade
import machinosoccerweb.members.models.Member
import machinosoccerweb.members.models.MemberPhoto
import machinosoccerweb.members.models.Parent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification

import java.time.LocalDate

@ContextConfiguration(loader = SpringApplicationContextLoader, classes = Application)
@EnableConfigurationProperties
@ActiveProfiles(["development", "unitTest"])
@WebAppConfiguration
class ForeignKeyCascadingSpec extends Specification{
  @Autowired
  private AccountRepository accountRepository
  @Autowired
  private ParentRepository parentRepository
  @Autowired
  private MemberRepository memberRepository
  @Autowired
  private MemberPhotoRepository memberPhotoRepository

  def "an account and members should be deleted when the associated parent is deleted" () {
    setup:
    def emailAddress = "example@example.com"

    // parent population
    def parent = parentRepository.save(
        new Parent(givenName: "", givenNameKana:"", familyName: "", familyNameKana: "",
            phoneNumber1: ""))
    parentRepository.flush()

    // account population
    accountRepository.save(
        new Account(emailAddress,
            true, true,
            parent.familyId,
            Account.Status.AddressConfirmed,
            "users"))

    accountRepository.flush()

    // member population
    def memberNo1 = "1234567"
    def memberNo2 = "9999999"
    def members = [memberNo1, memberNo2].collect {no ->
      new Member(memberNo: no, familyName: "", familyNameKana: "",
          givenName: "", givenNameKana: "",
          grade: Grade.Elem1, gender: Gender.Male,
          joinedAt: LocalDate.now(), course: Course.Elem1,
          familyId: parent.familyId)
    }
    memberRepository.save(members)
    memberRepository.flush()

    when:
    memberRepository.findAll().size() == 2
    accountRepository.findAll().size() == 1
    parentRepository.delete(parent.familyId)
    parentRepository.flush()

    then:
    accountRepository.findOne(emailAddress) == null
    memberRepository.findAll().size() == 0
  }

  def "a member photo should be deleted when the owner member is deleted" () {
    setup:
    // parent population
    def parent = parentRepository.save(
        new Parent(givenName: "", givenNameKana:"", familyName: "", familyNameKana: "",
            phoneNumber1: ""))

    // member population
    def memberNo1 = "1234567"
    def memberNo2 = "9999999"
    def members = [memberNo1, memberNo2].collect {no ->
      new Member(memberNo: no, familyName: "", familyNameKana: "",
          givenName: "", givenNameKana: "",
          grade: Grade.Elem1, gender: Gender.Male,
          joinedAt: LocalDate.now(), course: Course.Elem1,
          photo: new MemberPhoto(memberNo: no, photoUrl: "", thumbnailUrl: "",
          picasaPhotoEntryId: "", picasaPhotoEntryEditURI: ""),
          familyId: parent.familyId)
    }
    memberRepository.save(members)
    memberRepository.flush()

    when:
    memberRepository.findAll().size() == 2
    memberPhotoRepository.findAll().size() == 2
    memberRepository.delete(memberNo1)

    then:
    memberRepository.findAll().size() == 1
    memberPhotoRepository.findAll().size() == 1
    memberPhotoRepository.findOne(memberNo2) != null
  }
}
