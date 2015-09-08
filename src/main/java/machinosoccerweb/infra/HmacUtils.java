package machinosoccerweb.infra;

import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.Arrays;
import java.util.stream.IntStream;

@Service
public class HmacUtils {
    private static final String ALGORITHM_NAME = "HmacSHA256";

    public Mac createHmacSHA256(String phrase) throws GeneralSecurityException {
        Mac mac = Mac.getInstance(ALGORITHM_NAME);
        Key key = new SecretKeySpec(phrase.getBytes(), ALGORITHM_NAME);
        mac.init(key);

        return mac;
    }

    public byte[] calcHash(Mac hmac, byte[] input) {
        return hmac.doFinal(input);
    }

    public IntStream calcHashToIntStream(Mac hmac, byte[] input) {
        byte[] hash = calcHash(hmac, input);
        int[] intHash = new int[hash.length];
        for(int i=0; i<intHash.length; i++) {
            intHash[i] = hash[i] & 0xff;
        }

        return Arrays.stream(intHash);
    }

    public String calcHashToHexString(Mac hmac, byte[] input) {
        IntStream hash = calcHashToIntStream(hmac, input);

        return hash.mapToObj( b -> String.format("%02x", b) )
                .reduce("", (s1, s2) -> s1 + s2);
    }
}
