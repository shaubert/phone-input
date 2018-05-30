package com.shaubert.ui.phone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Countries {
    private List<Country> countries;
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
        this.countries = Collections.unmodifiableList(countries);

        for (Country country : countries) {
            isoCountriesMap.put(country.getIsoCode().toLowerCase(Locale.US), country);
        }
    }

    public List<Country> getCountries() {
        return countries;
    }

    public Country getCountryByIso(String iso) {
        if (TextUtils.isEmpty(iso)) {
            return null;
        }
        return isoCountriesMap.get(iso.toLowerCase(Locale.US));
    }

    public Country getCountryByCode(int code) {
        for (Country country : countries) {
            if (country.getCountryCode() == code) return country;
        }
        return null;
    }

    public interface Callback {
        void onLoaded(Countries loadedCountries);
    }

}
