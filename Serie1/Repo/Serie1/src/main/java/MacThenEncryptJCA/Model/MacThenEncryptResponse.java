package MacThenEncryptJCA.Model;

import javax.crypto.Mac;

public class MacThenEncryptResponse {
    private CipherMessage cipherMessage;
    private byte[] authMark;
    private Mac mac;

    public MacThenEncryptResponse(CipherMessage cipherMessage, byte[] authMark, Mac mac) {
        this.cipherMessage = cipherMessage;
        this.authMark = authMark;
        this.mac = mac;
    }

    public CipherMessage getCipherMessage() {
        return cipherMessage;
    }

    public byte[] getAuthMark() {
        return authMark;
    }

    public Mac getMac() {
        return mac;
    }
}
