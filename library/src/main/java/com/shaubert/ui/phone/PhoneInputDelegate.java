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

    private Country country;
    private Country defaultCountry;
    private PhoneNumberUtil.PhoneNumberFormat phoneNumberFormat = PhoneNumberUtil.PhoneNumberFormat.NATIONAL;
    private PhoneNumberUtil phoneNumberUtil;
    private MaskBuilder maskBuilder;

    private PhoneInputView phoneInput;

    public PhoneInputDelegate(PhoneInputView phoneInput, AttributeSet attrs) {
        this.phoneInput = phoneInput;
        maskBuilder = new DefaultMaskBuilder(phoneInput);
        init(attrs);
    }

    public void setMaskBuilder(MaskBuilder maskBuilder) {
        this.maskBuilder = maskBuilder;
        refreshMask();
    }

    private void init(AttributeSet attrs) {
        countries = Countries.get(getContext());
        phoneNumberUtil = PhoneNumberUtil.getInstance();
        setupDefaultCountry();

        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.pi_PhoneInputStyle);

            String countryIso = typedArray.getString(R.styleable.pi_PhoneInputStyle_countryIso);
            Country country = countries.getCountryByIso(countryIso);
            if (country != null) {
                setCountry(country);
            }

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

            typedArray.recycle();
        }
    }

    private void setupDefaultCountry() {
        String[] possibleRegions = Phones.getPossibleRegions(getContext());
        for (String region : possibleRegions) {
            Country country = countries.getCountryByIso(region);
            if (country != null) {
                setDefaultCountry(country);
                break;
            }
        }
    }

    public Country getDefaultCountry() {
        return defaultCountry;
    }

    private void setDefaultCountry(Country country) {
        this.defaultCountry = country;
        this.country = country;
        refreshMask();
    }

    public void setCountryIso(String countryIso) {
        setCountry(countries.getCountryByIso(countryIso));
    }

    public void setCountry(Country country) {
        this.country = country;
        refreshMask();
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
            Country country = countries.getCountryByIso(ss.countryIso);
            if (country != null) {
                setCountry(country);
            }
        }
    }

    public String getFormattedPhoneNumber(String text, PhoneNumberUtil.PhoneNumberFormat format) {
        Phonenumber.PhoneNumber phoneNumber = getPhoneNumber(text);
        if (phoneNumber != null) {
            return phoneNumberUtil.format(phoneNumber, format);
        }
        return null;
    }

    public boolean isValidPhoneNumber(String text) {
        Phonenumber.PhoneNumber phoneNumber = getPhoneNumber(text);
        return phoneNumber != null && phoneNumberUtil.isValidNumber(phoneNumber);
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

        return phoneNumberUtil.format(phoneNumber, phoneNumberFormat);
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
