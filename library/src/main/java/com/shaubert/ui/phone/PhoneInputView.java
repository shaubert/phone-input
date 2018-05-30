package com.shaubert.ui.phone;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public interface PhoneInputView {

    void setMaskBuilder(MaskBuilder maskBuilder);

    Context getContext();

    @Nullable Character getMaskChar();

    void setMask(Mask mask);

    Country getDefaultCountry();

    void setCountryIso(String countryIso);

    void setCountry(Country country);

    void setPhoneNumberFormat(PhoneNumberUtil.PhoneNumberFormat phoneNumberFormat);

    PhoneNumberUtil.PhoneNumberFormat getPhoneNumberFormat();

    @Nullable String getFormattedPhoneNumber(PhoneNumberUtil.PhoneNumberFormat format);

    boolean isValidPhoneNumber();

    @Nullable Phonenumber.PhoneNumber getPhoneNumber();

    void setPhoneNumber(@Nullable Phonenumber.PhoneNumber phoneNumber);

    void setPhoneNumberString(@Nullable String phoneNumber);

    void setAutoChangeCountry(boolean autoChangeCountry);

    void setDisplayCountryCode(boolean displayCountryCode);

    void addTextChangeListener(TextChangeListener listener);

    void removeTextChangeListener(TextChangeListener listener);

    void setCountryChangeListener(CountryChangedListener countryChangeListener);

    interface TextChangeListener {
        void onTextChanged(@NonNull String text);
    }

}
