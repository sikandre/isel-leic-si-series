package HybridScheme;

import HybridScheme.Cipher.CustomCipher;
import HybridScheme.Cipher.CustomCipherImp;
import HybridScheme.Decipher.CustomDecipher;
import HybridScheme.Decipher.CustomDecipherImp;
import HybridScheme.Models.InputArgs;

import java.util.Scanner;

public class HybridApp {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("insert args");
            args = new Scanner(System.in).nextLine().split(" ");
        }

        InputArgs inputArgs = Parse.getInputArgs(args);
        if(inputArgs.isCipher()){
            CustomCipher cipher = new CustomCipherImp(inputArgs);
        }
        else if(inputArgs.isDecipher()){
            CustomDecipher decipher = new CustomDecipherImp(inputArgs);
        }
        else
            System.out.println("No action found");
    }
}
