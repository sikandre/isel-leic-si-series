import controller.Configs;
import controller.operations.HybridScheme;
import controller.operations.CriptographicOperation;
import utils.time.TimeOutHolder;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

/*
* Usando a JCA realize uma aplicac~ao para cifrar e decifrar cheiros usando um esquema hbrido. Este tipo
de esquema usa cifra assimetrica para transportar uma chave simetrica (gerada pela aplicac~ao) que cifra
o conteudo do cheiro.
 input: i) nome de cheiro (com
mensagem em claro ou cifrada);
* ii) a operac~ao a realizar (cifra ou decifra).
*
* No modo cifra recebe
o certicado do destinatario e produz i) cheiro com mensagem cifrada (Cf ); ii) cheiro com metadados
(IV e chave simetrica cifrada com a chave publica do destinatario).
*
* No modo decifra recebe:
* i) Cf ;
* ii) metadados;
* iii) Chave privada do destinatario (cheiro .pfx).
* e produz cheiro com o texto em claro.

* Apresente tempos de execuc~ao para cifrar e decifrar o PDF do enunciado usando 2 algoritmos simetricos
diferentes, em combinac~ao com o algoritmo assimetrico RSA. Use o material criptograco presente no
anexo certificates-keys.zip
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
