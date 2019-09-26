package MacThenEncrypt.model;

import javax.crypto.SecretKey;

public class KeyIVAndMsg {
    public SecretKey key;
    public byte[] initialVector;
    public byte[] msg;

    public KeyIVAndMsg(SecretKey key, byte[] iv, byte[] msg) {
        this.key = key;
        this.initialVector = iv;
        this.msg = msg;
    }
}
