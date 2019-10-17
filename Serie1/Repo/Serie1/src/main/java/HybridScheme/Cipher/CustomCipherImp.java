package HybridScheme.Cipher;

import HybridScheme.Models.InputArgs;
import HybridScheme.Models.Metadata;
import org.apache.commons.io.IOUtils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.*;
import java.util.*;

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

    @Override
    public boolean CipherMessage() throws CustomCipherException {
        try {
            authenticateCertificate(certificate);
            validateCertificatePath(certificate);

            KeyGenerator keyGenerator = KeyGenerator.getInstance(CIPHER_ALGORITHM.split("/")[0]);
            keyGenerator.init(128);
            SecretKey key = keyGenerator.generateKey();

            byte[] initialVector = generateInitialVector();
            IvParameterSpec initialVectorSpecifications = new IvParameterSpec(initialVector);

            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key, initialVectorSpecifications);
            byte[] encryptedOriginalFile = cipher.doFinal(originalFile);

            //cipher key with public key
            Cipher c = Cipher.getInstance("RSA/ECB/OAEPPadding");
            c.init(Cipher.PUBLIC_KEY, certificate.getPublicKey());
            byte[] encryptedKey = c.doFinal(key.getEncoded());
            metadata = new Metadata(initialVector, encryptedKey);

            Files.write(Paths.get("Serie1/src/OutputFiles/cipherFile"), encryptedOriginalFile);
            Files.write(Paths.get("Serie1/src/OutputFiles/metadata"), metadata.getMetadataAsBytes());

        } catch (CustomCipherException | IOException | GeneralSecurityException e) {
            throw new CustomCipherException(e.getMessage());
        }
        return true;
    }

    private X509Certificate getCertificateFile(String certificate) {
        X509Certificate cer = null;
        try {
            CertificateFactory fact = CertificateFactory.getInstance("X.509");
            FileInputStream fis = new FileInputStream(getCertificateFilePath(certificate));
            cer = (X509Certificate) fact.generateCertificate(fis);

        } catch (Exception e) {
            System.out.println(certificate+" Not Found");
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

    private void validateCertificatePath(X509Certificate leafCertificate) throws CustomCipherException {
        try {
            LinkedList<X509Certificate> chainCertificate = getChainCertificate(leafCertificate);
            // Find root certificate
            X509Certificate rootCert = null;
            for (X509Certificate c : chainCertificate) {
                if (isRoot(c)){
                    rootCert = c;
                    chainCertificate.remove(c);
                }
            }
            if(rootCert == null) throw new CustomCipherException("Chain Certificate Root not Found");

            X509CertSelector selector = new X509CertSelector();
            selector.setCertificate(leafCertificate);
            TrustAnchor trustAnchors = new TrustAnchor(rootCert, null);

            PKIXBuilderParameters pkixParams = new PKIXBuilderParameters(Collections.singleton(trustAnchors), selector);
            pkixParams.setRevocationEnabled(false);
            CertStore intermediateCertStore = CertStore.getInstance("Collection", new CollectionCertStoreParameters(chainCertificate));
            pkixParams.addCertStore(intermediateCertStore);
            // Build and verify the certification chain
            CertPathBuilder builder = CertPathBuilder.getInstance("PKIX");
            builder.build(pkixParams);

        } catch (GeneralSecurityException e) {
            throw new CustomCipherException(e.getMessage());
        }
    }

    private LinkedList<X509Certificate> getChainCertificate(X509Certificate leafCertificate) {
        LinkedList<X509Certificate> chain = new LinkedList<>();
        X509Certificate curr = leafCertificate;
        while (!curr.getSubjectDN().getName().equals(curr.getIssuerDN().getName())) {
            String name = curr.getIssuerDN().getName().split(",")[0].split("=")[1];
            curr = getCertificateFile(name + ".cer");
            chain.add(curr);
        }
        return chain;
    }
    private void authenticateCertificate(X509Certificate certificate) throws CustomCipherException {
        PublicKey publicKey = getAuthorizingCertificate(certificate).getPublicKey();
        try {
            certificate.verify(publicKey);
        } catch (GeneralSecurityException e) {
            throw new CustomCipherException("Authentication Failed");
        }
    }

    private X509Certificate getAuthorizingCertificate(X509Certificate certificate) {
        String path = certificate.getIssuerDN().getName().split(",")[0].split("=")[1]+ ".cer";
        X509Certificate authority = getCertificateFile(path);
        return authority;
    }

    private static boolean isRoot(X509Certificate cert) throws GeneralSecurityException {
        try {
            // Try to verify certificate signature with its own public key
            PublicKey key = cert.getPublicKey();
            cert.verify(key);
            return true;
        } catch (SignatureException | InvalidKeyException e) {
            return false;
        }
    }


}
