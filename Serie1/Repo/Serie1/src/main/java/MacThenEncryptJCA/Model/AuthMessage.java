package MacThenEncryptJCA.Model;

import javax.crypto.Mac;

public class AuthMessage {
    public byte[] msg;
    private Mac mac;
    private int authMarkLen;    //should be length of mark

    public AuthMessage(byte[] msg, Mac mac, int mark) {
        this.msg = msg;
        this.mac = mac;
        this.authMarkLen = mark;
    }

    public Mac getMac() {
        return mac;
    }

    public void setMac(Mac mac) {
        this.mac = mac;
    }

    public int getAuthMarkLen() {
        return authMarkLen;
    }

}
