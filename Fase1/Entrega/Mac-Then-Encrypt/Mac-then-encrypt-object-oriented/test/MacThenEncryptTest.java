import cipher.Decrypter;
import cipher.Encrypter;
import cipher.Message;
import mac.Tag;
import mac.TagProducer;
import mac.Verifier;
import org.junit.jupiter.api.Test;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertTrue;


class MacThenEncryptTest {
    @Test
    public void shouldMacThenEncryptObjectOriented() throws InvalidKeyException, NoSuchAlgorithmException {
        byte[] msg = "The quick brown fox jumps over the lazy dog".getBytes(UTF_8);
        TagProducer mp = new TagProducer("HmacSHA256");
        Tag m = mp.produceMark(msg);

        Encrypter enc = new Encrypter("AES/CBC/PKCS5Padding");
        byte[] msgEncrypted = enc.encrypt(ArrayUtils.addAll(msg,m.mark));

        Decrypter dec = new Decrypter("AES/CBC/PKCS5Padding");
        Message message = dec.decrypt(ArrayUtils.addAll(msgEncrypted,m.mark));

        Verifier v = new Verifier("HmacSHA256");
        assertTrue(Arrays.equals(message.msg,msg));
        assertTrue(v.verify(message.msg,message.mark));
    }
}