package controller;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Configs {

    public static final String CIPHER = "Cipher";
    public static final String DECIPHER = "Decipher";

    private final String[] args;

    // Configs
    private String sourceFileName;
    private String operation;
    private String destinationCertificateFilePath;
    private Path metadataFilePath;
    private String symetricalAlgorithm;
    private String asymetricalAlgorithm;
    private Path destinationFilePath;
    private Path pfx;

    public Configs(String[] args){
        if(args.length < 6) throw new UnsupportedOperationException();
        this.args = args;
    }
    /* Input format - in args[]
     * [0] - Symmetrical algorithm (ex: DES, AES/CBC/PKCS5Padding)
     * [1] - Asymetrical algorithm (ex: RSA, DSA)
     * [2] - source file name
     * [3] - operation (cipher/decipher)
     * [4] - destination certificate (cipher) / metadata file path (decipher)
     * [5] - destination filename
     * [6] - .pfx (decipher)
     */
    public void parse(){
        this.symetricalAlgorithm = args[0];
        this.asymetricalAlgorithm = args[1];
        this.sourceFileName = args[2];
        this.operation = operation(args[3]);
        this.destinationCertificateFilePath = this.operation.equals(CIPHER)? args[4] : null;
        this.metadataFilePath = this.operation.equals(DECIPHER)? Paths.get(args[4]) : null;
        this.destinationFilePath = Paths.get(args[5]);
        this.pfx = this.operation.equals(DECIPHER)? Paths.get(args[6]) : null;
    }

    private String operation(String arg) {
        if(arg.compareToIgnoreCase(CIPHER) == 0)
            return CIPHER;
        else if(arg.compareToIgnoreCase(DECIPHER) == 0)
            return DECIPHER;
        else throw new UnsupportedOperationException();
    }

    public String getOperation() {
        return this.operation;
    }
    public String getSourceFileName(){return this.sourceFileName;}

    public Path getDestinationFilePath() {
        return this.destinationFilePath;
    }

    public Path getMetadataFilePath() {
        return this.metadataFilePath;
    }

    public String getCertificateFilePath() {
        return this.destinationCertificateFilePath;
    }

    public String getAsymetricalAlgorithm() {
        return asymetricalAlgorithm;
    }

    public String getSymetricalAlgorithm() {
        return symetricalAlgorithm;
    }
}
