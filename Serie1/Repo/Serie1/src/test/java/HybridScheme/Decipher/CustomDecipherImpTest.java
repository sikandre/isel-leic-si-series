package HybridScheme.Decipher;

import HybridScheme.Models.InputArgs;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class CustomDecipherImpTest {

    @Test
    public void decipherMessage() throws GeneralSecurityException, IOException {
        InputArgs inputArgs = new InputArgs(
                null,
                false,
                null,
                true,
                "metadata",
                "cipherFile",
                "pfx/Alice_1.pfx");

        CustomDecipher decipher = new CustomDecipherImp(inputArgs);
        boolean result = decipher.decipherMessage();
        Assert.assertTrue(result);
    }
}