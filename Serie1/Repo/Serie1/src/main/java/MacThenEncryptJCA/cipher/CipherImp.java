package MacThenEncryptJCA.cipher;

import MacThenEncrypt.model.KeyIVAndMsg;
import MacThenEncryptJCA.Model.CipherMessage;
import MacThenEncryptJCA.cipher.Abstractions.Cipher;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.GeneralSecurityException;
import java.util.Random;

public class CipherImp implements Cipher {

    private static final int INITIAL_VECTOR_SIZE = 16;

    public CipherMessage encryptUsingAES(byte[] msg, String algorithm) throws GeneralSecurityException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm.split("/")[0]);
        SecretKey key = keyGenerator.generateKey();

        byte[] initialVector = new byte[INITIAL_VECTOR_SIZE];
        new Random().nextBytes(initialVector);

        IvParameterSpec initialVectorSpecifications = new IvParameterSpec(initialVector);

        // Encrypting message
        javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance(algorithm);
        cipher.init( javax.crypto.Cipher.ENCRYPT_MODE, key, initialVectorSpecifications);
        byte[] encrypted = cipher.doFinal(msg);

        return new CipherMessage(key,initialVector,encrypted);
    }



    @Override
    public byte[] decryptUsingAES (CipherMessage cipherMessage, String cipherAlgorithm) throws GeneralSecurityException{
        SecretKey key = cipherMessage.key;
        byte[] iv = cipherMessage.initialVector;
        byte[] msg = cipherMessage.msg;
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        javax.crypto.Cipher decipher = javax.crypto.Cipher.getInstance(cipherAlgorithm);
        decipher.init(javax.crypto.Cipher.DECRYPT_MODE, key, ivParameterSpec);
        byte[] decryptedMsg = decipher.doFinal(msg);

        return decryptedMsg;
    }
}
