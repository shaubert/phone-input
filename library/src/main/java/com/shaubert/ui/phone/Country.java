package com.shaubert.ui.phone;

/**
 * Created by GODARD Tuatini on 07/05/15.
 */
public class Country {
    private final String isoCode;
    private final String dialingCode;

    public Country(String isoCode, String dialingCode) {
        this.isoCode = isoCode;
        this.dialingCode = dialingCode;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public String getDialingCode() {
        return dialingCode;
    }
}
