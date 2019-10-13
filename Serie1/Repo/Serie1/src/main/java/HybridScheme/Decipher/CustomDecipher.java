package HybridScheme.Decipher;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface CustomDecipher {
    boolean decipherMessage() throws GeneralSecurityException, IOException;
}
