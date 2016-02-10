package machinosoccerweb.infra;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.Arrays;
import java.util.stream.IntStream;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;


@Component
public class HmacUtils {
  private static final String ALGORITHM_NAME = "HmacSHA256";

  public Mac createHmacSha256(String phrase) {
    try {
      Mac mac = Mac.getInstance(ALGORITHM_NAME);
      Key key = new SecretKeySpec(phrase.getBytes(), ALGORITHM_NAME);
      mac.init(key);
      return mac;
    } catch (GeneralSecurityException securityException) {
      throw new IllegalStateException(securityException);
    }
  }

  public byte[] calcHash(Mac hmac, byte[] input) {
    return hmac.doFinal(input);
  }

  public IntStream calcHashToIntStream(Mac hmac, byte[] input) {
    byte[] hash = calcHash(hmac, input);
    int[] intHash = new int[hash.length];
    for (int i = 0; i < intHash.length; i++) {
      intHash[i] = hash[i] & 0xff;
    }

    return Arrays.stream(intHash);
  }

  public String calcHashToHexString(Mac hmac, byte[] input) {
    IntStream hash = calcHashToIntStream(hmac, input);

    return hash.mapToObj(b -> String.format("%02x", b))
        .reduce("", (s1, s2) -> s1 + s2);
  }
}
