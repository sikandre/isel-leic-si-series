package HybridScheme.Models;

public class InputArgs {
    private String filePath;
    private boolean cipher;
    private String certificate;
    private boolean decipher;
    private String metadata;
    private String encryptedFilePath;
    private String keyStoreFilePath;

    public InputArgs() {
    }

    public InputArgs(String filePath, boolean cipher, String certificate, boolean decipher, String metadata, String privateKeyFilePath, String keyStoreFilePath) {
        this.filePath = filePath;
        this.cipher = cipher;
        this.certificate = certificate;
        this.decipher = decipher;
        this.metadata = metadata;
        this.encryptedFilePath = privateKeyFilePath;
        this.keyStoreFilePath = keyStoreFilePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isCipher() {
        return cipher;
    }

    public void setCipher() {
        this.cipher = true;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificatePath(String certificate) {
        this.certificate = certificate;
    }

    public boolean isDecipher() {
        return decipher;
    }

    public void setDecipher() {
        this.decipher = true;
    }

    public String getMetadataPath() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getEncryptedFilePath() {
        return encryptedFilePath;
    }

    public void setEncryptedFilePath(String encryptedFilePath) {
        this.encryptedFilePath = encryptedFilePath;
    }

    public String getKeyStoreFilePath() {
        return keyStoreFilePath;
    }

    public void setKeyStoreFilePath(String keyStoreFilePath) {
        this.keyStoreFilePath = keyStoreFilePath;
    }
}
