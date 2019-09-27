package MacThenEncryptJCA.Model;

import javax.crypto.SecretKey;

public class CipherMessage {
    public SecretKey key;
    public byte[] initialVector;
    public byte[] msg;

    public CipherMessage(SecretKey key, byte[] iv, byte[] msg) {
        this.key = key;
        this.initialVector = iv;
        this.msg = msg;
    }
}
