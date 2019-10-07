package controller.operations;

import controller.Configs;
import controller.operations.cipher.HybridCipherImp;

import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

public abstract class HybridScheme {
    protected Configs confs;
    public HybridScheme(Configs configs){
        this.confs = configs;
    }

    public static HybridScheme getInstance(Configs configs) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, UnrecoverableKeyException, CertificateException, KeyStoreException, IOException {
        return configs.getOperation().equals(Configs.CIPHER) ? new HybridCipherImp(configs) : new HybridDecipher(configs);
    }

    public abstract void encryptDecrypt();
}
