package com.shaubert.ui.phone;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtilProxy;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.Locale;

public class DefaultMaskBuilder implements MaskBuilder {

    private static final String DEFAULT_MASK = "123456789012345";

    private PhoneInputView phoneInput;
    private PhoneNumberUtil phoneNumberUtil;


    public DefaultMaskBuilder(PhoneInputView phoneInput) {
        this.phoneInput = phoneInput;
        phoneNumberUtil = PhoneNumberUtil.getInstance();
    }

    @Override
    public Mask getMask(Country country, PhoneNumberUtil.PhoneNumberFormat phoneNumberFormat) {
        if (country == null) {
            return Mask.EMPTY;
        }

        String region = country.getIsoCode().toUpperCase(Locale.US);
        Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.getExampleNumberForType(region,
                PhoneNumberUtil.PhoneNumberType.MOBILE);
        String formattedNumber;
        if (phoneNumber != null) {
            formattedNumber = phoneNumberUtil.format(phoneNumber, phoneNumberFormat);
        } else {
            Integer maxLength = PhoneNumberUtilProxy.getMaxLength(
                    phoneNumberUtil, region, PhoneNumberUtil.PhoneNumberType.MOBILE);
            formattedNumber = getFixedPart(country, phoneNumberFormat)
                    + " "
                    + DEFAULT_MASK.substring(0, maxLength != null ? maxLength : DEFAULT_MASK.length());
        }

        Character maskChar = phoneInput.getMaskChar();
        String mask = null;
        if (maskChar != null) {
            String fixedPart = getFixedPart(country, phoneNumberFormat);
            StringBuilder maskBuilder = new StringBuilder(formattedNumber);
            int digitsCount = 0;
            for (int i = fixedPart.length(); i < maskBuilder.length(); i++) {
                char ch = maskBuilder.charAt(i);
                if (Character.isDigit(ch)) {
                    digitsCount++;
                    maskBuilder.replace(i, i + 1, maskChar.toString());
                }
            }

            Integer maxLength = PhoneNumberUtilProxy.getMaxLength(
                    phoneNumberUtil, region, PhoneNumberUtil.PhoneNumberType.MOBILE);
            if (maxLength != null && digitsCount < maxLength) {
                for (int i = digitsCount; i < maxLength; i++) {
                    maskBuilder.append(maskChar.toString());
                }
            }

            mask = maskBuilder.toString();
        }

        return new Mask(
                mask,
                getFixedPart(country, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL),
                formattedNumber);
    }

    private String getFixedPart(Country country, PhoneNumberUtil.PhoneNumberFormat format) {
        int countryCode = phoneNumberUtil.getCountryCodeForRegion(country.getIsoCode().toUpperCase(Locale.US));
        switch (format) {
            case E164:
            case INTERNATIONAL:
            case RFC3966:
                return "+" + countryCode;

            case NATIONAL:
                return "";
        }
        return "";
    }

    @Override
    public String getValueForMask(Phonenumber.PhoneNumber phoneNumber, Country country, PhoneNumberUtil.PhoneNumberFormat phoneNumberFormat) {
        if (phoneNumber == null) {
            return null;
        }

        String fixedCountryCode = getFixedPart(country, phoneNumberFormat);
        String formattedNumber = phoneNumberUtil.format(phoneNumber, phoneNumberFormat);
        return PhoneNumberUtil.normalizeDigitsOnly(formattedNumber.replace(fixedCountryCode, ""));
    }

}
