package model;

import utils.time.arrays.ArrayUtils;

public class Metadata {

    byte[] initialVector;
    byte[] symetricalKey;

    public Metadata(byte[] initialVector, byte[] symetricalKey) {
        this.initialVector = initialVector;
        this.symetricalKey = symetricalKey;
    }

    public byte[] toByteArray(){
        return ArrayUtils.addAll(initialVector,symetricalKey);
    }
}
