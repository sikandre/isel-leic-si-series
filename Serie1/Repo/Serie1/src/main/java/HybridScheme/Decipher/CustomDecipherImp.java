package HybridScheme.Decipher;

import HybridScheme.Models.InputArgs;
import HybridScheme.Models.Metadata;
import org.apache.commons.io.IOUtils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.Enumeration;

public class CustomDecipherImp implements CustomDecipher {
    private static final int INITIAL_VECTOR_SIZE = 16;
    private final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String PASSWORD = "changeit";
    private byte[] metadata;
    private byte[] encriptFile;
    private KeyStore keyStore;  // File with private key

    public CustomDecipherImp(InputArgs inputArgs) {
        encriptFile = getFileFromPath(inputArgs.getEncryptedFilePath());
        metadata = getFileFromPath(inputArgs.getMetadataPath());
        keyStore = getKeyStoreFromPath(inputArgs.getKeyStoreFilePath());

    }

    @Override
    public boolean decipherMessage() {
        try {
            Key key = keyStore.getKey("1", PASSWORD.toCharArray());

            byte[] iv = Arrays.copyOfRange(metadata, 0,INITIAL_VECTOR_SIZE);
            byte[] encryptedKey = Arrays.copyOfRange(metadata, INITIAL_VECTOR_SIZE, metadata.length);

            Cipher decipher = Cipher.getInstance("RSA/ECB/OAEPPadding");
            decipher.init(Cipher.PRIVATE_KEY, key);
            byte[] decripedKey = decipher.doFinal(encryptedKey);    //key to decipher file

            SecretKeySpec secretKeySpec = new SecretKeySpec(decripedKey, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            Cipher finalDecipher = Cipher.getInstance(CIPHER_ALGORITHM);
            finalDecipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] decripedFile = finalDecipher.doFinal(encriptFile);

            Files.write(Paths.get("Serie1/src/OutputFiles/File.pdf"), decripedFile);
        } catch (GeneralSecurityException | IOException e) {
            System.out.println("Unable to Decipher");
            System.out.println(e.getMessage());
            return false;
        }
        return true;

    }

    private KeyStore getKeyStoreFromPath(String keyStoreFilePath) {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            FileInputStream fis = new FileInputStream("Serie1/src/InputFiles/"+keyStoreFilePath);
            keyStore.load(fis, PASSWORD.toCharArray());
            return keyStore;
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private byte[] getFileFromPath(String fileName) {
        try {
            FileInputStream is = new FileInputStream("Serie1/src/OutputFiles/"+fileName);
            return IOUtils.toByteArray(is);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}