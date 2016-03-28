package machinosoccerweb.members.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Grade {
  Parent("保護者", GradeKind.Parent),
  Elem6("小６", GradeKind.Elementary),
  Elem5("小５", GradeKind.Elementary),
  Elem4("小４", GradeKind.Elementary),
  Elem3("小３", GradeKind.Elementary),
  Elem2("小２", GradeKind.Elementary),
  Elem1("小１", GradeKind.Elementary),
  Pres3("年長", GradeKind.PreSchool),
  Pres2("年中", GradeKind.PreSchool),
  Pres1("年少", GradeKind.PreSchool);

  private final String label;
  private final GradeKind kind;

  public enum GradeKind {
    Parent, Elementary, PreSchool,
  }
}
