package com.shaubert.ui.phone;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class PhoneInputDelegate {

    @Nullable
    private Countries countries;
    private boolean customCountries;
    private boolean autoSetDefaultCountry = true;

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

            int formatInt = typedArray.getInt(R.styleable.pi_PhoneInputStyle_pi_phoneNumberFormat, -1);
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

            countryFromAttrs = typedArray.getString(R.styleable.pi_PhoneInputStyle_pi_countryIso);
            autoChangeCountry = typedArray.getBoolean(R.styleable.pi_PhoneInputStyle_pi_autoChangeCountry, false);
            displayCountryCode = typedArray.getBoolean(R.styleable.pi_PhoneInputStyle_pi_displayCountryCode, false);
            customCountries = typedArray.getBoolean(R.styleable.pi_PhoneInputStyle_pi_customCountries, false);
            autoSetDefaultCountry = typedArray.getBoolean(R.styleable.pi_PhoneInputStyle_pi_autoSetDefaultCountry, autoSetDefaultCountry);
            typedArray.recycle();
        }

        //to init phoneNumberUtil
        loadCountries();
    }

    private void loadCountries() {
        Countries.get(getContext(), new Countries.Callback() {
            @Override
            public void onLoaded(Countries loadedCountries) {
                phoneNumberUtil = PhoneNumberUtil.getInstance();
                if (!customCountries) {
                    countries = loadedCountries;
                    onCountriesLoaded();
                }
            }
        });
    }

    public void setCustomCountries(@Nullable Countries countries) {
        if (!customCountries && countries == null) {
            return;
        }

        this.countries = countries;
        if (country == defaultCountry
                || countries == null
                || !countries.getCountries().contains(country)) {
            setCountry(null);
        }
        this.defaultCountry = null;
        if (phoneNumberUtil == null) {
            phoneNumberUtil = PhoneNumberUtil.getInstance();
        }

        if (countries != null) {
            this.customCountries = true;
            onCountriesLoaded();
        } else {
            this.customCountries = false;
            loadCountries();
        }
    }

    public void setAutoSetDefaultCountry(boolean autoSetDefaultCountry) {
        this.autoSetDefaultCountry = autoSetDefaultCountry;
    }

    private void onCountriesLoaded() {
        if (countries == null) return;

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

        if (this.country == null && autoSetDefaultCountry) {
            setCountry(defaultCountry);
        }
    }

    private void processRestoredCountry() {
        if (restoredCountry != null && countries != null) {
            Country country = countries.getCountryByIso(restoredCountry);
            restoredCountry = null;
            setCountry(country);
        }
    }

    private void setupDefaultCountry() {
        if (countries == null) return;

        String[] possibleRegions = Phones.getPossibleRegions(getContext());
        for (String region : possibleRegions) {
            Country country = countries.getCountryByIso(region);
            if (country != null) {
                this.defaultCountry = country;
                break;
            }
        }

        if (defaultCountry == null && !countries.getCountries().isEmpty()) {
            defaultCountry = countries.getCountries().get(0);
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
        if (countries == null) return;

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
        if (newCode == null) {
            newCode = "";
        }

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
        restoredCountry = ss.countryIso;
        if (restoredCountry == null) {
            restoredCountry = ""; //to restore null country
        }

        if (countries != null) {
            processRestoredCountry();
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
        if (countries == null) return Collections.emptySet();

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
