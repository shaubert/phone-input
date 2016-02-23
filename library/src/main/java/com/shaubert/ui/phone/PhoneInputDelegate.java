package com.shaubert.ui.phone;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.Locale;

public class PhoneInputDelegate {

    private Countries countries;
    private String countryFromAttrs;
    private String restoredCountry;

    private Country country;
    private Country defaultCountry;
    private PhoneNumberUtil.PhoneNumberFormat phoneNumberFormat = PhoneNumberUtil.PhoneNumberFormat.NATIONAL;
    private PhoneNumberUtil phoneNumberUtil;
    private MaskBuilder maskBuilder;

    private PhoneInputView phoneInput;

    public PhoneInputDelegate(PhoneInputView phoneInput) {
        this.phoneInput = phoneInput;
        maskBuilder = new DefaultMaskBuilder(phoneInput);
    }

    public void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.pi_PhoneInputStyle);

            int formatInt = typedArray.getInt(R.styleable.pi_PhoneInputStyle_phoneNumberFormat, -1);
            switch (formatInt) {
                case 0:
                    phoneNumberFormat = PhoneNumberUtil.PhoneNumberFormat.E164;
                    break;
                case 1:
                    phoneNumberFormat = PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL;
                    break;
                case 2:
                    phoneNumberFormat = PhoneNumberUtil.PhoneNumberFormat.NATIONAL;
                    break;
                case 3:
                    phoneNumberFormat = PhoneNumberUtil.PhoneNumberFormat.RFC3966;
                    break;
            }

            countryFromAttrs = typedArray.getString(R.styleable.pi_PhoneInputStyle_countryIso);
            typedArray.recycle();
        }

        Countries.get(getContext(), new Countries.Callback() {
            @Override
            public void onLoaded(Countries loadedCountries) {
                countries = loadedCountries;
                phoneNumberUtil = PhoneNumberUtil.getInstance();
                onCountriesLoaded();
            }
        });
    }

    private void onCountriesLoaded() {
        if (defaultCountry == null) {
            setupDefaultCountry();
        }

        processRestoredCountry();

        if (this.country != null) {
            return;
        }

        Country country = countries.getCountryByIso(countryFromAttrs);
        if (country != null) {
            setCountry(country);
        }

        if (this.country == null) {
            setCountry(defaultCountry);
        }
    }

    private void processRestoredCountry() {
        if (!TextUtils.isEmpty(restoredCountry)) {
            Country country = countries.getCountryByIso(restoredCountry);
            restoredCountry = null;
            if (country != null) {
                setCountry(country);
            }
        }
    }

    private void setupDefaultCountry() {
        String[] possibleRegions = Phones.getPossibleRegions(getContext());
        for (String region : possibleRegions) {
            Country country = countries.getCountryByIso(region);
            if (country != null) {
                this.defaultCountry = country;
                break;
            }
        }
    }

    public void setMaskBuilder(MaskBuilder maskBuilder) {
        if (this.maskBuilder == maskBuilder || (this.maskBuilder != null && this.maskBuilder.equals(maskBuilder))) {
            return;
        }
        this.maskBuilder = maskBuilder;
        refreshMask();
    }

    public Country getDefaultCountry() {
        return defaultCountry;
    }

    public void setCountryIso(String countryIso) {
        setCountry(countries.getCountryByIso(countryIso));
    }

    public void setCountry(Country country) {
        if (this.country == country || (this.country != null && this.country.equals(country))) {
            return;
        }
        this.country = country;
        refreshMask();
    }

    public Country getCountry() {
        return country;
    }

    public PhoneNumberUtil.PhoneNumberFormat getPhoneNumberFormat() {
        return phoneNumberFormat;
    }

    public void setPhoneNumberFormat(PhoneNumberUtil.PhoneNumberFormat phoneNumberFormat) {
        if (this.phoneNumberFormat != phoneNumberFormat) {
            this.phoneNumberFormat = phoneNumberFormat;
            refreshMask();
        }
    }

    public void refreshMask() {
        phoneInput.setMask(getMaskFromCountry(country));
    }

    private String getMaskFromCountry(Country country) {
        if (maskBuilder == null) {
            return null;
        } else {
            return maskBuilder.getMask(country, phoneNumberFormat);
        }
    }

    public String getValueForMask(Phonenumber.PhoneNumber phoneNumber) {
        if (maskBuilder != null) {
            return maskBuilder.getValueForMask(phoneNumber, country, phoneNumberFormat);
        } else {
            return formatPhoneNumber(phoneNumber);
        }
    }

    private Context getContext() {
        return phoneInput.getContext();
    }

    public Parcelable dispatchOnSaveInstanceState(Parcelable superState) {
        SavedState ss = new SavedState(superState);
        ss.countryIso = country != null ? country.getIsoCode() : null;
        return ss;
    }

    public void dispatchOnRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        if (!TextUtils.isEmpty(ss.countryIso)) {
            restoredCountry = ss.countryIso;
            if (countries != null) {
                processRestoredCountry();
            }
        }
    }

    public String getFormattedPhoneNumber(String text, PhoneNumberUtil.PhoneNumberFormat format) {
        Phonenumber.PhoneNumber phoneNumber = getPhoneNumber(text);
        if (phoneNumber != null) {
            return getPhoneNumberUtil().format(phoneNumber, format);
        }
        return null;
    }

    private PhoneNumberUtil getPhoneNumberUtil() {
        if (phoneNumberUtil == null) {
            phoneNumberUtil = PhoneNumberUtil.getInstance();
        }
        return phoneNumberUtil;
    }

    public boolean isValidPhoneNumber(String text) {
        Phonenumber.PhoneNumber phoneNumber = getPhoneNumber(text);
        return phoneNumber != null && getPhoneNumberUtil().isValidNumber(phoneNumber);
    }

    public Phonenumber.PhoneNumber getPhoneNumber(String text) {
        return getPhoneNumber(text, country);
    }

    public static Phonenumber.PhoneNumber getPhoneNumber(@Nullable String text, @Nullable Country country) {
        if (country == null) {
            return null;
        }

        if (TextUtils.isEmpty(text)) {
            return null;
        }

        try {
            return PhoneNumberUtil.getInstance().parse(text, country.getIsoCode().toUpperCase(Locale.US));
        } catch (NumberParseException ignored) {
        }
        return null;
    }

    public String formatPhoneNumber(Phonenumber.PhoneNumber phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }

        return getPhoneNumberUtil().format(phoneNumber, phoneNumberFormat);
    }

    public void setCountryFromPhoneNumber(String phoneNumber) {
        Phonenumber.PhoneNumber number = getPhoneNumber(phoneNumber);
        if (number != null) {
            setCountryFromPhoneNumber(number);
        }
    }

    public void setCountryFromPhoneNumber(Phonenumber.PhoneNumber phoneNumber) {
        if (phoneNumber != null) {
            Country country = Phones.getCountyFromPhone(phoneNumber, getContext());
            if (country != null) {
                setCountry(country);
            }
        }
    }

    public static class SavedState extends View.BaseSavedState {
        String countryIso;

        SavedState(Parcelable superState) {
            super(superState);
        }

        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel in) {
            super(in);
            countryIso = in.readString();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeString(countryIso);
        }

        @Override
        public String toString() {
            return "PhoneInputDelegate.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " countryIso=" + countryIso + "}";
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

}
