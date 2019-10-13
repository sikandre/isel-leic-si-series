package HybridScheme.Cipher;

import HybridScheme.Models.InputArgs;
import HybridScheme.Models.Metadata;
import MacThenEncryptJCA.Model.CipherMessage;
import MacThenEncryptJCA.cipher.CipherImp;
import org.apache.commons.io.IOUtils;

import javax.crypto.Cipher;
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

    public CustomCipherImp(InputArgs inputArgs) {
        originalFile = getFileFromPath(inputArgs.getFilePath());
        certificate = getCertificateFile(inputArgs.getCertificate());
    }

    private X509Certificate getCertificateFile(String certificate) {
        X509Certificate cer = null;
        try {
            CertificateFactory fact = CertificateFactory.getInstance("X.509");
            FileInputStream fis = new FileInputStream("Serie1/src/InputFiles/"+certificate);
            cer = (X509Certificate) fact.generateCertificate(fis);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return cer;
    }

    private byte[] getFileFromPath(String fileName) {
        byte[] result = new byte[0];
        try {
            FileInputStream fis = new FileInputStream("Serie1/src/InputFiles/"+fileName);
            result = IOUtils.toByteArray(fis);
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
            KeyGenerator keyGenerator = KeyGenerator.getInstance(CIPHER_ALGORITHM.split("/")[0]);
            keyGenerator.init(128);
            SecretKey key = keyGenerator.generateKey();

            byte[] initialVector = new byte[INITIAL_VECTOR_SIZE];
            new Random().nextBytes(initialVector);
            IvParameterSpec initialVectorSpecifications = new IvParameterSpec(initialVector);

            javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init( Cipher.ENCRYPT_MODE, key, initialVectorSpecifications);
            byte[] encryptedOriginalFile = cipher.doFinal(originalFile);

            //cipher key with public key
            javax.crypto.Cipher c = javax.crypto.Cipher.getInstance("RSA/ECB/OAEPPadding");
            c.init(Cipher.PUBLIC_KEY, certificate.getPublicKey());
            byte[] encryptedKey = c.doFinal(key.getEncoded());
            metadata = new Metadata(initialVector, encryptedKey);

            Files.write(Paths.get("Serie1/src/OutputFiles/cipherFile"), encryptedOriginalFile);
            Files.write(Paths.get("Serie1/src/OutputFiles/metadata"), metadata.getMetadataAsBytes());

        } catch (GeneralSecurityException | IOException e) {
            System.out.println(e.getMessage());
            System.out.println("unable to cipher");
            return false;
        }
        return true;
    }
}
