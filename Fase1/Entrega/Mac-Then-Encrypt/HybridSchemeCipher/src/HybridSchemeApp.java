import controller.Configs;
import controller.operations.HybridScheme;
import utils.time.TimeOutHolder;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

/* Input format - in args[]
* [0] - Symmetrical algorithm (ex: DES, AES)
* [1] - Asymetrical algorithm (ex: RSA, DSA)
* [2] - source file name
* [3] - operation (cipher/decipher)
* [4] - destination certificate (cipher) / metadata file path (decipher)
* [5] - destination filename
* [6] - .pfx (decipher)
*
* ex : AES/CBC/PKCS5Padding RSA/ECB/PKCS1Padding serie1-1920i.pdf cipher Alice_1.cer out.txt
* */

public class HybridSchemeApp {

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException, UnrecoverableKeyException, KeyStoreException, CertificateException {
        Configs configs = new Configs(args);
        configs.parse();

        TimeOutHolder th = new TimeOutHolder();
        HybridScheme hs = HybridScheme.getInstance(configs);

        th.start();

        hs.encryptDecrypt();

        th.end();
        th.printElapsed();
    }
}
