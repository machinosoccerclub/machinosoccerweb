package machinosoccerweb.members.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Gender {
  Male("男"),
  Female("女");

  private final String label;
}
