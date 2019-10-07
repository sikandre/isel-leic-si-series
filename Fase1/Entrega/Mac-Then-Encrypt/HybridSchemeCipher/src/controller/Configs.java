package controller;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Configs {
    public static final String CIPHER = "Cipher";
    public static final String DECIPHER = "Decipher";

    private final String[] args;

    // Configs
    private Path fn;
    private String op;
    private String destinationCertificateFilePath;
    private Path metadataFilePath;
    private Path destinationPrivateKeyFileName;

    public Configs(String[] args){
        if(args.length < 4) throw new UnsupportedOperationException();
        this.args = args;
    }

    public void parse(){
        this.fn = Paths.get(args[0]);
        this.op = operation(args[1]);
        this.destinationCertificateFilePath = args[2];
        this.metadataFilePath = Paths.get(args[3]);
        if (op.equals(DECIPHER)) {
            this.destinationPrivateKeyFileName = Paths.get(args[4]);
        }
    }

    private String operation(String arg) {
        if(arg.equals(CIPHER))
            return CIPHER;
        else if(arg.equals(DECIPHER))
            return DECIPHER;
        else throw new UnsupportedOperationException();
    }

    public String getOperation() {
        return this.op;
    }
    public Path getSourceFilePath(){return this.fn;}

    public Path getDestinationFilePath() {
        return this.metadataFilePath;
    }

    public Path getMetadataFilePath() {
        return this.metadataFilePath;
    }

    public String getCertificateFilePath() {
        return this.destinationCertificateFilePath;
    }

    public String getAsymetricalAlgorithm() {
        return "";
    }

    public String getSymetricalAlgorithm() {
        return "";
    }
}
