package MacThenEncryptJCA;

import MacThenEncryptJCA.Mac.Abstractions.Mac;
import MacThenEncryptJCA.Mac.MacImp;
import MacThenEncryptJCA.Model.AuthMessage;
import MacThenEncryptJCA.Model.MacThenEncryptResponse;
import MacThenEncryptJCA.cipher.Abstractions.Cipher;
import MacThenEncryptJCA.cipher.CipherImp;

import java.security.GeneralSecurityException;
import java.util.Arrays;

import static java.nio.charset.StandardCharsets.UTF_8;

public class MacThenEncrypt {

        public static MacThenEncryptResponse macThenEncrypt(String txt) throws GeneralSecurityException {
        byte[] originalMsg = "The quick brown fox jumps over the lazy dog".getBytes(UTF_8);

        //authenticate
        Mac mac = new MacImp();
        String macAlgorithm = "HmacSHA256";
        AuthMessage authMessage = mac.macAuthenticate(macAlgorithm, originalMsg);

        //cipher
        String cipherAlgorithm = "AES/CBC/PKCS5Padding";
        Cipher cipher = new CipherImp();
        return new MacThenEncryptResponse(cipher.encryptUsingAES(authMessage.msg, cipherAlgorithm), authMessage.mark, authMessage.getMac());
    }

    public static boolean DecriptThenAuthenticate(MacThenEncryptResponse cipherMessage)throws GeneralSecurityException{
        String cipherAlgorithm = "AES/CBC/PKCS5Padding";
        Cipher cipher = new CipherImp();
        byte[] decryptMsg = cipher.decryptUsingAES(cipherMessage.getCipherMessage(), cipherAlgorithm);
        byte[] decryptedMsg = Arrays.copyOfRange(decryptMsg, cipherMessage.getAuthMark().length,decryptMsg.length);

        Mac mac = new MacImp();
        return mac.verify(cipherMessage, decryptedMsg);
    }

}
