package machinosoccerweb.members.models;

import java.time.LocalDate;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class MemberNumber {
  private final String memberNumber;

  private static final int Area = 1;

  private static final int Mod = 10;

  private static final Pattern NumberFormat = Pattern.compile("^\\d{7}$");

  private MemberNumber(String memberNumber) {
    this.memberNumber = memberNumber;
  }

  public static MemberNumber from(LocalDate joinedAt, long serial) {
    String number = String.format("%d%d%03d", Area, joinedAt.getYear() - 2000, serial);
    assert (number.length() == 6);

    int sum = sumUp(number);
    int checkDigit = Mod - (sum % Mod);

    return new MemberNumber(number + (checkDigit % Mod));
  }

  public static MemberNumber from(String strNumber) {
    if (strNumber == null) {
      throw new NullPointerException("the number must not be null");
    }

    if (!NumberFormat.matcher(strNumber).matches()) {
      throw new NumberFormatException("Invalid Number format:" + strNumber);
    }

    if (sumUp(strNumber) % Mod != 0) {
      throw new RuntimeException("invalid checksum:" + strNumber);
    }

    return new MemberNumber(strNumber);
  }

  private static int sumUp(String number) {
    return IntStream.range(0, number.length())
        .map(i -> calcDigit(i, number))
        .sum();
  }

  private static int calcDigit(int index, String number) {
    int num = parse(number.charAt(index));
    return index % 2 == 0 ? num : num * 2;
  }

  private static int parse(char ch) {
    return ch - '0';
  }

  @Override
  public String toString() {
    return memberNumber;
  }
}