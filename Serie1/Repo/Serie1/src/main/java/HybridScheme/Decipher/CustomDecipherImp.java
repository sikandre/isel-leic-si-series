package HybridScheme.Decipher;

import HybridScheme.Models.InputArgs;
import HybridScheme.Models.Metadata;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertPathParameters;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;

public class CustomDecipherImp implements CustomDecipher {
    private static final int INITIAL_VECTOR_SIZE = 16;
    private static final String PASSWORD = "changeit";
    private byte[] metadata;
    private byte[] encriptFile;
    private KeyStore keyStore;

    public CustomDecipherImp(InputArgs inputArgs) {
        encriptFile = getFileFromPath(inputArgs.getEncryptedFilePath());
        metadata = getFileFromPath(inputArgs.getMetadataPath());
        keyStore = getKeyStoreFromPath(inputArgs.getKeyStoreFilePath())

    }

    private KeyStore getKeyStoreFromPath(String keyStoreFilePath) {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            FileInputStream fis = new FileInputStream("Serie1/src/main/resources/"+keyStoreFilePath);
            keyStore.load(fis, PASSWORD.toCharArray());
            return keyStore;
            /*keyStore.store(new FileOutputStream(
                    "Serie1/src/main/resources/"+keyStoreFilePath),
                    PASSWORD.toCharArray());*/ //para guardar e nao load
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] getFileFromPath(String fileName) {
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream is = classloader.getResourceAsStream(fileName);
            var result = IOUtils.toByteArray(is);
            return result;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }

    }
}