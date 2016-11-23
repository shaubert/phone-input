package com.shaubert.ui.phone;

import android.content.Context;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.Locale;

public class Phones {

    public static String[] getPossibleRegions(Context context) {
        return new String[] {
                getRegionFromSim(context),
                getRegionFromNetwork(context),
                getRegionFromLocale(),
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
        String regionCode = PhoneNumberUtil.getInstance().getRegionCodeForCountryCode(phoneNumber.getCountryCode());
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
}
