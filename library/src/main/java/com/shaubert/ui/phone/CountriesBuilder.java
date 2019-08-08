package com.shaubert.ui.phone;

import android.content.Context;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtilProxy;

import java.util.*;

class CountriesBuilder {

    private static Map<String, Integer> regionToCodeMap;

    static Integer getCountyCode(String countryIso) {
        Map<String, Integer> codesWithReflection = getCountryCodesWithProxy();
        if (codesWithReflection != null) {
            return codesWithReflection.get(countryIso);
        }

        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        return phoneNumberUtil.getCountryCodeForRegion(countryIso);
    }

    static List<Country> createCountriesList(Context context, boolean loadCountryCodes) {
        List<Country> countries = null;
        if (loadCountryCodes) {
            countries = createCountriesListWithProxy(context);
        }
        if (countries == null) {
            countries = createCountriesListWithMetadata(context);
        }
        return countries;
    }

    static List<Country> createCountriesListWithMetadata(Context context) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        Set<String> regions = phoneNumberUtil.getSupportedRegions();
        List<Country> countries = new ArrayList<>(regions.size());
        for (String region : regions) {
            countries.add(new Country(region, context));
        }
        return countries;
    }

    @SuppressWarnings("unchecked")
    static List<Country> createCountriesListWithProxy(Context context) {
        Map<String, Integer> regionToCodeMap = getCountryCodesWithProxy();
        if (regionToCodeMap == null) {
            return null;
        }

        List<Country> countries = new ArrayList<>(regionToCodeMap.size());
        for (Map.Entry<String, Integer> entry : regionToCodeMap.entrySet()) {
            if (entry.getValue() == null) continue;
            countries.add(new Country(entry.getKey(), entry.getValue(), context));
        }

        return countries;
    }

    static synchronized Map<String, Integer> getCountryCodesWithProxy() {
        if (regionToCodeMap != null) {
            return regionToCodeMap;
        }

        Map<Integer, List<String>> countryCodeToRegionCodeMap = getCountyCodeToRegionCodeMap();
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        Set<Integer> globalNetworkCallingCodes = phoneNumberUtil.getSupportedGlobalNetworkCallingCodes();

        regionToCodeMap = new HashMap<>();
        for (Map.Entry<Integer, List<String>> entry : countryCodeToRegionCodeMap.entrySet()) {
            Integer code = entry.getKey();
            if (globalNetworkCallingCodes.contains(code) || code == null) {
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
        return PhoneNumberUtilProxy.getCountryCodeToRegionCodeMap();
    }

}
