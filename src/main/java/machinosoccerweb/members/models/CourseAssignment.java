package machinosoccerweb.members.models;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CourseAssignment {

  private final List<String> gamePlayerGirlsNameList;

  @Autowired
  public CourseAssignment(
      @Value("machinosoccerweb.girlsPlayerNames") String gamePlayerGirlsNameConfig) {
    gamePlayerGirlsNameList = Arrays.asList(gamePlayerGirlsNameConfig.split(";"));
  }

  public Course assign(Member member) {
    switch (member.getGrade().getKind()) {
      case PreSchool:
        return assignPreschoolMember(member);
      case Elementary:
        return assignElementaryMember(member);
      default:
        throw new IllegalStateException("unknown grade kind:" + member.getGrade().getKind());
    }
  }

  private Course assignElementaryMember(Member member) {
    switch (member.getGender()) {
      case Male:
        return normalAssign(member.getGrade());
      case Female:
        if (!gamePlayerGirlsNameList.contains(concatenateName(member))) {
          return Course.ElemF;
        } else {
          return normalAssign(member.getGrade());
        }
      default:
        throw new IllegalStateException("unknown gender type:" + member.getGender());
    }

  }

  private Course assignPreschoolMember(Member member) {
    return normalAssign(member.getGrade());
  }

  private Course normalAssign(Grade grade) {
    return Course.valueOf(grade.name());
  }

  private String concatenateName(Member member) {
    return member.getFamilyNameKana() + member.getGivenNameKana();
  }
}
