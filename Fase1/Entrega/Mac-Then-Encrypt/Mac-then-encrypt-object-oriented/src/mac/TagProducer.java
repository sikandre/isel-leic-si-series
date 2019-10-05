package mac;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class TagProducer {
    public Mac mac;
    public TagProducer(String algorithm) throws NoSuchAlgorithmException, InvalidKeyException {
        KeyGenerator kg = KeyGenerator.getInstance(algorithm);
        SecretKey k2 = kg.generateKey();
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(k2);

    }

    public Tag produceMark(byte[] msg) {
        return new Tag(mac.doFinal(msg));
    }
}
