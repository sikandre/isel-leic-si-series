package MacThenEncryptJCA.Model;

import javax.crypto.Mac;

public class MacThenEncryptResponse {
    private CipherMessage cipherMessage;
    private Mac mac;

    public MacThenEncryptResponse(CipherMessage cipherMessage, Mac mac) {
        this.cipherMessage = cipherMessage;
        this.mac = mac;
    }

    public CipherMessage getCipherMessage() {
        return cipherMessage;
    }

    public Mac getMac() {
        return mac;
    }
}
