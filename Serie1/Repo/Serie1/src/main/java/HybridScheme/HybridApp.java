package HybridScheme;

import HybridScheme.Cipher.CustomCipher;
import HybridScheme.Cipher.CustomCipherImp;
import HybridScheme.Decipher.CustomDecipher;
import HybridScheme.Decipher.CustomDecipherImp;
import HybridScheme.Models.InputArgs;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Scanner;

public class HybridApp {
    public static void main(String[] args) throws GeneralSecurityException, IOException {
        if (args.length == 0) {
            System.out.println("insert args");
            args = new Scanner(System.in).nextLine().split(" ");
        }

        InputArgs inputArgs = Parse.getInputArgs(args);
        if(inputArgs.isCipher()){
            CustomCipher cipher = new CustomCipherImp(inputArgs);
            cipher.CipherMessage();
        }
        else if(inputArgs.isDecipher()){
            CustomDecipher decipher = new CustomDecipherImp(inputArgs);
            decipher.decipherMessage();
        }
        else
            System.out.println("No action found");
    }
}
