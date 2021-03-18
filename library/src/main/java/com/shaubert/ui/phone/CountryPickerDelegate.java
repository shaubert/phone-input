package com.shaubert.ui.phone;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import java.util.Collections;
import java.util.UUID;

public class CountryPickerDelegate implements CountryPickerView {

    private FragmentActivity activity;
    private String tag = UUID.randomUUID().toString();
    private Country country;

    @Nullable
    private Countries countries;
    private boolean customCountries;
    private boolean autoSetDefaultCountry = true;

    private CountryPickerDialogManager countryPickerDialogManager;
    private CountryChangedListener countryChangedListener;
    private CountriesFilter countriesFilter;

    private CountryPickerView view;
    private String restoredCountryIso;

    public CountryPickerDelegate(CountryPickerView view, AttributeSet attrs, int defStyleAttr) {
        this.view = view;
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        Activity activity = getActivity(getContext());
        if (!(activity instanceof FragmentActivity)) {
            throw new IllegalArgumentException("must be created with FragmentActivity");
        }
        this.activity = (FragmentActivity) activity;

        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(
                    attrs,
                    R.styleable.pi_CountryPickerStyle,
                    defStyleAttr,
                    0
            );

            customCountries = typedArray.getBoolean(R.styleable.pi_CountryPickerStyle_pi_customCountries, false);
            autoSetDefaultCountry = typedArray.getBoolean(R.styleable.pi_CountryPickerStyle_pi_autoSetDefaultCountry, autoSetDefaultCountry);
            typedArray.recycle();
        }

        if (!customCountries) {
            loadCountries();
        }
    }

    private void loadCountries() {
        Countries.get(new Countries.Callback() {
            @Override
            public void onLoaded(Countries loadedCountries) {
                if (!customCountries) {
                    countries = loadedCountries;
                    onCountriesLoaded();
                }
            }
        });
    }

    private static Activity getActivity(Context context) {
        for (;
             context instanceof ContextWrapper && !(context instanceof Activity);
             context = ((ContextWrapper) context).getBaseContext())
            ;
        if (context instanceof Activity) {
            return (Activity) context;
        }
        return null;
    }

    public void openPicker() {
        setupDialogManager();
        if (customCountries) {
            countryPickerDialogManager.setCustomCountries(
                    countries != null ? countries : new Countries(Collections.<Country>emptyList())
            );
        } else {
            countryPickerDialogManager.setCustomCountries(null);
        }
        countryPickerDialogManager.setScrollToCountryIsoCode(country != null ? country.getIsoCode() : null);
        countryPickerDialogManager.show();
    }

    private void setupDialogManager() {
        if (countryPickerDialogManager == null) {
            countryPickerDialogManager = new CountryPickerDialogManager(tag,
                    activity.getSupportFragmentManager());
            countryPickerDialogManager.setCallbacks(new CountryPickerCallbacks() {
                @Override
                public void onCountrySelected(Country country) {
                    setCountryInternal(country, true);
                }
            });
        }
    }

    @Override
    public Context getContext() {
        return view.getContext();
    }

    @Override
    public Country getCountry() {
        return country;
    }

    @Override
    public void setCountry(Country country) {
        setCountryInternal(country, false);
    }

    private void setCountryInternal(Country country, boolean fromUser) {
        if (countriesFilter != null
                && country != null
                && !countriesFilter.filter(country)) {
            country = null;
        }

        if (country == this.country || (country != null && country.equals(this.country))) {
            return;
        }

        this.country = country;
        if (countryChangedListener != null) {
            countryChangedListener.onCountryChanged(country, fromUser);
        }
    }

    @Override
    public void setCountryChangedListener(CountryChangedListener countryChangedListener) {
        this.countryChangedListener = countryChangedListener;
    }

    @Override
    public void setCountriesFilter(CountriesFilter countriesFilter) {
        this.countriesFilter = countriesFilter;

        setupDialogManager();
        countryPickerDialogManager.setCountriesFilter(countriesFilter);

        setCountry(country);
    }

    public void setAutoSetDefaultCountry(boolean autoSetDefaultCountry) {
        this.autoSetDefaultCountry = autoSetDefaultCountry;
    }

    @Override
    public void setCustomCountries(@Nullable Countries countries) {
        if (!customCountries && countries == null) {
            return;
        }

        this.countries = countries;
        if (countries == null
                || !countries.getCountries().contains(country)) {
            setCountry(null);
        }

        if (countries != null) {
            this.customCountries = true;
            onCountriesLoaded();
        } else {
            this.customCountries = false;
            loadCountries();
        }

        setupDialogManager();
        countryPickerDialogManager.setCustomCountries(countries);
    }

    @Override
    public void setHideKeyboardOnDismiss(boolean hideKeyboardOnDismiss) {
        setupDialogManager();
        countryPickerDialogManager.setHideKeyboardOnDismiss(hideKeyboardOnDismiss);
    }

    public void dispatchOnRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        if (!TextUtils.isEmpty(ss.tag)) {
            tag = ss.tag;
        }

        restoredCountryIso = ss.countryIso;
        if (restoredCountryIso == null) {
            restoredCountryIso = ""; //to set country to null
        }
        if (countries != null) {
            onCountriesLoaded();
        }

        setupDialogManager();
    }

    private void onCountriesLoaded() {
        if (countries == null) return;

        if (restoredCountryIso != null) {
            Country country = countries.getCountryByIso(restoredCountryIso);
            restoredCountryIso = null;
            setCountry(country);
            return;
        }

        if (this.country != null || !autoSetDefaultCountry) return;

        String[] possibleRegions = Phones.getPossibleRegions(getContext());
        for (String region : possibleRegions) {
            Country country = countries.getCountryByIso(region);
            if (country != null) {
                setCountry(country);
                break;
            }
        }

        if (this.country == null && !countries.getCountries().isEmpty()) {
            setCountry(countries.getCountries().get(0));
        }
    }

    public Parcelable dispatchOnSaveInstanceState(Parcelable superState) {
        SavedState ss = new SavedState(superState);
        ss.tag = tag;
        ss.countryIso = country != null ? country.getIsoCode() : null;
        return ss;
    }

    public static class SavedState extends View.BaseSavedState {
        String tag;
        String countryIso;

        SavedState(Parcelable superState) {
            super(superState);
        }

        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel in) {
            super(in);
            tag = in.readString();
            countryIso = in.readString();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeString(tag);
            out.writeString(countryIso);
        }

        @Override
        public String toString() {
            return "CountryPickerDelegate.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " tag=" + tag
                    + " countryIso=" + countryIso
                    + "}";
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
