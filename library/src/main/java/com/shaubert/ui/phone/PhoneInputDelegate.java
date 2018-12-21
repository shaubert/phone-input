package com.shaubert.ui.phone;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class PhoneInputDelegate {

    private Countries countries;
    private String countryFromAttrs;
    private String restoredCountry;

    private Country country;
    private Country defaultCountry;
    private PhoneNumberUtil.PhoneNumberFormat phoneNumberFormat = PhoneNumberUtil.PhoneNumberFormat.NATIONAL;
    private PhoneNumberUtil phoneNumberUtil;
    private MaskBuilder maskBuilder;
    private boolean autoChangeCountry;
    private boolean displayCountryCode;

    private Handler handler = new Handler();

    private PhoneInputView phoneInput;

    private Set<PhoneInputView.TextChangeListener> listeners = new CopyOnWriteArraySet<>();
    private CountryChangedListener countryChangeListener;

    public PhoneInputDelegate(PhoneInputView phoneInput) {
        this.phoneInput = phoneInput;
        maskBuilder = new DefaultMaskBuilder(phoneInput);
    }

    public void init(AttributeSet attrs, int defStyleAttr) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(
                    attrs,
                    R.styleable.pi_PhoneInputStyle,
                    defStyleAttr,
                    0
            );

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
            autoChangeCountry = typedArray.getBoolean(R.styleable.pi_PhoneInputStyle_autoChangeCountry, false);
            displayCountryCode = typedArray.getBoolean(R.styleable.pi_PhoneInputStyle_displayCountryCode, false);
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
        setCountry(country, false);
    }

    private void setCountry(Country country, boolean fromUser) {
        if (this.country == country || (this.country != null && this.country.equals(country))) {
            return;
        }
        this.country = country;
        refreshMask();

        if (countryChangeListener != null) {
            countryChangeListener.onCountryChanged(country, fromUser);
        }
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

    public void setAutoChangeCountry(boolean autoChangeCountry) {
        this.autoChangeCountry = autoChangeCountry;
    }

    public void setDisplayCountryCode(boolean displayCountryCode) {
        if (this.displayCountryCode != displayCountryCode) {
            this.displayCountryCode = displayCountryCode;
            refreshMask();
        }
    }

    public boolean isDisplayCountryCode() {
        return displayCountryCode
                && (phoneNumberFormat == PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL
                || phoneNumberFormat == PhoneNumberUtil.PhoneNumberFormat.E164);
    }

    public void refreshMask() {
        phoneInput.setMask(getMaskFromCountry(country));
    }

    @Nullable
    public String getCountryCode(String text) {
        Country country = getTopCountry(resolveCountries(text));
        return country != null ? String.valueOf(country.getCountryCode()) : null;
    }

    public String replaceCountryCode(String text, String newCode) {
        if (newCode == null) return text;

        String oldNumber = PhoneNumberUtil.normalizeDigitsOnly(text);
        Country country = getTopCountry(resolveCountries(text));
        if (country != null) {
            String newDigits = PhoneNumberUtil.normalizeDigitsOnly(newCode);
            String oldDigits = PhoneNumberUtil.normalizeDigitsOnly(String.valueOf(country.getCountryCode()));
            if (newDigits.equals(oldDigits)) {
                return text;
            }

            return new StringBuilder(oldNumber)
                    .replace(0, oldDigits.length(), newDigits)
                    .insert(0, "+")
                    .toString();
        }

        return newCode + oldNumber;
    }

    private Mask getMaskFromCountry(Country country) {
        if (maskBuilder == null) {
            return Mask.EMPTY;
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
        return getPhoneNumber(text, country, true);
    }

    public Phonenumber.PhoneNumber parsePhoneNumber(String text) {
        return getPhoneNumber(text, country, false);
    }

    public static Phonenumber.PhoneNumber getPhoneNumber(@Nullable String text, @Nullable Country country, boolean validateAgainstCountry) {
        if (country == null) {
            return null;
        }

        if (TextUtils.isEmpty(text)) {
            return null;
        }

        try {
            String region = country.getIsoCode().toUpperCase(Locale.US);
            Phonenumber.PhoneNumber number = PhoneNumberUtil.getInstance().parse(text, region);
            if (validateAgainstCountry && PhoneNumberUtil.getInstance().isValidNumberForRegion(number, region)) {
                return number;
            } else if (!validateAgainstCountry && PhoneNumberUtil.getInstance().isValidNumber(number)) {
                return number;
            }
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
        Phonenumber.PhoneNumber number = parsePhoneNumber(phoneNumber);
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

    private void updateCountryIfNeeded(@NonNull String text) {
        if (autoChangeCountry
                && countries != null) {
            Set<Country> resolvedCountries = resolveCountries(text);
            if (!resolvedCountries.isEmpty()
                    && !resolvedCountries.contains(country)) {
                final Country topCountry = getTopCountry(resolvedCountries);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        setCountry(topCountry, true);
                    }
                });
            }
        }
    }

    @Nullable
    private Country getTopCountry(Set<Country> resolvedCountries) {
        if (resolvedCountries.isEmpty()) return null;

        return Phones.getTopCountryWithCode(
                resolvedCountries.iterator().next().getCountryCode(),
                countries);
    }

    private Set<Country> resolveCountries(@NonNull String text) {
        Set<Country> result = new HashSet<>();
        String trimmed = text.trim();
        if (trimmed.startsWith("+")) {
            String digits = PhoneNumberUtil.normalizeDigitsOnly(trimmed);
            for (Country country : countries.getCountries()) {
                if (digits.startsWith(String.valueOf(country.getCountryCode()))) {
                    result.add(country);
                }
            }
        }
        return result;
    }

    public TextWatcher createTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                PhoneInputDelegate.this.onTextChanged(s.toString());
            }
        };
    }

    private void onTextChanged(@NonNull String text) {
        updateCountryIfNeeded(text);
        for (PhoneInputView.TextChangeListener listener : listeners) {
            listener.onTextChanged(text);
        }
    }

    public void addTextChangeListener(PhoneInputView.TextChangeListener listener) {
        listeners.add(listener);
    }

    public void removeTextChangeListener(PhoneInputView.TextChangeListener listener) {
        listeners.remove(listener);
    }

    public void setCountryChangeListener(CountryChangedListener countryChangeListener) {
        this.countryChangeListener = countryChangeListener;
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
