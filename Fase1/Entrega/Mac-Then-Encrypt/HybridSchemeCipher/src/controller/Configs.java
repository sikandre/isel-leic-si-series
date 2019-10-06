package controller;

public class Configs {
    public static final String CIPHER = "Cipher";
    public static final String DECIPHER = "Decipher";

    private final String[] args;

    // Configs
    private String fn;
    private String op;
    private String cmfn;
    private String metadataFn;
    private String dpKeyFn;

    public Configs(String[] args){
        this.args = args;
    }

    public void parse(){
        this.fn = args[0];
        this.op = operation(args[1]);
        cmfn = args[2];
        metadataFn = args[3];
        if (op.equals(DECIPHER)) {
            dpKeyFn = args[4];
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
}
