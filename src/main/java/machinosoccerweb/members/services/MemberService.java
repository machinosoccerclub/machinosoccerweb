package machinosoccerweb.members.services;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import machinosoccerweb.ControllerUtils;
import machinosoccerweb.members.models.Course;
import machinosoccerweb.members.models.CourseAssignment;
import machinosoccerweb.members.models.Member;
import machinosoccerweb.members.models.MemberNumber;
import machinosoccerweb.members.repositories.MemberRepository;
import machinosoccerweb.security.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static machinosoccerweb.ControllerUtils.unauthorized;

@Service
@Slf4j
public class MemberService {
  private final MemberRepository memberRepository;
  private final CourseAssignment courseAssignment;

  private final Clock clock;

  @Autowired
  public MemberService(MemberRepository memberRepository, CourseAssignment courseAssignment,
                       Clock clock) {
    this.memberRepository = memberRepository;
    this.courseAssignment = courseAssignment;
    this.clock = clock;
  }

  public Member ensureAuthorizedMember(String memberNo, LoginUser loginUser)
      throws ControllerUtils.ResourceNotFoundException, ControllerUtils.UnauthorizedException {
    Member member = findMember(memberNo)
        .orElseThrow(ControllerUtils.ResourceNotFoundException::new);
    if (member.getFamilyId() != loginUser.getFamilyId()) {
      log.error("SECURITY NOTICE ** unauthorized ** : " +
              "loginUser:{} attempts uploading photo for the member: {}",
          loginUser, member);
      throw unauthorized();
    }

    return member;
  }

  public Optional<Member> findMember(String memberNo) {
    return Optional.ofNullable(memberRepository.findOne(memberNo));
  }

  public Member save(Member member) {
    if (member.getMemberNo() == null) {

      LocalDate joinedAt = ensureNonNullJoinedAtDate(member);
      member.setJoinedAt(joinedAt);

      MemberNumber memberNumber = createNewMemberNumber(member);
      member.setMemberNo(memberNumber.toString());

      Course course = courseAssignment.assign(member);
      member.setCourse(course);
    }

    return memberRepository.save(member);
  }

  private LocalDate ensureNonNullJoinedAtDate(Member member) {
    return Optional.ofNullable(member.getJoinedAt())
        .orElseGet(() -> LocalDate.now(clock));  // default to current date
  }

  private MemberNumber createNewMemberNumber(Member member) {
    long serial = memberRepository.getNextSerialNumber();
    MemberNumber number = MemberNumber.from(member.getJoinedAt(), serial);
    return number;
  }
}
