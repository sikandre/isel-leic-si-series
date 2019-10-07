package controller.operations.cipher;

import controller.Configs;
import controller.operations.HybridScheme;
import model.Metadata;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.nio.file.Files;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Random;

public class HybridCipherImp extends HybridScheme {

    private static final int INITIAL_VECTOR_SIZE = 16;

    private final byte[] initialVector;
    private final SecretKey symetricalKey;
    byte[] message;

    public HybridCipherImp(Configs configs) throws NoSuchAlgorithmException, IOException {
        super(configs);
        message = Files.readAllBytes(confs.getSourceFilePath());
        KeyGenerator kg = KeyGenerator.getInstance(confs.getSymetricalAlgorithm());
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
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }


    private byte[] cipherSymKeyWithPublicKeyAndGetCipheredSymetricalKey(PublicKey destinationPublicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        char[] pw = {'c','h','a','n','g','e','i','t'};

        // cipher key with destination public key
        Cipher asymetricalCipher = Cipher.getInstance(confs.getAsymetricalAlgorithm());
        asymetricalCipher.init(Cipher.WRAP_MODE, destinationPublicKey);
        return asymetricalCipher.doFinal(symetricalKey.getEncoded());
    }

    private PublicKey validateCertificateAndGetPublicKey() {
        X509Certificate certificate = getCertificateFile(confs.getCertificateFilePath());
        validateCertificate(certificate);
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
    private boolean validateCertificate(Certificate certificate) {
        return false;
    }

    private X509Certificate getCertificateFile(String certificate) {
        X509Certificate cer = null;
        try {
            CertificateFactory fact = CertificateFactory.getInstance("X.509");
            FileInputStream fis = new FileInputStream("Serie1/src/main/resources/" + certificate);
            cer = (X509Certificate) fact.generateCertificate(fis);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cer;
    }

}
