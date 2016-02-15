package com.shaubert.ui.phone;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public class PhoneInputLayout extends LinearLayout {

    private PhoneInputView phoneInput;
    private CountryPickerView countryPicker;

    private Country country;
    private Countries countries;
    private PhoneNumberUtil phoneNumberUtil;

    public PhoneInputLayout(Context context) {
        super(context);
        init();
    }

    public PhoneInputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PhoneInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PhoneInputLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        countries = Countries.get(getContext());
        phoneNumberUtil = PhoneNumberUtil.getInstance();

        String[] possibleRegions = Phones.getPossibleRegions(getContext());
        for (String region : possibleRegions) {
            country = countries.getCountryByIso(region);
            if (country != null) {
                break;
            }
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);

        if (child instanceof PhoneInputView) {
            setPhoneInput((PhoneInputView) child);
        } else if (child instanceof CountryPickerView) {
            setCountryPicker((CountryPickerView) child);
        }
    }

    private void setPhoneInput(PhoneInputView phoneInput) {
        if (this.phoneInput != null) {
            throw new IllegalStateException("PhoneInput was added before");
        }
        this.phoneInput = phoneInput;
        this.phoneInput.setCountry(country);
    }

    private void setCountryPicker(CountryPickerView countryPicker) {
        if (this.countryPicker != null) {
            throw new IllegalStateException("CountryPickerView was added before");
        }
        this.countryPicker = countryPicker;
        this.countryPicker.setCountry(country);
        this.countryPicker.setOnCountryChangedListener(new CountryPickerView.OnCountryChangedListener() {
            @Override
            public void onCountryChanged(@Nullable Country country) {
                setCountry(country);
            }
        });
    }

    public void setCountry(Country country) {
        this.country = country;
        if (phoneInput != null) {
            phoneInput.setCountry(country);
        }
        if (countryPicker != null) {
            countryPicker.setCountry(country);
        }
    }

    public Country getCountry() {
        return country;
    }

    public void setPhoneNumberFormat(PhoneNumberUtil.PhoneNumberFormat phoneNumberFormat) {
        if (phoneInput != null) phoneInput.setPhoneNumberFormat(phoneNumberFormat);
    }

    public PhoneNumberUtil.PhoneNumberFormat getPhoneNumberFormat() {
        return phoneInput != null ? phoneInput.getPhoneNumberFormat() : null;
    }

    public String getFormattedPhoneNumber(PhoneNumberUtil.PhoneNumberFormat format) {
        return phoneInput != null ? phoneInput.getFormattedPhoneNumber(format) : null;
    }

    public boolean isValidPhoneNumber() {
        return phoneInput != null && phoneInput.isValidPhoneNumber();
    }

    public void setPhoneNumberString(String phoneNumberStr) {
        Phonenumber.PhoneNumber phoneNumber = PhoneInputDelegate.getPhoneNumber(phoneNumberStr, country);
        if (phoneNumber != null) {
            setPhoneNumber(phoneNumber);
        } else {
            if (phoneInput != null) phoneInput.setPhoneNumberString(phoneNumberStr);
        }
    }

    public void setPhoneNumber(Phonenumber.PhoneNumber phoneNumber) {
        if (phoneNumber != null) {
            String regionCode = phoneNumberUtil.getRegionCodeForCountryCode(phoneNumber.getCountryCode());
            Country country = countries.getCountryByIso(regionCode);
            if (country != null) {
                setCountry(country);
            }
        }
        if (phoneInput != null) phoneInput.setPhoneNumber(phoneNumber);
    }

    public Phonenumber.PhoneNumber getPhoneNumber() {
        return phoneInput != null ? phoneInput.getPhoneNumber() : null;
    }

    public CountryPickerView getCountryPicker() {
        return countryPicker;
    }

    public PhoneInputView getPhoneInput() {
        return phoneInput;
    }

}
