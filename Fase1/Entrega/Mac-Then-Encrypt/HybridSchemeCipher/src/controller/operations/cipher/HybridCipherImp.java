package controller.operations.cipher;

import controller.Configs;
import controller.operations.HybridScheme;
import model.Metadata;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.security.*;
import java.security.cert.*;
import java.util.LinkedList;
import java.util.Random;

public class HybridCipherImp extends HybridScheme {

    private static Logger log = LoggerFactory.getLogger(HybridCipherImp.class);
    private static final int INITIAL_VECTOR_SIZE = 16;

    private final byte[] initialVector;
    private final SecretKey symetricalKey;
    byte[] message;

    public HybridCipherImp(Configs configs) throws NoSuchAlgorithmException, IOException {
        super(configs);
        message = getBytesFromResources(confs.getSourceFileName());
        //TODO corrigir
        KeyGenerator kg = KeyGenerator.getInstance(confs.getSymetricalAlgorithm().split("/")[0]);
        symetricalKey = kg.generateKey();
        initialVector = new byte[INITIAL_VECTOR_SIZE];
        new Random().nextBytes(initialVector);
    }

    @Override
    public void encryptDecrypt() {
        try {
            encryptMessageAndWriteToDestinationFile();
            PublicKey destinationPublicKey = validateCertificateAndGetPublicKey();
            byte[] cipheredSymetricalKey = cipherSymKeyWithPublicKeyAndGetCipheredSymetricalKey(destinationPublicKey);
            // write metadata ( key and initial vector)
            Metadata metadata = new Metadata(cipheredSymetricalKey, initialVector);
            Files.write(confs.getMetadataFilePath(), metadata.toByteArray());
        }
        catch (NoSuchAlgorithmException e){
            log.error(e.getMessage());
        }catch (IOException e) {
            log.error(e.getMessage());
        } catch (InvalidKeyException e) {
            log.error(e.getMessage());
        } catch (InvalidAlgorithmParameterException e) {
            log.error(e.getMessage());
        } catch (NoSuchPaddingException e) {
            log.error(e.getMessage());
        } catch (BadPaddingException e) {
            log.error(e.getMessage());
        } catch (IllegalBlockSizeException e) {
            log.error(e.getMessage());
        }
    }

    private byte[] getBytesFromResources(String sourceFileName) {
        byte[] bytes = new byte[0];
        try {
            bytes = IOUtils.toByteArray(Thread.currentThread().getContextClassLoader().getResourceAsStream(sourceFileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }
    private byte[] cipherSymKeyWithPublicKeyAndGetCipheredSymetricalKey(PublicKey destinationPublicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        // cipher key with destination public key
        Cipher asymetricalCipher = Cipher.getInstance(confs.getAsymetricalAlgorithm());
        asymetricalCipher.init(Cipher.WRAP_MODE, destinationPublicKey);
        return asymetricalCipher.doFinal(symetricalKey.getEncoded());
    }

    private PublicKey validateCertificateAndGetPublicKey() {
        X509Certificate certificate = getCertificateFile(confs.getCertificateFilePath());
        validateCertificate(confs.getCertificateFilePath());
        return certificate.getPublicKey();
    }

    private void encryptMessageAndWriteToDestinationFile() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
        Cipher symetricalCipher = Cipher.getInstance(confs.getSymetricalAlgorithm());
        IvParameterSpec ivspec = new IvParameterSpec(initialVector);
        symetricalCipher.init( Cipher.ENCRYPT_MODE, symetricalKey, ivspec);

        byte[] cipheredText = symetricalCipher.doFinal(message);
        // write cf
        Files.write(confs.getDestinationFilePath(),cipheredText);

    }

    // TODO implement certification chain validation
    private boolean validateCertificate(String certificatePath) {
        try {
            char [] pw = {'c','h','a','n','g','e','i','t'};
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            URL resource = Thread.currentThread().getContextClassLoader().getResource("ks.jks");
            File file = new File(resource.toURI());
            ks.load(new FileInputStream(file), pw);

            CertPathBuilder cpb = CertPathBuilder.getInstance("PKIX");
            X509Certificate cert = getCertificateFile(certificatePath);
            X509CertSelector selector = new X509CertSelector();
            selector.setCertificate(cert);
            CertPathParameters xparams = new PKIXBuilderParameters(ks,selector);
            // TODO where cs integrates with selector and keystore ???
            CertStore cs = getCertStore(certificatePath);

            cpb.build(xparams);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            return false;
        } catch (CertPathBuilderException e) {
            e.printStackTrace();
            return false;
        } catch (KeyStoreException e) {
            e.printStackTrace();
            return false;
        } catch (CertificateException e) {
            e.printStackTrace();
            return false;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return true;
    }

    private CertStore getCertStore(String certificatePath) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        LinkedList<X509Certificate> chain = new LinkedList<>();
        X509Certificate curr = getCertificateFile(certificatePath);
        chain.add(curr);
        while(!curr.getSubjectDN().getName().equals(curr.getIssuerDN().getName())) {
            String name = curr.getIssuerDN().getName().split(",")[0].split("=")[1];
            curr = getCertificateFile(name+".cer");
            chain.add(curr);
        }
        CollectionCertStoreParameters ccsp = new CollectionCertStoreParameters(chain);
        return CertStore.getInstance("Collection",ccsp);
    }

    private X509Certificate getCertificateFile(String certificate) {
        X509Certificate cer = null;
        try {
            CertificateFactory fact = CertificateFactory.getInstance("X.509");
            URL resource = Thread.currentThread().getContextClassLoader().getResource(certificate);
            File file = new File(resource.toURI());
            FileInputStream fis = new FileInputStream(file);
            cer = (X509Certificate) fact.generateCertificate(fis);
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return cer;
    }

}
