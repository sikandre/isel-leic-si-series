package MacThenEncryptJCA.Mac.Abstractions;

import MacThenEncryptJCA.Model.AuthMessage;
import MacThenEncryptJCA.Model.MacThenEncryptResponse;

import java.security.GeneralSecurityException;

public interface Mac {

    AuthMessage macAuthenticate(String macAlgorithm, byte[] message) throws GeneralSecurityException;

    boolean verify(MacThenEncryptResponse mac, byte[] msg);
}
