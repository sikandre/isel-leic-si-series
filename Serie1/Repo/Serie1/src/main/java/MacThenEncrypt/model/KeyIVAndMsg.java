package MacThenEncrypt.model;

import javax.crypto.SecretKey;

public class KeyIVAndMsg {
    public SecretKey key;
    public byte[] iv;
    public byte[] msg;

    public KeyIVAndMsg(SecretKey key, byte[] iv, byte[] msg) {
        this.key = key;
        this.iv = iv;
        this.msg = msg;
    }
}
