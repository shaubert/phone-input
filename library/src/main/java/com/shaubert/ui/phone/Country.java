package com.shaubert.ui.phone;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Country implements Comparable<Country> {
    private static final Map<String, String> DISPLAY_COUNTRY_NAMES_CACHE = new HashMap<>();
    private static String language;

    private final String isoCode;
    private final Context appContext;

    private String unicodeSymbol;
    private int countryCode;
    private boolean hasCountryCode;

    public Country(String isoCode, Context context) {
        this.isoCode = isoCode;
        this.appContext = context;
    }

    public Country(String isoCode, int countryCode, Context context) {
        this.isoCode = isoCode;
        this.countryCode = countryCode;
        this.appContext = context;
        hasCountryCode = true;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public String getUnicodeSymbol() {
        if (unicodeSymbol == null) {
            unicodeSymbol = asUnicodeFlag(isoCode, getCountryCode());
        }
        return unicodeSymbol;
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

    private static String asUnicodeFlag(String isoCode, int countryCode) {
        if (isoCode == null) return null;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return "+" + countryCode;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < isoCode.length(); i++) {
            builder.append(Character.toChars(Character.codePointAt(isoCode, i) + 127397));
        }
        return builder.toString();
    }

    public String getDisplayName() {
        synchronized (DISPLAY_COUNTRY_NAMES_CACHE) {
            String newLanguage = appContext.getResources().getConfiguration().locale.getLanguage();
            if (!TextUtils.equals(newLanguage, language)) {
                DISPLAY_COUNTRY_NAMES_CACHE.clear();
                language = newLanguage;
            }

            String name = DISPLAY_COUNTRY_NAMES_CACHE.get(isoCode);
            if (name == null) {
                name = new Locale(language, isoCode).getDisplayCountry();
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

        if (countryCode != country.countryCode) return false;
        return isoCode.equals(country.isoCode);

    }

    @Override
    public int hashCode() {
        int result = isoCode.hashCode();
        result = 31 * result + countryCode;
        return result;
    }

    @Override
    public int compareTo(@NonNull Country o) {
        return getDisplayName().compareTo(o.getDisplayName());
    }

}
