package machinosoccerweb.login.repositories
import machinosoccerweb.Application
import machinosoccerweb.jpa.JpaLoginLinkRequestRepository
import machinosoccerweb.login.models.LoginLinkRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification
import spock.lang.Unroll

import javax.transaction.Transactional
import java.time.LocalDate

@ContextConfiguration(loader = SpringApplicationContextLoader, classes = Application)
@EnableConfigurationProperties
@ActiveProfiles(["development", "unitTest"])
@WebAppConfiguration
@Transactional
@Rollback
class LoginLinkRequestRepositoryTest extends Specification {
  @Autowired
  private JpaLoginLinkRequestRepository linkRequestRepository

  @Unroll
  def "persist an entity with attribute typed java.time.LocalDate"() {
    setup:
    def date = LocalDate.of(2013, 5, 14)

    when:
    linkRequestRepository.saveAndFlush(new LoginLinkRequest(encodedDateAddress: "123", key: "456",
        emailAddress: "aaa@example.com", issuedDate: date))

    then:
    def entity = linkRequestRepository.findOne("123")
    assert entity.issuedDate == date
  }

  @Unroll
  def "delete old #beforeDays entities, remaining #afterDays entities newer."() {
    setup:
    def specificDate = LocalDate.of(2020, 1, 30)

    def oldEntities = (0..<beforeDays)
        .collect { d ->
          def date = specificDate.minusDays(d+1)
          new LoginLinkRequest(encodedDateAddress: date.toString(), key: "456",
              emailAddress: "aaa@example.com", issuedDate: date)
        }
    linkRequestRepository.save(oldEntities)

    def newEntities = (0..<afterDays).collect { d ->
      def date = specificDate.plusDays(d)
      new LoginLinkRequest(encodedDateAddress: date.toString(), key: "456",
          emailAddress: "aaa@example.com", issuedDate: date)
    }
    linkRequestRepository.save(newEntities)

    linkRequestRepository.flush()

    when:
    def result = linkRequestRepository.deleteLoginLinkRequestsBefore(specificDate)
    linkRequestRepository.flush()

    then:
    afterDays  == linkRequestRepository.findAll().size()
    beforeDays == result

    where:
    beforeDays | afterDays
    3          | 4
    4          | 3
    27         | 56
    1          | 8
    9          | 0
    0          | 2
  }

  @Unroll
  def "delete entities with specified email #emailAddress"() {
    setup:
    def date = LocalDate.of(2013, 5, 12)

    def entities = (1..5).collect { i ->
      (0..<i).collect { j ->
        String id = "$i - $j"
        String emailAddress = "email${i}@example.com"
        new LoginLinkRequest(encodedDateAddress: id, key: "456",
            emailAddress: emailAddress, issuedDate: date)
      }
    }.flatten()
    linkRequestRepository.save(entities)
    linkRequestRepository.flush()

    when:
    def result = linkRequestRepository.deleteByEmailAddress(emailAddress)

    then:
    remaining == linkRequestRepository.findAll().size()
    result    == numOfDeleted

    where:
    emailAddress           | numOfDeleted | remaining
    "email1@example.com"   | 1            |     2 + 3 + 4 + 5
    "email2@example.com"   | 2            | 1 +     3 + 4 + 5
    "email4@example.com"   | 4            | 1 + 2 + 3 +     5
    "email5@example.com"   | 5            | 1 + 2 + 3 + 4
    "notexist@example.com" | 0            | 1 + 2 + 3 + 4 + 5
  }
}