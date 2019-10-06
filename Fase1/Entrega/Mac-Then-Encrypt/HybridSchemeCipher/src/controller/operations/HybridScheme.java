package controller.operations;

import controller.Configs;

public abstract class HybridScheme implements CriptographicOperation {
    Configs confs;
    String mode;
    public HybridScheme(Configs configs){
        this.confs = configs;
    }

    public static HybridScheme getInstance(Configs configs) {
        return configs.getOperation().equals(Configs.CIPHER) ? new HybridCipher(configs) : new HybridDecipher(configs);
    }
}
