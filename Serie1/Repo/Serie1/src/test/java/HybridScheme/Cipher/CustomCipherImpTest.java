package HybridScheme.Cipher;

import HybridScheme.Models.InputArgs;
import org.junit.Assert;
import org.junit.Test;

public class CustomCipherImpTest {

    @Test
    public void cipherMessage() {
        InputArgs inputArgs = new InputArgs(
                "serie1-1920i.pdf",
                true,
                "cert-CA/CA1.cer",
                false,
                null,
                null,
                null);

        CustomCipher cipher = new CustomCipherImp(inputArgs);
        boolean result = cipher.CipherMessage();
        Assert.assertTrue(result);
    }
}