package machinosoccerweb.members.repositories;

import java.util.Arrays;
import java.util.List;

import machinosoccerweb.infra.TempCollectionRepository;
import machinosoccerweb.members.models.Parent;
import org.springframework.stereotype.Repository;

@Repository
public class ParentRepository extends TempCollectionRepository<Parent, Long> {
  private static final List<Parent> initialEntities = initParents();

  public ParentRepository() {
    super(initialEntities);
  }

  @Override
  protected Long convert(long newId) {
    return Long.valueOf(newId);
  }

  private static List<Parent> initParents() {
    Parent parent = new Parent();
    parent.setFamilyId(Long.valueOf("user".hashCode()));
    parent.setFamilyName("鈴木");
    parent.setFamilyNameKana("すずき");
    parent.setGivenName("一朗");
    parent.setGivenNameKana("いちろう");
    parent.setPhoneNumber1("012-3456-7890");

    return Arrays.asList(parent);
  }
}
