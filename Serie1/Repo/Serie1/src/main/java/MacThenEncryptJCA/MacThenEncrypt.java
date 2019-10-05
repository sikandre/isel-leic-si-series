package MacThenEncryptJCA;

import MacThenEncryptJCA.Mac.Abstractions.Mac;
import MacThenEncryptJCA.Mac.MacImp;
import MacThenEncryptJCA.Model.AuthMessage;
import MacThenEncryptJCA.Model.MacThenEncryptResponse;
import MacThenEncryptJCA.cipher.Abstractions.Cipher;
import MacThenEncryptJCA.cipher.CipherImp;
import static java.nio.charset.StandardCharsets.UTF_8;


import java.security.GeneralSecurityException;
import java.util.Arrays;

public class MacThenEncrypt {

    public static MacThenEncryptResponse macThenEncrypt(String msg) throws GeneralSecurityException {
        byte[] originalMsg = msg.getBytes(UTF_8);

        //authenticate
        Mac mac = new MacImp();
        String macAlgorithm = "HmacSHA256";
        AuthMessage authMessage = mac.macAuthenticate(macAlgorithm, originalMsg);

        //cipher
        String cipherAlgorithm = "AES/CBC/PKCS5Padding";
        Cipher cipher = new CipherImp();
        return new MacThenEncryptResponse(cipher.encryptUsingAES(authMessage.msg, cipherAlgorithm), authMessage.getMac(), authMessage.getAuthMarkLen());
    }

    public static boolean DecriptThenAuthenticate(MacThenEncryptResponse cipherMessage) throws GeneralSecurityException {
        //decrypt
        String cipherAlgorithm = "AES/CBC/PKCS5Padding";
        Cipher cipher = new CipherImp();
        byte[] decryptMsg = cipher.decryptUsingAES(cipherMessage.getCipherMessage(), cipherAlgorithm);

        //authenticate
        byte[] authMsg = Arrays.copyOfRange(decryptMsg, cipherMessage.getMac().getMacLength(), decryptMsg.length);
        Mac mac = new MacImp();
        return mac.verify(cipherMessage, authMsg);
    }

}
