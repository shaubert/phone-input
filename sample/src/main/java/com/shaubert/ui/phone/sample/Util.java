package com.shaubert.ui.phone.sample;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.shaubert.ui.phone.Countries;
import com.shaubert.ui.phone.Country;

import java.util.List;
import java.util.Random;

class Util {

    public static Phonenumber.PhoneNumber getRandomPhone(Countries countries) {
        List<Country> countriesList = countries.getCountries();
        Country country = countriesList.get(new Random().nextInt(countriesList.size()));
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber phoneNumber =
                phoneNumberUtil.getExampleNumberForType(country.getIsoCode(), PhoneNumberUtil.PhoneNumberType.MOBILE);
        if (phoneNumber == null) {
            phoneNumber = phoneNumberUtil.getExampleNumber(country.getIsoCode());
        }
        return phoneNumber;
    }

}
