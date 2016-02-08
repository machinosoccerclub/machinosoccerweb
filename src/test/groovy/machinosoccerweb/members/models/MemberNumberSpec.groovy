package machinosoccerweb.members.models

import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate;

class MemberNumberSpec extends Specification {

  @Unroll
  def "a member joined at #year / #month with number: #serial should have member no: #memberNo"() {
    expect:
    MemberNumber.from(LocalDate.of(year, month, 1), serial).toString() == memberNo

    where:
    year | month | serial || memberNo
    2015 | 1     | 0      || "1150002"
    2015 | 1     | 1      || "1150010"
    2015 | 2     | 1      || "1150010"
    2016 | 1     | 0      || "1160001"
    2099 | 12    | 0      || "1990002"
    2015 | 1     | 40     || "1150408"
    2015 | 1     | 729    || "1157298"
    2099 | 12    | 999    || "1999997"
    2013 | 5     | 000    || "1130004"
    2023 | 12    | 456    || "1234567"
  }

  @Unroll
  def "valid number: #memberNo should be instantiated properly"() {
    expect:
    MemberNumber.from(memberNo).toString() == memberNo

    where:
    memberNo  | dummy
    "1150002" | 0
    "9999999" | 0
    "4848480" | 0
    "2727272" | 0
    "4505569" | 0
    "1111111" | 0
  }

  @Unroll
  def "invalid number: #invalidMemberNo should get #exception because of #reason"() {
    when:
    MemberNumber.from(invalidMemberNo)

    then:
    thrown exception

    where:
    invalidMemberNo | exception             | reason
    "1150003"       | RuntimeException      | "invalid check sum"
    "115000a"       | NumberFormatException | "containing an alphabetical character"
    "11500021"      | NumberFormatException | "not 7 digits"
    "115000"        | NumberFormatException | "not 7 digits"
    null            | NullPointerException  | "number string is null"
  }
}