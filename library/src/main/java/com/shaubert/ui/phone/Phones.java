package com.shaubert.ui.phone;

import android.content.Context;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.Locale;

public class Phones {

    private static String FALLBACK_COUNTRY = "US";

    public static String getFallbackCountry() {
        return FALLBACK_COUNTRY;
    }

    public static void setFallbackCountry(String fallbackCountry) {
        FALLBACK_COUNTRY = fallbackCountry;
    }

    public static String[] getPossibleRegions(Context context) {
        return getPossibleRegions(context, FALLBACK_COUNTRY);
    }

    public static String[] getPossibleRegions(Context context, String defaultCountry) {
        return new String[] {
                getRegionFromSim(context),
                getRegionFromNetwork(context),
                getRegionFromLocale(),
                defaultCountry
        };
    }

    public static String getRegionFromSim(Context context) {
        try {
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return manager.getSimCountryIso();
        } catch (Exception ignored) {
        }
        return null;
    }

    public static String getRegionFromNetwork(Context context) {
        try {
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return manager.getNetworkCountryIso();
        } catch (Exception ignored) {
        }
        return null;
    }

    public static String getRegionFromLocale() {
        return Locale.getDefault().getCountry();
    }

    public static Country getCountyFromPhone(Phonenumber.PhoneNumber phoneNumber, Context context) {
        String regionCode = PhoneNumberUtil.getInstance().getRegionCodeForNumber(phoneNumber);
        return Countries.get(context).getCountryByIso(regionCode);
    }

    public static @Nullable Phonenumber.PhoneNumber parse(String phone, @Nullable Country country) {
        try {
            String region = country != null ? country.getIsoCode().toUpperCase(Locale.US) : getRegionFromLocale();
            return PhoneNumberUtil.getInstance().parse(phone, region);
        } catch (NumberParseException ignored) {
        }
        return null;
    }

    /**
     * country in [] marked as top in this group
     * code: 590 = St. Martin, Guadeloupe, [St. Barthélemy/BL]
     * code: 61 = Christmas Island, [Australia/AU], Cocos (Keeling) Islands
     * code: 262 = [Mayotte/YT], Réunion
     * code: 358 = [Finland/FI], Åland Islands
     * code: 7 = [Russia/RU], Kazakhstan
     * code: 47 = Svalbard &amp; Jan Mayen, [Norway/NO]
     * code: 44 = [United Kingdom/GB], Jersey, Guernsey, Isle of Man
     * code: 212 = Western Sahara, [Morocco/MA]
     * code: 290 = Tristan da Cunha, [St. Helena/SH]
     * code: 1 = U.S. Virgin Islands, American Samoa, Guam, Bahamas,
     * Antigua &amp; Barbuda, St. Lucia, St. Kitts &amp; Nevis, Montserrat,
     * Canada, [United States/US], Grenada, Northern Mariana Islands,
     * Trinidad &amp; Tobago, Anguilla, British Virgin Islands,
     * Turks &amp; Caicos Islands, Barbados, St. Vincent &amp; Grenadines,
     * Bermuda, Sint Maarten, Dominica, Jamaica, Puerto Rico, Cayman Islands
     * Dominican Republic
     * code: 599 = Curaçao, [Caribbean Netherlands/BQ]
     * code: 39 = Vatican City, [Italy/IT]
     *
     * @param code country code
     * @return top country with that code
     */
    @Nullable
    public static Country getTopCountryWithCode(int code, Countries countries) {
        switch (code) {
            case 590: return countries.getCountryByIso("BL");
            case 61: return countries.getCountryByIso("AU");
            case 262: return countries.getCountryByIso("YT");
            case 358: return countries.getCountryByIso("FI");
            case 7: return countries.getCountryByIso("RU");
            case 47: return countries.getCountryByIso("NO");
            case 44: return countries.getCountryByIso("GB");
            case 212: return countries.getCountryByIso("MA");
            case 290: return countries.getCountryByIso("SH");
            case 1: return countries.getCountryByIso("US");
            case 599: return countries.getCountryByIso("BQ");
            case 39: return countries.getCountryByIso("IT");

            default: return countries.getCountryByCode(code);
        }
    }

    public static int parseCode(String code) {
        try {
            return Integer.parseInt(PhoneNumberUtil.normalizeDigitsOnly(code));
        } catch (Exception ignored) {
        }
        return -1;
    }

}
