package MacThenEncryptJCA.Mac;

import MacThenEncryptJCA.Mac.Abstractions.Mac;
import MacThenEncryptJCA.Model.AuthMessage;
import MacThenEncryptJCA.Model.MacThenEncryptResponse;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.GeneralSecurityException;
import java.util.Arrays;

public class MacImp implements Mac {
    @Override
    public AuthMessage macAuthenticate(String algorithm, byte[] message) throws GeneralSecurityException {
        // Applying HMAC to generate mark with K2
        KeyGenerator kg = KeyGenerator.getInstance(algorithm);
        SecretKey key = kg.generateKey();
        javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
        mac.init(key);
        byte[] mark = mac.doFinal(message);
        return new AuthMessage(addAll(mark, message), mark, mac);
    }

    @Override
    public boolean verify(MacThenEncryptResponse macResponse, byte[] msg) {
        javax.crypto.Mac mac = macResponse.getMac();
        byte[] verify = mac.doFinal(msg);
        return Arrays.equals(verify,macResponse.getAuthMark());
    }

    public static byte[] addAll(final byte[] array1, byte[] array2) {
        byte[] joinedArray = Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }
}
