package com.shaubert.ui.phone;

import android.util.Log;
import com.google.i18n.phonenumbers.CountryCodeToRegionCodeMap;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

class CountriesBuilder {

    public static final String TAG = CountriesBuilder.class.getSimpleName();

    private static Field countryCallingCodeToRegionCodeMapField;
    private static Method getCountryCodeToRegionCodeMapMethod;
    private static Map<String, Integer> regionToCodeMap;

    static int getCountyCode(String countryIso) {
        Map<String, Integer> codesWithReflection = getCountryCodesWithReflection();
        if (codesWithReflection != null) {
            return codesWithReflection.get(countryIso);
        }

        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        return phoneNumberUtil.getCountryCodeForRegion(countryIso);
    }

    static List<Country> createCountriesList(boolean loadCountryCodes) {
        List<Country> countries = null;
        if (loadCountryCodes) {
            countries = createCountriesListWithReflection();
        }
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
            countries.add(new Country(region));
        }
        return countries;
    }

    @SuppressWarnings("unchecked")
    static List<Country> createCountriesListWithReflection() {
        Map<String, Integer> regionToCodeMap = getCountryCodesWithReflection();
        if (regionToCodeMap == null) {
            return null;
        }

        List<Country> countries = new ArrayList<>(regionToCodeMap.size());
        for (Map.Entry<String, Integer> entry : regionToCodeMap.entrySet()) {
            countries.add(new Country(entry.getKey(), entry.getValue()));
        }

        return countries;
    }

    static synchronized Map<String, Integer> getCountryCodesWithReflection() {
        if (regionToCodeMap != null) {
            return regionToCodeMap;
        }

        Map<Integer, List<String>> countryCodeToRegionCodeMap = getCountyCodeToRegionCodeMap();
        if (countryCodeToRegionCodeMap == null) {
            return null;
        }

        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        Set<Integer> globalNetworkCallingCodes = phoneNumberUtil.getSupportedGlobalNetworkCallingCodes();

        regionToCodeMap = new HashMap<>();
        for (Map.Entry<Integer, List<String>> entry : countryCodeToRegionCodeMap.entrySet()) {
            Integer code = entry.getKey();
            if (globalNetworkCallingCodes.contains(code)) {
                continue;
            }

            List<String> regions = entry.getValue();
            for (String region : regions) {
                regionToCodeMap.put(region, code);
            }
        }

        return regionToCodeMap;
    }

    @SuppressWarnings("unchecked")
    private static Map<Integer, List<String>> getCountyCodeToRegionCodeMap() {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        try {
            if (countryCallingCodeToRegionCodeMapField == null) {
                countryCallingCodeToRegionCodeMapField = PhoneNumberUtil.class.getDeclaredField("countryCallingCodeToRegionCodeMap");
            }
            if (countryCallingCodeToRegionCodeMapField != null) {
                countryCallingCodeToRegionCodeMapField.setAccessible(true);
                return (Map<Integer, List<String>>) countryCallingCodeToRegionCodeMapField.get(phoneNumberUtil);
            }
        } catch (Exception ex) {
            Log.d(TAG, "failed to fast load countries list from field");
        }

        try {
            if (getCountryCodeToRegionCodeMapMethod == null) {
                getCountryCodeToRegionCodeMapMethod = CountryCodeToRegionCodeMap.class.getDeclaredMethod("getCountryCodeToRegionCodeMap");
            }
            if (getCountryCodeToRegionCodeMapMethod != null) {
                getCountryCodeToRegionCodeMapMethod.setAccessible(true);
                return (Map<Integer, List<String>>) getCountryCodeToRegionCodeMapMethod.invoke(CountryCodeToRegionCodeMap.class);
            }
        } catch (Exception ex) {
            Log.d(TAG, "failed to fast load countries list from CountryCodeToRegionCodeMap");
        }

        return null;
    }

}
