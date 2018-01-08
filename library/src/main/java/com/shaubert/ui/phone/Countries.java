package com.shaubert.ui.phone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import java.util.*;

public class Countries {
    private List<Country> countries = new ArrayList<>();
    private Map<String, Country> isoCountriesMap = new HashMap<>();

    @SuppressLint("StaticFieldLeak")
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
            List<Country> countries = CountriesBuilder.createCountriesList(context, true);
            instance = new Countries(countries);
        }

        return instance;
    }

    private Countries(List<Country> countries) {
        this.countries = countries;

        for (Country country : countries) {
            isoCountriesMap.put(country.getIsoCode().toLowerCase(Locale.US), country);
        }
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

    public interface Callback {
        void onLoaded(Countries loadedCountries);
    }

}
