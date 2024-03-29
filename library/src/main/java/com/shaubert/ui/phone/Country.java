package com.shaubert.ui.phone;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Country implements Comparable<Country> {
    private static final Map<String, String> DISPLAY_COUNTRY_NAMES_CACHE = new HashMap<>();
    private static String language;

    private final String isoCode;

    private String displayName;
    private String unicodeSymbol;
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

    public Country(String isoCode, String displayName, String unicodeSymbol) {
        this(isoCode, displayName, unicodeSymbol, 0, false);
    }

    public Country(String isoCode, String displayName, String unicodeSymbol, int countryCode) {
        this(isoCode, displayName, unicodeSymbol, countryCode, true);
    }

    private Country(String isoCode, String displayName, String unicodeSymbol, int countryCode, boolean hasCountryCode) {
        this.isoCode = isoCode;
        this.displayName = displayName;
        this.unicodeSymbol = unicodeSymbol;
        this.countryCode = countryCode;
        this.hasCountryCode = hasCountryCode;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public String getUnicodeSymbol() {
        if (unicodeSymbol == null) {
            unicodeSymbol = asUnicodeFlag(isoCode);
        }
        return unicodeSymbol;
    }

    public synchronized int getCountryCode() {
        if (!hasCountryCode) {
            countryCode = resolveCountryCode();
            hasCountryCode = true;
        }
        return countryCode;
    }

    private int resolveCountryCode() {
        Integer code = CountriesBuilder.getCountyCode(isoCode);
        return code != null ? code : 0;
    }

    private static String asUnicodeFlag(String isoCode) {
        if (isoCode == null) return null;

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < isoCode.length(); i++) {
            builder.append(Character.toChars(Character.codePointAt(isoCode, i) + 127397));
        }
        return builder.toString();
    }

    public String getDisplayName() {
        if (displayName != null) {
            return displayName;
        }

        synchronized (DISPLAY_COUNTRY_NAMES_CACHE) {
            Countries.CurrentLocaleProvider provider = Countries.getCurrentLocaleProvider();
            final String newLanguage;
            if (provider != null) {
                newLanguage = provider.getCurrentLocale().getLanguage();
            } else {
                newLanguage = Locale.getDefault().getLanguage();
            }

            if (!TextUtils.equals(newLanguage, language)) {
                DISPLAY_COUNTRY_NAMES_CACHE.clear();
                language = newLanguage;
            }

            String name = DISPLAY_COUNTRY_NAMES_CACHE.get(isoCode);
            if (name == null) {
                name = new Locale(isoCode, isoCode).getDisplayCountry(new Locale((language)));
                DISPLAY_COUNTRY_NAMES_CACHE.put(isoCode, name);
            }
            return name;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Country country = (Country) o;

        if (getCountryCode() != country.getCountryCode()) return false;
        return isoCode.equals(country.isoCode);
    }

    @Override
    public int hashCode() {
        int result = isoCode.hashCode();
        result = 31 * result + getCountryCode();
        return result;
    }

    @Override
    public String toString() {
        return "Country{" +
                "isoCode='" + isoCode + '\'' +
                ", unicodeSymbol='" + unicodeSymbol + '\'' +
                ", countryCode=" + countryCode +
                '}';
    }

    @Override
    public int compareTo(@NonNull Country o) {
        return getDisplayName().compareTo(o.getDisplayName());
    }

}
