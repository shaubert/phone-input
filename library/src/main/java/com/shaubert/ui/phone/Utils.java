package com.shaubert.ui.phone;

import androidx.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Utils {

    static int getDigitsBefore(String text, int pos) {
        int digitsBefore = 0;
        for (int i = 0; i < Math.min(pos, text.length()); i++) {
            if (Character.isDigit(text.charAt(i))) {
                digitsBefore++;
            }
        }
        return digitsBefore;
    }

    static int getDigitsBeforePos(String text, int digitsBefore) {
        int pos = 0;
        for (; pos < text.length() && digitsBefore > 0; pos++) {
            if (Character.isDigit(text.charAt(pos))) {
                digitsBefore--;
            }
        }
        return pos;
    }

    static int getDigitsDifference(@Nullable String oldVal, @Nullable String newVal) {
        int newCount = newVal == null ? 0 : getDigitsBefore(newVal, newVal.length());
        int oldCount = oldVal == null ? 0 : getDigitsBefore(oldVal, oldVal.length());
        return newCount - oldCount;
    }

    static void printCountriesWithSameCode(Countries countries) {
        Map<Integer, List<Country>> map = new HashMap<>();
        for (Country country : countries.getCountries()) {
            List<Country> list = map.get(country.getCountryCode());
            if (list == null) {
                list = new ArrayList<>();
                map.put(country.getCountryCode(), list);
            }
            list.add(country);
        }

        for (Map.Entry<Integer, List<Country>> entry : map.entrySet()) {
            if (entry.getValue().size() > 1) {
                Log.d("!!!", "same code: " + entry.getKey());
                for (Country country : entry.getValue()) {
                    Log.d("!!!", country.getIsoCode() + " - " + country.getDisplayName());
                }
            }
        }
    }

}
