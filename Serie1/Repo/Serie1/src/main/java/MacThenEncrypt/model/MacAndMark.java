package MacThenEncrypt.model;

import javax.crypto.Mac;
import javax.crypto.SecretKey;

public class MacAndMark {
    public byte[] mark;
    public Mac mac;

    public MacAndMark(Mac mac, byte[] mark) {
        this.mac = mac;
        this.mark = mark;
    }
}
