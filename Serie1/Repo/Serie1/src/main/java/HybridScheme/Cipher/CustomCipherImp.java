package HybridScheme.Cipher;

import HybridScheme.Models.InputArgs;
import HybridScheme.Models.Metadata;
import MacThenEncryptJCA.Model.CipherMessage;
import MacThenEncryptJCA.cipher.Abstractions.Cipher;
import MacThenEncryptJCA.cipher.CipherImp;
import org.apache.commons.io.IOUtils;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Random;

public class CustomCipherImp implements CustomCipher {
    private static final int INITIAL_VECTOR_SIZE = 16;
    private final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";

    private byte[] originalFile;
    private X509Certificate certificate;
    private Metadata metadata;
    private byte[] cipherMessage;

    private InputArgs inputArgs;

    public CustomCipherImp(InputArgs inputArgs) {
        this.inputArgs = inputArgs;
        originalFile = getFileFromPath(inputArgs.getFilePath());
        certificate = getCertificateFile(inputArgs.getCertificate());
    }

    private X509Certificate getCertificateFile(String certificate) {
        X509Certificate cer = null;
        try {
            CertificateFactory fact = CertificateFactory.getInstance("X.509");
            FileInputStream fis = new FileInputStream("Serie1/src/main/resources/"+certificate);
            cer = (X509Certificate) fact.generateCertificate(fis);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return cer;
    }

    private byte[] getFileFromPath(String fileName) {
        byte[] result = new byte[0];
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream is = classloader.getResourceAsStream(fileName);
            result = IOUtils.toByteArray(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private byte[] generateInitialVector() {
        byte[] initialVector = new byte[INITIAL_VECTOR_SIZE];
        new Random().nextBytes(initialVector);
        return initialVector;
    }

    private boolean validateCertificate(X509Certificate cert) {
        return false;
    }

    @Override
    public boolean CipherMessage() {
        try {
            //using the same class to cipher from ex:8
            Cipher cipher = new CipherImp();
            CipherMessage cipherMessage = cipher.encryptUsingAES(originalFile, CIPHER_ALGORITHM);
            //cipher key with public key
            javax.crypto.Cipher c = javax.crypto.Cipher.getInstance("RSA");
            c.init(javax.crypto.Cipher.ENCRYPT_MODE, certificate);
            byte[] encryptedKey = c.doFinal(cipherMessage.key.getEncoded());
            metadata = new Metadata(cipherMessage.initialVector, encryptedKey);

            Files.write(Paths.get("cipherFile"), cipherMessage.msg);
            Files.write(Paths.get("metadata"), metadata.getMetadataAsBytes());

        } catch (GeneralSecurityException | IOException e) {
            System.out.println(e.getMessage());
            System.out.println("unable to cipher");
            return false;
        }
        return true;
    }
}
