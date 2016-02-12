package com.shaubert.ui.phone;

/**
 * Created by GODARD Tuatini on 07/05/15.
 */
public class Country {
    private final String isoCode;
    private final int countryCode;

    public Country(String isoCode, int countryCode) {
        this.isoCode = isoCode;
        this.countryCode = countryCode;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public int getCountryCode() {
        return countryCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Country country = (Country) o;

        if (countryCode != country.countryCode) return false;
        return isoCode.equals(country.isoCode);

    }

    @Override
    public int hashCode() {
        int result = isoCode.hashCode();
        result = 31 * result + countryCode;
        return result;
    }
}
