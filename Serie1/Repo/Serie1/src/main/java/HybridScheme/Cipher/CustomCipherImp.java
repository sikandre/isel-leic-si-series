package HybridScheme.Cipher;

import HybridScheme.Models.InputArgs;
import HybridScheme.Models.Metadata;
import org.apache.commons.io.IOUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.util.LinkedList;
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

            FileInputStream fis = new FileInputStream(getCertificateFilePath(certificate));
            cer = (X509Certificate) fact.generateCertificate(fis);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return cer;
    }

    private String getCertificateFilePath(String certificate) {
        Path initialPath = Paths.get("Serie1/src/InputFiles/");
        String path = getPathFromName(initialPath, certificate);
        return path;
    }

    private String getPathFromName(Path path, String certificate) {
        File dir = path.toFile();
        File[] files = dir.listFiles();
        String res = null;
        for (int i = 0; i < files.length; i++) {
            if(files[i].isDirectory()){
                res = getPathFromName(Paths.get(files[i].getPath()),certificate);
                if(res != null)
                    return res;
            }else if(files[i].getName().equalsIgnoreCase(certificate)){
                return files[i].getPath();
            }
        }
        return res;
    }

    private byte[] getFileFromPath(String fileName) {
        byte[] result = new byte[0];
        try {
            FileInputStream fis = new FileInputStream("Serie1/src/InputFiles/" + fileName);
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

    private boolean validateCertificate(X509Certificate cert) throws CustomCipherException {
        try {
            char[] pw = {'c', 'h', 'a', 'n', 'g', 'e', 'i', 't'};
            KeyStore ks = getKeyStore("ks.jks", pw);

            X509CertSelector selector = new X509CertSelector();
            selector.setCertificate(cert);

            CertStore cs = getCertStore(cert);

            PKIXBuilderParameters xparams = new PKIXBuilderParameters(ks, selector);
            xparams.setRevocationEnabled(false);
            xparams.addCertStore(cs);

            CertPathBuilder cpb = CertPathBuilder.getInstance("PKIX");
            cpb.build(xparams);

        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException |
                CertPathBuilderException | KeyStoreException | CertificateException |
                IOException | URISyntaxException e ) {
            throw new CustomCipherException();
        }
        return true;
    }

    private KeyStore getKeyStore(String ksName, char[] pw) throws KeyStoreException, URISyntaxException, IOException, CertificateException, NoSuchAlgorithmException {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        File file = Paths.get("Serie1/src/InputFiles/" + ksName).toFile();
        ks.load(new FileInputStream(file), pw);
        return ks;
    }

    private CertStore getCertStore(X509Certificate endCertificate) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        LinkedList<X509Certificate> chain = new LinkedList<>();
        X509Certificate curr = endCertificate;
        while (!curr.getSubjectDN().getName().equals(curr.getIssuerDN().getName())) {
            chain.add(curr);
            String name = curr.getIssuerDN().getName().split(",")[0].split("=")[1];
            curr = getCertificateFile(name + ".cer");
        }
        chain.add(curr);
        CollectionCertStoreParameters ccsp = new CollectionCertStoreParameters(chain);
        return CertStore.getInstance("Collection", ccsp);
    }

    @Override
    public boolean CipherMessage() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(CIPHER_ALGORITHM.split("/")[0]);
            keyGenerator.init(128);
            SecretKey key = keyGenerator.generateKey();

            byte[] initialVector = generateInitialVector();
            IvParameterSpec initialVectorSpecifications = new IvParameterSpec(initialVector);

            javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key, initialVectorSpecifications);
            byte[] encryptedOriginalFile = cipher.doFinal(originalFile);

            //cipher key with public key
            javax.crypto.Cipher c = javax.crypto.Cipher.getInstance("RSA/ECB/OAEPPadding");
            c.init(Cipher.PUBLIC_KEY, certificate.getPublicKey());
            byte[] encryptedKey = c.doFinal(key.getEncoded());
            metadata = new Metadata(initialVector, encryptedKey);

            Files.write(Paths.get("Serie1/src/OutputFiles/cipherFile"), encryptedOriginalFile);
            validateCertificate(certificate);
            Files.write(Paths.get("Serie1/src/OutputFiles/metadata"), metadata.getMetadataAsBytes());

        } catch (GeneralSecurityException | IOException | CustomCipherException e) {
            System.out.println(e.getMessage());
            System.out.println("unable to cipher");
            return false;
        }
        return true;
    }
}
