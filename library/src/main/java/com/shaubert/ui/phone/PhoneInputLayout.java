package com.shaubert.ui.phone;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public class PhoneInputLayout extends LinearLayout {

    private PhoneInputView phoneInput;
    private CountryPickerView countryPicker;

    private Country country;
    @Nullable
    private Countries countries;
    private boolean customCountries;
    private boolean autoSetDefaultCountry = true;

    private boolean innerCountryChange;

    public PhoneInputLayout(Context context) {
        super(context);
        init(null, 0);
    }

    public PhoneInputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public PhoneInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PhoneInputLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(
                    attrs,
                    R.styleable.pi_PhoneInputLayoutStyle,
                    defStyleAttr,
                    0
            );

            customCountries = typedArray.getBoolean(R.styleable.pi_PhoneInputLayoutStyle_pi_customCountries, false);
            autoSetDefaultCountry = typedArray.getBoolean(R.styleable.pi_PhoneInputLayoutStyle_pi_autoSetDefaultCountry, autoSetDefaultCountry);
            typedArray.recycle();
        }

        if (!customCountries) {
            loadCountries();
        }
    }

    public void setCustomCountries(@Nullable Countries countries) {
        if (!customCountries && countries == null) {
            return;
        }

        this.countries = countries;
        this.customCountries = countries != null;

        if (phoneInput != null) {
            phoneInput.setCustomCountries(countries);
        }
        if (countryPicker != null) {
            countryPicker.setCustomCountries(countries);
        }

        if (countries != null) {
            setupPossibleRegion();
        } else {
            loadCountries();
        }
    }

    private void loadCountries() {
        Countries.get(new Countries.Callback() {
            @Override
            public void onLoaded(Countries loadedCountries) {
                if (!customCountries) {
                    countries = loadedCountries;
                    setupPossibleRegion();
                }
            }
        });
    }

    public void setAutoSetDefaultCountry(boolean autoSetDefaultCountry) {
        this.autoSetDefaultCountry = autoSetDefaultCountry;
    }

    private void setupPossibleRegion() {
        if (country != null || countries == null || !autoSetDefaultCountry) {
            return;
        }

        String[] possibleRegions = Phones.getPossibleRegions(getContext());
        for (String region : possibleRegions) {
            Country country = countries.getCountryByIso(region);
            if (country != null) {
                setCountry(country);
                break;
            }
        }

        if (country == null && !countries.getCountries().isEmpty()) {
            setCountry(countries.getCountries().get(0));
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
        if (customCountries && countries != null) {
            this.phoneInput.setCustomCountries(countries);
        }
        this.phoneInput.setCountry(country);
        this.phoneInput.setCountryChangeListener(new CountryChangedListener() {
            @Override
            public void onCountryChanged(@Nullable Country country, boolean fromUser) {
                if (innerCountryChange) return;

                if (fromUser) {
                    setCountry(country);
                }
            }
        });
    }

    private void setCountryPicker(CountryPickerView countryPicker) {
        if (this.countryPicker != null) {
            throw new IllegalStateException("CountryPickerView was added before");
        }
        this.countryPicker = countryPicker;
        if (customCountries && countries != null) {
            this.countryPicker.setCustomCountries(countries);
        }
        this.countryPicker.setCountry(country);
        this.countryPicker.setCountryChangedListener(new CountryChangedListener() {
            @Override
            public void onCountryChanged(@Nullable Country country, boolean fromUser) {
                if (innerCountryChange) return;

                setCountry(country);
                if (phoneInput != null && fromUser) ((View) phoneInput).requestFocus();
            }
        });
    }

    public void setCountry(Country country) {
        if (this.country == country || (this.country != null && this.country.equals(country))) {
            return;
        }
        this.country = country;

        innerCountryChange = true;
        if (phoneInput != null) {
            phoneInput.setCountry(country);
        }
        if (countryPicker != null) {
            countryPicker.setCountry(country);
        }
        innerCountryChange = false;
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
        Phonenumber.PhoneNumber phoneNumber = PhoneInputDelegate.getPhoneNumber(phoneNumberStr, country, false);
        if (phoneNumber != null) {
            setPhoneNumber(phoneNumber);
        } else {
            if (phoneInput != null) phoneInput.setPhoneNumberString(phoneNumberStr);
        }
    }

    public void setPhoneNumber(Phonenumber.PhoneNumber phoneNumber) {
        if (phoneNumber != null) {
            Country country = Phones.getCountyFromPhone(phoneNumber);
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
