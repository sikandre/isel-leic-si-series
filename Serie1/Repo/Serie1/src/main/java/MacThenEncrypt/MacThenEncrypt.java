package MacThenEncrypt;

import MacThenEncrypt.Exceptions.DecryptionException;
import MacThenEncrypt.model.KeyIVAndMsg;
import MacThenEncrypt.model.MacAndMark;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.security.*;
import java.util.Arrays;


import static java.nio.charset.StandardCharsets.UTF_8;

public class MacThenEncrypt {

    public static boolean macThenEncrypt(String msgStr) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException, IOException, InvalidAlgorithmParameterException, DecryptionException, SignatureException {
        byte[] originalMsg = "The quick brown fox jumps over the lazy dog".getBytes(UTF_8);
        String macAlgorithm = "HmacSHA256";
        MacAndMark kam = MacThenEncrypt.mac(macAlgorithm, originalMsg);

        byte[] msgAndMark = addAll(kam.mark,originalMsg);

        String cipherAlgorithm = "AES/CBC/PKCS5Padding";
        KeyIVAndMsg kiam = MacThenEncrypt.encryptUsingAES(cipherAlgorithm, msgAndMark);
        byte[] decrypted = MacThenEncrypt.decryptUsingAES(cipherAlgorithm, kiam);

        byte[] decryptedMark = Arrays.copyOfRange(decrypted, 0, kam.mark.length);
        byte[] decryptedMsg = Arrays.copyOfRange(decrypted, kam.mark.length,decrypted.length);

        return MacThenEncrypt.verify(kam,decryptedMsg);
    }


    public static MacAndMark mac(String algorithm, byte[] msg) throws NoSuchAlgorithmException, InvalidKeyException {

        // Applying HMAC to generate mark with K2
        KeyGenerator kg = KeyGenerator.getInstance(algorithm);
        SecretKey k2 = kg.generateKey();
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(k2);
        byte[] mark = mac.doFinal(msg);

        return new MacAndMark(mac,mark);
    }
    public static boolean verify(MacAndMark kam, byte[] originalMsg) {
        Mac mac = kam.mac;
        byte[] verif = mac.doFinal(originalMsg);
        return Arrays.equals(verif,kam.mark);
    }

    public static KeyIVAndMsg encryptUsingAES(String algorithm, byte[] msg) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        KeyGenerator kg2 = KeyGenerator.getInstance(algorithm.split("/")[0]);
        SecretKey k1 = kg2.generateKey();
        //TODO generate iv randomly
        byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        IvParameterSpec ivspec = new IvParameterSpec(iv);

        // Encrypting message
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init( Cipher.ENCRYPT_MODE, k1, ivspec);
        byte[] encrypted = cipher.doFinal(msg);

        return new KeyIVAndMsg(k1,iv,encrypted);
    }

    public static byte[] decryptUsingAES(String algorithm, KeyIVAndMsg kiam) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        SecretKey key = kiam.key;
        byte[] iv = kiam.iv;
        byte[] msg = kiam.msg;
        IvParameterSpec ivspec = new IvParameterSpec(iv);

        Cipher decipher = Cipher.getInstance(algorithm);
        decipher.init(Cipher.DECRYPT_MODE, key, ivspec);
        byte[] decryptedMsg = decipher.doFinal(msg);

        return decryptedMsg;
    }

    public static byte[] addAll(final byte[] array1, byte[] array2) {
        byte[] joinedArray = Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

}
