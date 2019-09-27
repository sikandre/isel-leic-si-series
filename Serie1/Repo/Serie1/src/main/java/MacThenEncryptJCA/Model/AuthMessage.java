package MacThenEncryptJCA.Model;

import javax.crypto.Mac;

public class AuthMessage {
    public byte[] msg;
    private Mac mac;

    public AuthMessage(byte[] msg, Mac mac) {
        this.msg = msg;
        this.mac = mac;
    }

    public Mac getMac() {
        return mac;
    }

    public void setMac(Mac mac) {
        this.mac = mac;
    }
}
