package machinosoccerweb.members.services;

import machinosoccerweb.members.models.Member;
import machinosoccerweb.members.repositories.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
  private final MemberRepository memberRepository;

  @Autowired
  public MemberService(MemberRepository memberRepository) {
    this.memberRepository = memberRepository;
  }

  public Member save(Member member) {
    // TODO: fill memberNo if it is blank (in case of adding a new member)
    return memberRepository.save(member);
  }
}
