package com.shaubert.ui.phone;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import java.util.UUID;

public class CountryPickerDelegate implements CountryPickerView {

    private String tag = UUID.randomUUID().toString();
    private Country country;
    private Countries countries;

    private CountryPickerDialogManager countryPickerDialogManager;
    private OnCountryChangedListener onCountryChangedListener;

    private CountryPickerView view;

    public CountryPickerDelegate(CountryPickerView view, AttributeSet attrs) {
        this.view = view;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        Context context = getContext();
        if (!(context instanceof FragmentActivity)) {
            throw new IllegalArgumentException("must be created with FragmentActivity");
        }

        countries = Countries.get(getContext());
    }

    public void openPicker() {
        setupDialogManager();
        countryPickerDialogManager.setScrollToCountryIsoCode(country != null ? country.getIsoCode() : null);
        countryPickerDialogManager.show();
    }

    private void setupDialogManager() {
        if (countryPickerDialogManager == null) {
            countryPickerDialogManager = new CountryPickerDialogManager(tag,
                    ((FragmentActivity) getContext()).getSupportFragmentManager());
            countryPickerDialogManager.setCallbacks(new CountryPickerCallbacks() {
                @Override
                public void onCountrySelected(Country country, int flagResId) {
                    setCountry(country);
                }
            });
        }
    }

    public Countries getCountries() {
        return countries;
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
        if (country == this.country || (country != null && country.equals(this.country))) {
            return;
        }

        this.country = country;
        if (onCountryChangedListener != null) {
            onCountryChangedListener.onCountryChanged(country);
        }
    }

    @Override
    public void setOnCountryChangedListener(OnCountryChangedListener onCountryChangedListener) {
        this.onCountryChangedListener = onCountryChangedListener;
    }

    public void dispatchOnRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        if (!TextUtils.isEmpty(ss.tag)) {
            tag = ss.tag;
        }

        Country country = countries.getCountryByIso(ss.countryIso);
        if (country != null) {
            setCountry(country);
        }

        setupDialogManager();
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
