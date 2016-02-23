package com.shaubert.ui.phone;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import java.util.*;

public class Countries {
    private Context appContext;
    private List<Country> countries = new ArrayList<>();
    private Map<String, Country> isoCountriesMap = new HashMap<>();
    private Map<String, String> displayCountryNames = new HashMap<>();
    private String language;

    private final Comparator<Country> countryComparator = new Comparator<Country>() {
        @Override
        public int compare(Country country1, Country country2) {
            String lhsName = displayCountryNames.get(country1.getIsoCode());
            String rhsName = displayCountryNames.get(country2.getIsoCode());
            return lhsName.compareTo(rhsName);
        }
    };

    private static Countries instance;

    public static void get(final Context context, final Callback callback) {
        if (instance != null) {
            callback.onLoaded(instance);
            return;
        }

        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                get(context);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onLoaded(instance);
                    }
                });
            }
        }).start();
    }

    public static synchronized Countries get(Context context) {
        if (instance == null) {
            List<Country> countries = CountriesBuilder.createCountriesList(true);
            instance = new Countries(countries, context);
        }

        return instance;
    }

    private Countries(List<Country> countries, Context context) {
        this.countries = countries;
        this.appContext = context.getApplicationContext();

        for (Country country : countries) {
            isoCountriesMap.put(country.getIsoCode().toLowerCase(Locale.US), country);
        }

        updateDisplayCountyNamesIfNeeded();
    }

    private void updateDisplayCountyNamesIfNeeded() {
        String newLanguage = appContext.getResources().getConfiguration().locale.getLanguage();
        if (TextUtils.equals(newLanguage, language)) {
            return;
        }

        language = newLanguage;
        displayCountryNames.clear();
        for (Country country : countries) {
            String name = new Locale(language, country.getIsoCode()).getDisplayCountry();
            displayCountryNames.put(country.getIsoCode(), name);
        }

        Collections.sort(countries, countryComparator);
    }

    public List<Country> getCountries() {
        return new ArrayList<>(countries);
    }

    public Country getCountryByIso(String iso) {
        if (TextUtils.isEmpty(iso)) {
            return null;
        }
        return isoCountriesMap.get(iso.toLowerCase(Locale.US));
    }

    public String getDisplayCountryName(Country country) {
        updateDisplayCountyNamesIfNeeded();
        return displayCountryNames.get(country.getIsoCode());
    }

    public int getFlagResId(Country country) {
        int countryResId = getCountryResId(appContext, country);
        if (countryResId == 0) {
            countryResId = R.mipmap.unknown_flag;
        }
        return countryResId;
    }

    private static int getCountryResId(Context context, Country country) {
        return getMipmapResId(context, country.getIsoCode().toLowerCase(Locale.ENGLISH) + "_flag");
    }

    private static int getMipmapResId(Context context, String drawableName) {
        return context.getResources().getIdentifier(
                drawableName.toLowerCase(Locale.ENGLISH), "mipmap", context.getPackageName());
    }

    public interface Callback {
        void onLoaded(Countries loadedCountries);
    }

}
