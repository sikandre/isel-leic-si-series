package MacThenEncrypt.model;

import javax.crypto.Mac;

public class MacAndMark {
    public byte[] mark;
    public Mac mac;

    public MacAndMark(Mac mac, byte[] mark) {
        this.mac = mac;
        this.mark = mark;
    }
}
