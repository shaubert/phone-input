package com.shaubert.ui.phone;

public class Mask {

    public static Mask EMPTY = new Mask(null, null, null);

    public final String mask;
    public final String prefix;
    public final String defaultNumber;

    public Mask(String mask,
                String prefix,
                String defaultNumber) {
        this.mask = mask;
        this.prefix = prefix;
        this.defaultNumber = defaultNumber;
    }
}
