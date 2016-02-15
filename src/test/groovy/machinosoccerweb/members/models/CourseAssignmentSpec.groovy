package machinosoccerweb.members.models

import spock.lang.Specification
import spock.lang.Unroll

class CourseAssignmentSpec extends Specification {

  @Unroll
  def "preschool students: a member #gender and #grade should be assigned to #course"() {
    setup:
    def courseAssignment = new CourseAssignment("")

    when:
    def member = new Member(grade: grade, gender: gender)

    then:
    courseAssignment.assign(member) == course

    where:
    gender        | grade       || course
    Gender.Male   | Grade.Pres1 || Course.Pres1
    Gender.Female | Grade.Pres1 || Course.Pres1
    Gender.Female | Grade.Pres3 || Course.Pres3
  }

  @Unroll
  def "elementary school boys: a member #grade should be assigned to #course"() {
    setup:
    def courseAssignment = new CourseAssignment("")

    when:
    def member = new Member(grade: grade, gender: Gender.Male)

    then:
    courseAssignment.assign(member) == course

    where:
    grade       || course
    Grade.Elem1 || Course.Elem1
    Grade.Elem6 || Course.Elem6
  }


  @Unroll
  def "elementary girl named #familyName #givenName #grade should be assigned to #course"() {
    setup:
    def courseAssignment = new CourseAssignment(gamePlayerGirlsNameConfig)

    when:
    def member = new Member(familyNameKana: familyName, givenNameKana: givenName,
        grade: grade, gender: Gender.Female)

    then:
    courseAssignment.assign(member) == course

    where:
    gamePlayerGirlsNameConfig | familyName | givenName | grade       || course
    "おやまだはなこ;さわほまれ"  | "やまだ"    | "はなこ"   | Grade.Elem1 || Course.ElemF
    "やまだはなこ;さわほまれ"    | "おやまだ"  | "はなこ"   | Grade.Elem1 || Course.ElemF
    "やまだはなこ;さわほまれ"    | "やまだ"    | "はなこ"   | Grade.Elem3 || Course.Elem3
    "やまだはなこ;さわほまれ"    | "さわ"      | "ほまれ"   | Grade.Elem6 || Course.Elem6
  }
}