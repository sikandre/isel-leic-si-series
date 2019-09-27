import MacThenEncrypt.Exceptions.DecryptionException;
import MacThenEncrypt.MacThenEncrypt;
import MacThenEncrypt.model.KeyIVAndMsg;
import MacThenEncrypt.model.MacAndMark;
import org.junit.jupiter.api.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Arrays;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MacThenEncryptTest {
    @Test
    public void shouldMacAndVerify() throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] originalMsg = "The quick brown fox jumps over the lazy dog".getBytes(UTF_8);
        String algorithm = "HmacSHA256";
        MacAndMark kam = MacThenEncrypt.mac(algorithm, originalMsg);
        assertTrue(MacThenEncrypt.verify(kam,originalMsg));
    }
    @Test
    public void shouldEncryptUsingAES_with_CBC_and_PKCS5Padding() throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        byte[] msg = "The quick brown fox jumps over the lazy dog".getBytes(UTF_8);
        String algorithm = "AES/CBC/PKCS5Padding";
        KeyIVAndMsg kiam = MacThenEncrypt.encryptUsingAES(algorithm, msg);
        byte[] decrypted = MacThenEncrypt.decryptUsingAES(algorithm, kiam);
        assertTrue(Arrays.equals(msg,decrypted));
    }

    @Test
    public void shouldMacThenEncrypt() throws IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, InvalidAlgorithmParameterException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException, DecryptionException {
        assertTrue(MacThenEncrypt.macThenEncrypt("Texto em claro"));
    }
}