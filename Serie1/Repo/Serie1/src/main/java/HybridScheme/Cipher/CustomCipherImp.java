package HybridScheme.Cipher;

import HybridScheme.Models.InputArgs;
import HybridScheme.Models.Metadata;
import org.apache.commons.io.IOUtils;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class CustomCipherImp implements CustomCipher {
    private byte[] originalFile;
    private byte[] certificateFile;
    private Metadata metadata;
    private byte[] cipherMessage;

    public CustomCipherImp(InputArgs inputArgs) {
        originalFile = getFileFromPath(inputArgs.getFilePath());
        getCertificateFile(inputArgs.getCertificate());
    }

    private void getCertificateFile(String certificate) {
        try {
            CertificateFactory fact = CertificateFactory.getInstance("X.509");
            FileInputStream fis = new FileInputStream("Serie1/src/main/resources/"+certificate);
            X509Certificate cer = (X509Certificate) fact.generateCertificate(fis);
            PublicKey key = cer.getPublicKey();

            System.out.println(key);

        } catch (Exception e) {
            e.printStackTrace();
        }

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
}
