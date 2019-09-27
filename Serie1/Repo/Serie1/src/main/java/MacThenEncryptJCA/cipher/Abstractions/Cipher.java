package MacThenEncryptJCA.cipher.Abstractions;

import MacThenEncryptJCA.Model.CipherMessage;

import java.security.GeneralSecurityException;

public interface Cipher {

    CipherMessage encryptUsingAES(byte[] msg, String algorithm) throws GeneralSecurityException;

    byte[] decryptUsingAES(CipherMessage cipherMessage, String cipherAlgorithm) throws GeneralSecurityException;
}
