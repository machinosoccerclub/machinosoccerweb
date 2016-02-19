package machinosoccerweb.demo;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import machinosoccerweb.login.models.LoginLinkRequest;
import machinosoccerweb.login.repositories.LoginLinkRequestRepository;
import machinosoccerweb.login.services.LoginLinkService;
import machinosoccerweb.members.models.Account;
import machinosoccerweb.members.models.Gender;
import machinosoccerweb.members.models.Grade;
import machinosoccerweb.members.models.Member;
import machinosoccerweb.members.models.Parent;
import machinosoccerweb.members.repositories.AccountRepository;
import machinosoccerweb.members.repositories.ParentRepository;
import machinosoccerweb.members.services.MemberService;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Profile("demo")
@Controller
@Slf4j
public class DemoController {

  private final LoginLinkRequestRepository loginLinkRequestRepository;
  private final LoginLinkService loginLinkService;
  private final AccountRepository accountRepository;
  private final ParentRepository parentRepository;
  private final MemberService memberService;

  @Autowired
  public DemoController(LoginLinkRequestRepository loginLinkRequestRepository,
                        LoginLinkService loginLinkService,
                        AccountRepository accountRepository,
                        ParentRepository parentRepository,
                        MemberService memberService) {
    this.loginLinkRequestRepository = loginLinkRequestRepository;
    this.loginLinkService = loginLinkService;
    this.accountRepository = accountRepository;
    this.parentRepository = parentRepository;
    this.memberService = memberService;
  }

  @RequestMapping(value = "demo", method = RequestMethod.GET)
  public String demo(@ModelAttribute DemoForm model) {
    return "demo/demo";
  }

  @RequestMapping(value = "demo", method = RequestMethod.POST)
  public String demoLogin(@Validated DemoForm model, BindingResult result)
      throws UnsupportedEncodingException {
    if (result.hasErrors()) {
      return "demo/demo";
    }

    log.debug("handling demo login * * * * : {}", model);

    LoginLinkRequest loginLinkRequest =
        loginLinkService.createLoginLinkRequest(model.getEmail());
    loginLinkRequestRepository.save(loginLinkRequest);

    if (model.isDataPopulation()) {
      populateDemoData(model.getEmail());
    }

    String path = String.format("/emailConf?a=%s&k=%s",
        URLEncoder.encode(loginLinkRequest.getEncodedDateAddress(), "utf-8"),
        loginLinkRequest.getKey());

    return "redirect:" + path;
  }

  private void populateDemoData(String email) {
    log.debug("trying populate data for: {}", email);

    Account account = accountRepository.findOne(email);
    if (account != null) {
      return;
    }

    Parent parent = parentRepository.save(newParent());

    account = new Account(email, true, true, parent.getFamilyId(), Account.Status
        .AddressConfirmed, "users");
    accountRepository.save(account);

    memberService.save(newMember(parent.getFamilyId()));
  }

  private Member newMember(long familyId) {
    Member member = naming(new Member());
    member.setGrade(random(Grade.class));
    member.setGender(random(Gender.class));

    member.setFamilyId(familyId);

    return member;
  }

  private Parent newParent() {
    Parent parent = naming(new Parent());
    parent.setPhoneNumber1("012-3456-7890");

    return parent;
  }

  private <T> T naming(T person) {
    DemoFamilyName fName = random(fNames);
    DemoGivenName gName = random(gNames);
    BeanUtils.copyProperties(fName, person);
    BeanUtils.copyProperties(gName, person);

    return person;
  }

  private <T extends Enum<T>> T random(Class<T> enumDef) {
    T[] values = enumDef.getEnumConstants();
    long index = System.nanoTime() % (long) values.length;
    return values[(int) index];
  }

  private <T> T random(List<T> list) {
    long index = System.nanoTime() % (long) list.size();
    return list.get((int) index);
  }

  private final List<DemoFamilyName> fNames
      = Arrays.asList(new DemoFamilyName[]{
        new DemoFamilyName("街野", "マチノ"),
        new DemoFamilyName("街坂", "マチサカ"),
        new DemoFamilyName("本郷", "ホンゴウ"),
        new DemoFamilyName("澤", "サワ"),
        new DemoFamilyName("メッシ", "メッシ")
      });

  private final List<DemoGivenName> gNames
      = Arrays.asList(new DemoGivenName[]{
        new DemoGivenName("栄男", "サカオ"),
        new DemoGivenName("蹴男", "ケリオ"),
        new DemoGivenName("穂希", "ホマレ"),
        new DemoGivenName("リオネル", "リオネル"),
        new DemoGivenName("太郎", "タロウ"),
        new DemoGivenName("花子", "ハナコ"),
        new DemoGivenName("一郎", "イチロウ"),
        new DemoGivenName("二郎", "ジロウ"),
        new DemoGivenName("三郎", "サブロウ"),
        new DemoGivenName("四郎", "シロウ"),
      });

  @AllArgsConstructor
  @Data
  private class DemoFamilyName {
    private String familyName;
    private String familyNameKana;
  }

  @AllArgsConstructor
  @Data
  private class DemoGivenName {
    private String givenName;
    private String givenNameKana;
  }

  @Data
  public static class DemoForm {
    @NotBlank
    private String email;

    private boolean dataPopulation;
  }
}
