package MacThenEncryptJCA;
import MacThenEncryptJCA.Model.MacThenEncryptResponse;
import org.junit.Test;
import java.security.GeneralSecurityException;

import static junit.framework.TestCase.assertTrue;

public class MacThenEncryptTest {

    @Test
    public void macThenEncrypt() {
        String message = "The quick brown fox jumps over the lazy dog";
        try {
            MacThenEncryptResponse response = MacThenEncrypt.macThenEncrypt(message);
            assertTrue(MacThenEncrypt.DecriptThenAuthenticate(response));

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void decriptThenAuthenticate() {
    }
}