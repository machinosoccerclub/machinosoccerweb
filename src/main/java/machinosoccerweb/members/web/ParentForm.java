package machinosoccerweb.members.web;

import javax.validation.constraints.Pattern;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class ParentForm {
  @NotBlank
  private String givenName;
  @NotBlank
  private String familyName;
  @NotBlank
  @Pattern(regexp = "[ァ-ヶー]*", message = "{Katakana.message}")
  private String givenNameKana;
  @NotBlank
  @Pattern(regexp = "[ァ-ヶー]*", message = "{Katakana.message}")
  private String familyNameKana;

  @NotBlank
  @Pattern(regexp = "[\\-0-9]*", message = "{PhoneNumber.message}")
  private String phoneNumber1;
  @Pattern(regexp = "[\\-0-9]*", message = "{PhoneNumber.message}")
  private String phoneNumber2;

  private boolean isBlank;
}
