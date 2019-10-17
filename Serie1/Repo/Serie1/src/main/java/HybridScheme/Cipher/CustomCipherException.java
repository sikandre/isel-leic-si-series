package HybridScheme.Cipher;

import java.security.GeneralSecurityException;

public class CustomCipherException extends Exception {
    private String message;

    public CustomCipherException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
