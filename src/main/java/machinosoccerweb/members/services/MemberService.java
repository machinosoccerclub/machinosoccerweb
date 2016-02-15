package machinosoccerweb.members.services;

import machinosoccerweb.members.models.Course;
import machinosoccerweb.members.models.CourseAssignment;
import machinosoccerweb.members.models.Member;
import machinosoccerweb.members.models.MemberNumber;
import machinosoccerweb.members.repositories.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
  private final MemberRepository memberRepository;

  private final CourseAssignment courseAssignment;

  @Autowired
  public MemberService(MemberRepository memberRepository, CourseAssignment courseAssignment) {
    this.memberRepository = memberRepository;
    this.courseAssignment = courseAssignment;
  }

  public Member save(Member member) {
    if (member.getMemberNo() == null) {
      long serial = memberRepository.getNextSerialNumber();
      MemberNumber number = MemberNumber.from(member.getJoinedAt(), serial);
      member.setMemberNo(number.toString());

      Course course = courseAssignment.assign(member);
      member.setCourse(course);
    }
    return memberRepository.save(member);
  }
}
