package com.shaubert.ui.phone;

public class Country {
    private final String isoCode;

    private int countryCode;
    private boolean hasCountryCode;

    public Country(String isoCode) {
        this.isoCode = isoCode;
    }

    public Country(String isoCode, int countryCode) {
        this.isoCode = isoCode;
        this.countryCode = countryCode;
        hasCountryCode = true;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public synchronized int getCountryCode() {
        if (!hasCountryCode) {
            countryCode = resolveCountryCode();
        }
        return countryCode;
    }

    private int resolveCountryCode() {
        return CountriesBuilder.getCountyCode(isoCode);
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
