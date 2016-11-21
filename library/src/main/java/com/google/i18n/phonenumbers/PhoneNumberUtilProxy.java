package com.google.i18n.phonenumbers;

import android.text.TextUtils;

import java.util.*;

public class PhoneNumberUtilProxy {

    public static Map<Integer, List<String>> getCountryCodeToRegionCodeMap() {
        return CountryCodeToRegionCodeMap.getCountryCodeToRegionCodeMap();
    }

    static Phonemetadata.PhoneNumberDesc getPhoneNumberDesc(PhoneNumberUtil phoneNumberUtil, String regionCode, PhoneNumberUtil.PhoneNumberType type) {
        if (TextUtils.isEmpty(regionCode)) {
            return null;
        }

        Phonemetadata.PhoneMetadata phoneMetadata = phoneNumberUtil.getMetadataForRegion(regionCode);
        if (phoneMetadata == null) {
            return null;
        }

        return phoneNumberUtil.getNumberDescByType(phoneMetadata, type);
    }

    public static List<Integer> getPossibleLengthList(PhoneNumberUtil phoneNumberUtil, String regionCode, PhoneNumberUtil.PhoneNumberType type) {
        Phonemetadata.PhoneNumberDesc desc = getPhoneNumberDesc(phoneNumberUtil, regionCode, type);
        List<Integer> result = new ArrayList<>();
        if (desc != null) {
            List<Integer> possibleLengthList = desc.getPossibleLengthList();
            if (possibleLengthList != null) {
                result.addAll(possibleLengthList);
            }
        }
        return result;
    }

    public static Integer getMaxLength(PhoneNumberUtil phoneNumberUtil, String regionCode, PhoneNumberUtil.PhoneNumberType type) {
        List<Integer> lengthList = getPossibleLengthList(phoneNumberUtil, regionCode, type);
        if (lengthList.isEmpty()) return null;

        Collections.sort(lengthList, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return lhs.compareTo(rhs);
            }
        });
        return lengthList.get(lengthList.size() - 1);
    }

}
