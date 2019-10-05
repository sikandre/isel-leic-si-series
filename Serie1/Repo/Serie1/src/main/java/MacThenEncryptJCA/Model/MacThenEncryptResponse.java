package MacThenEncryptJCA.Model;

import javax.crypto.Mac;

public class MacThenEncryptResponse {
    private CipherMessage cipherMessage;
    private Mac mac;
    private int authMarkLen;

    public MacThenEncryptResponse(CipherMessage cipherMessage, Mac mac, int authMarkLen) {
        this.cipherMessage = cipherMessage;
        this.mac = mac;
        this.authMarkLen = authMarkLen;
    }

    public CipherMessage getCipherMessage() {
        return cipherMessage;
    }

    public Mac getMac() {
        return mac;
    }

    public int getAuthMarkLen() {
        return authMarkLen;
    }
}
