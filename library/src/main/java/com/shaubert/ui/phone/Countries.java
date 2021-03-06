package com.shaubert.ui.phone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
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

        new AsyncTask<Void, Void, Countries>() {
            @Override
            protected Countries doInBackground(Void... voids) {
                return Countries.get(context);
            }

            @Override
            protected void onPostExecute(Countries countries) {
                callback.onLoaded(countries);
            }
        }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    public static synchronized Countries get(Context context) {
        if (instance == null) {
            List<Country> countries = CountriesBuilder.createCountriesList(context, true);
            instance = new Countries(countries);
        }

        return instance;
    }

    public Countries(List<Country> countries) {
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
