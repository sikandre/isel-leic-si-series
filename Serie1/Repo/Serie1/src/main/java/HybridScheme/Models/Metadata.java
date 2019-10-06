package HybridScheme.Models;

public class Metadata {
    private byte[] initialVector;
    private byte[] simetricKey;

    public Metadata(byte[] initialVector, byte[] simetricKey) {
        this.initialVector = initialVector;
        this.simetricKey = simetricKey;
    }
}
