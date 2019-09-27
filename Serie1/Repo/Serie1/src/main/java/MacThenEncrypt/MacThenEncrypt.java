package MacThenEncrypt;

import MacThenEncrypt.Exceptions.DecryptionException;
import MacThenEncrypt.model.KeyIVAndMsg;
import MacThenEncrypt.model.MacAndMark;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.security.*;
import java.util.Arrays;
import java.util.Random;


import static java.nio.charset.StandardCharsets.UTF_8;

public class MacThenEncrypt {

    private static final int INITIAL_VECTOR_SIZE = 16;

    public static boolean macThenEncrypt(String msgStr) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException, IOException, InvalidAlgorithmParameterException, DecryptionException, SignatureException {
        byte[] originalMsg = "The quick brown fox jumps over the lazy dog".getBytes(UTF_8);
        String macAlgorithm = "HmacSHA256";
        MacAndMark keyAndMsg = MacThenEncrypt.mac(macAlgorithm, originalMsg);

        byte[] msgAndMark = addAll(keyAndMsg.mark,originalMsg);

        String cipherAlgorithm = "AES/CBC/PKCS5Padding";
        KeyIVAndMsg keyInitialVectorAndMessage = MacThenEncrypt.encryptUsingAES(cipherAlgorithm, msgAndMark);
        byte[] decrypted = MacThenEncrypt.decryptUsingAES(cipherAlgorithm, keyInitialVectorAndMessage);

        byte[] decryptedMark = Arrays.copyOfRange(decrypted, 0, keyAndMsg.mark.length);
        byte[] decryptedMsg = Arrays.copyOfRange(decrypted, keyAndMsg.mark.length,decrypted.length);

        return MacThenEncrypt.verify(keyAndMsg,decryptedMsg);
    }

    public static MacAndMark mac(String algorithm, byte[] msg) throws NoSuchAlgorithmException, InvalidKeyException {

        // Applying HMAC to generate mark with K2
        KeyGenerator kg = KeyGenerator.getInstance(algorithm);
        SecretKey key = kg.generateKey();
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(key);
        byte[] mark = mac.doFinal(msg);

        return new MacAndMark(mac,mark);
    }

    public static boolean verify(MacAndMark kam, byte[] originalMsg) {
        Mac mac = kam.mac;
        byte[] verify = mac.doFinal(originalMsg);
        return Arrays.equals(verify,kam.mark);
    }

    public static KeyIVAndMsg encryptUsingAES(String algorithm, byte[] msg) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm.split("/")[0]);
        SecretKey k1 = keyGenerator.generateKey();

        byte[] initialVector = new byte[INITIAL_VECTOR_SIZE];
        new Random().nextBytes(initialVector);

        IvParameterSpec initialVectorSpecifications = new IvParameterSpec(initialVector);

        // Encrypting message
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init( Cipher.ENCRYPT_MODE, k1, initialVectorSpecifications);
        byte[] encrypted = cipher.doFinal(msg);

        return new KeyIVAndMsg(k1,initialVector,encrypted);
    }

    public static byte[] decryptUsingAES(String algorithm, KeyIVAndMsg keyInitialVectorAndMessage) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        SecretKey key = keyInitialVectorAndMessage.key;
        byte[] iv = keyInitialVectorAndMessage.initialVector;
        byte[] msg = keyInitialVectorAndMessage.msg;
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
