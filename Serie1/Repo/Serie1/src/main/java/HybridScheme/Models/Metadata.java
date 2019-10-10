package HybridScheme.Models;

import MacThenEncryptJCA.ArrayUtils;

public class Metadata {
    private byte[] initialVector;
    private byte[] simetricKey;

    public Metadata(byte[] initialVector, byte[] simetricKey) {
        this.initialVector = initialVector;
        this.simetricKey = simetricKey;
    }

    public byte[] getMetadataAsBytes(){
        return ArrayUtils.addAll(initialVector, simetricKey);
    }
}
