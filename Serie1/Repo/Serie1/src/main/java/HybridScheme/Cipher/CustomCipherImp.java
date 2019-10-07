package HybridScheme.Cipher;

import HybridScheme.Models.InputArgs;
import HybridScheme.Models.Metadata;
import org.apache.commons.io.IOUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Random;

public class CustomCipherImp implements CustomCipher {
    private static final int INITIAL_VECTOR_SIZE = 16;

    private byte[] originalFile;
    private byte[] certificateFile;
    private Metadata metadata;
    private byte[] cipherMessage;

    private InputArgs inputArgs;

    public CustomCipherImp(InputArgs inputArgs) {
        this.inputArgs = inputArgs;
        originalFile = getFileFromPath(inputArgs.getFilePath());
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
}
