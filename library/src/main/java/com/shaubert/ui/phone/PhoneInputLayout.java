package com.shaubert.ui.phone;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class PhoneInputLayout extends LinearLayout {

    private PhoneInputView phoneInput;
    private CountryPickerView countryPicker;

    private Country country;
    private Countries countries;

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

    public CountryPickerView getCountryPicker() {
        return countryPicker;
    }

    public PhoneInputView getPhoneInput() {
        return phoneInput;
    }

}
