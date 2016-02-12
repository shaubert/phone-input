package com.shaubert.ui.phone;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Created by GODARD Tuatini on 07/05/15.
 */
class Utils {

    static final String TAG = Utils.class.getSimpleName();

    static int getCountryResId(Context context, Country country) {
        return getMipmapResId(context, country.getIsoCode().toLowerCase(Locale.ENGLISH) + "_flag");
    }

    static int getMipmapResId(Context context, String drawableName) {
        return context.getResources().getIdentifier(
                drawableName.toLowerCase(Locale.ENGLISH), "mipmap", context.getPackageName());
    }

    static JSONObject getCountriesJSON(Context context) {
        InputStream stream = context.getResources().openRawResource(R.raw.pi_countries_dialing_code);

        try {
            return new JSONObject(convertStreamToString(stream));
        } catch (JSONException e) {
            Log.e(TAG, "failed to read countries JSON", e);
        }

        return null;
    }

    static List<Country> parseCountries(JSONObject jsonCountries) {
        List<Country> countries = new ArrayList<>();
        if (jsonCountries == null) {
            return countries;
        }

        Iterator<String> iter = jsonCountries.keys();

        while (iter.hasNext()) {
            String key = iter.next();
            try {
                String value = (String) jsonCountries.get(key);
                countries.add(new Country(key, value));
            } catch (JSONException e) {
                Log.e(TAG, "failed to parse countries", e);
            }
        }
        return countries;
    }

    static String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    static void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

}
