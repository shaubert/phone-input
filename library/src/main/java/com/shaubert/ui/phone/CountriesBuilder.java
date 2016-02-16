package com.shaubert.ui.phone;

import android.util.Log;
import com.google.i18n.phonenumbers.CountryCodeToRegionCodeMap;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

class CountriesBuilder {

    public static final String TAG = CountriesBuilder.class.getSimpleName();

    static List<Country> createCountriesList() {
        List<Country> countries = createCountriesListWithReflection();
        if (countries == null) {
            countries = createCountriesListWithMetadata();
        }
        return countries;
    }

    static List<Country> createCountriesListWithMetadata() {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        Set<String> regions = phoneNumberUtil.getSupportedRegions();
        List<Country> countries = new ArrayList<>(regions.size());
        for (String region : regions) {
            countries.add(new Country(region, phoneNumberUtil.getCountryCodeForRegion(region)));
        }
        return countries;
    }

    @SuppressWarnings("unchecked")
    static List<Country> createCountriesListWithReflection() {
        Map<Integer, List<String>> countryCodeToRegionCodeMap = null;
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

        try {
            Field field = PhoneNumberUtil.class.getDeclaredField("countryCallingCodeToRegionCodeMap");
            if (field != null) {
                field.setAccessible(true);
                countryCodeToRegionCodeMap = (Map<Integer, List<String>>) field.get(phoneNumberUtil);
            }
        } catch (Exception ex) {
            Log.d(TAG, "failed to fast load countries list from field");
        }

        if (countryCodeToRegionCodeMap == null) {
            try {
                Method method = CountryCodeToRegionCodeMap.class.getDeclaredMethod("getCountryCodeToRegionCodeMap");
                if (method != null) {
                    method.setAccessible(true);
                    countryCodeToRegionCodeMap = (Map<Integer, List<String>>) method.invoke(CountryCodeToRegionCodeMap.class);
                }
            } catch (Exception ex) {
                Log.d(TAG, "failed to fast load countries list from CountryCodeToRegionCodeMap");
            }
        }

        if (countryCodeToRegionCodeMap == null) {
            return null;
        }

        Map<String, Integer> regionToCodeMap = new HashMap<>();
        for (Map.Entry<Integer, List<String>> entry : countryCodeToRegionCodeMap.entrySet()) {
            Integer code = entry.getKey();
            if (phoneNumberUtil.getSupportedGlobalNetworkCallingCodes().contains(code)) {
                continue;
            }

            List<String> regions = entry.getValue();
            for (String region : regions) {
                regionToCodeMap.put(region, code);
            }
        }

        List<Country> countries = new ArrayList<>(regionToCodeMap.size());
        for (Map.Entry<String, Integer> entry : regionToCodeMap.entrySet()) {
            countries.add(new Country(entry.getKey(), entry.getValue()));
        }

        return countries;
    }

}
