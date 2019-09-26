package MacThenEncryptJCA.mac;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class MarkProducer {
    public Mac mac;
    public MarkProducer(String algorithm) throws NoSuchAlgorithmException, InvalidKeyException {
        KeyGenerator kg = KeyGenerator.getInstance(algorithm);
        SecretKey k2 = kg.generateKey();
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(k2);

    }

    public Mark produceMark(byte[] msg) {
        return new Mark(mac.doFinal(msg));
    }
}
