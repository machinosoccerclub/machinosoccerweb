import machinosoccerweb.members.repositories.ParentRepository
import machinosoccerweb.members.models.Parent
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ParentRepositoryTest {
  private ParentRepository repository

  @Before
  public void setup() {
    repository = new ParentRepository()
  }

  @Test
  public void add() {
    def parent = new Parent(givenName: "giv", familyName: "fam")

    def savedParent = repository.save(parent)
    Assert.assertNotNull(savedParent.familyId)
  }


  @Test
  public void findOne() {
    def parent = new Parent(familyId: 34987L)
    repository.save(parent)

    def found = repository.findOne(34987L)

    Assert.assertNotNull(found)
  }

    @Test
  public void findById() {
    def parent = new Parent(familyId: 34987L)
    repository.save(parent)

    def found = repository.findByFamilyId(34987L)

    Assert.assertNotNull(found)
  }
}