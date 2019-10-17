package HybridScheme;

import HybridScheme.Models.InputArgs;

public class Parse {
    private static InputArgs inputArgs;

    public static InputArgs getInputArgs(String[] args) {
        inputArgs = new InputArgs();
        if(args.length == 0) return null;

        for (int i = 0; i < args.length; i++) {
            setArg(args[i], i);
        }
        return inputArgs;
    }
    //serie1-1920i.pdf cifra cert-end.entities/Alice_1.cer
//AES/CBC/PKCS5Padding RSA/ECB/PKCS1Padding serie1-1920i.pdf cipher Alice_1.cer out.txt
    private static void setArg(String arg, int i) {
        switch (i){
            case 0:
                inputArgs.setFilePath(arg);
                return;
            case 1:
                if(arg.toLowerCase().equals("cifra")) inputArgs.setCipher();
                else if(arg.toLowerCase().equals("decifra")) inputArgs.setDecipher();
                return;
            case 2:
                if(inputArgs.isCipher()) inputArgs.setCertificatePath(arg);
                else if (inputArgs.isDecipher()) inputArgs.setMetadata(arg);
                return;
            case 3:
                inputArgs.setEncryptedFilePath(arg);
                return;
            default:
        }
    }


}
