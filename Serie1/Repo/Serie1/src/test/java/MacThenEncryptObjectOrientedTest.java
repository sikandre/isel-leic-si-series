import MacThenEncryptObjectOriented.ArrayUtils;
import MacThenEncryptObjectOriented.cipher.Decrypter;
import MacThenEncryptObjectOriented.cipher.Encrypter;
import MacThenEncryptObjectOriented.cipher.Message;
import MacThenEncryptObjectOriented.mac.Mark;
import MacThenEncryptObjectOriented.mac.MarkProducer;
import MacThenEncryptObjectOriented.mac.Verifier;
import org.junit.jupiter.api.Test;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertTrue;


class MacThenEncryptObjectOrientedTest {
    @Test
    public void shouldMacThenEncryptObjectOriented() throws InvalidKeyException, NoSuchAlgorithmException {
        byte[] msg = "The quick brown fox jumps over the lazy dog".getBytes(UTF_8);
        MarkProducer mp = new MarkProducer("HmacSHA256");
        Mark m = mp.produceMark(msg);

        Encrypter enc = new Encrypter("AES/CBC/PKCS5Padding");
        byte[] msgEncrypted = enc.encrypt(ArrayUtils.addAll(msg,m.mark));

        Decrypter dec = new Decrypter("AES/CBC/PKCS5Padding");
        Message message = dec.decrypt(ArrayUtils.addAll(msgEncrypted,m.mark));

        Verifier v = new Verifier("HmacSHA256");
        assertTrue(Arrays.equals(message.msg,msg));
        assertTrue(v.verify(message.msg,message.mark));
    }
}