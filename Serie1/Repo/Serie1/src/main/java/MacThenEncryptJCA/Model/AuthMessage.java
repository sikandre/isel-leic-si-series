package MacThenEncryptJCA.Model;

import javax.crypto.Mac;

public class AuthMessage {
    public byte[] msg;
    public byte[] mark;
    private Mac mac;

    public AuthMessage(byte[] msg, byte[] mark, Mac mac) {
        this.msg = msg;
        this.mark = mark;
        this.mac = mac;
    }

    public Mac getMac() {
        return mac;
    }

    public void setMac(Mac mac) {
        this.mac = mac;
    }
}
