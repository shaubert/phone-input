package com.shaubert.ui.phone;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

public class CountryPickerDialogManager {
    private CountryPickerCallbacks callbacks;
    private String scrollToCountryIsoCode;
    private CountriesFilter countriesFilter;
    private Countries customCountries;
    private boolean hideKeyboardOnDismiss;

    private String tag;
    private FragmentManager fragmentManager;

    public CountryPickerDialogManager(String tag, FragmentManager fragmentManager) {
        this.tag = "county-picker-dialog-fragment-" + tag;
        this.fragmentManager = fragmentManager;
    }

    public void setCallbacks(CountryPickerCallbacks callbacks) {
        this.callbacks = callbacks;
        setCallbacks(callbacks, find());
    }

    public void setScrollToCountryIsoCode(String scrollToCountryIsoCode) {
        this.scrollToCountryIsoCode = scrollToCountryIsoCode;
        setScrollToCountryIsoCode(scrollToCountryIsoCode, find());
    }

    public void setCountriesFilter(CountriesFilter countriesFilter) {
        this.countriesFilter = countriesFilter;
        setCountriesFilter(countriesFilter, find());
    }

    public void setCustomCountries(Countries customCountries) {
        this.customCountries = customCountries;
        setCustomCountries(customCountries, find());
    }

    public void setHideKeyboardOnDismiss(boolean hideKeyboardOnDismiss) {
        this.hideKeyboardOnDismiss = hideKeyboardOnDismiss;
        setHideKeyboardOnDismiss(hideKeyboardOnDismiss, find());
    }

    public void hide() {
        CountryPickerDialogFragment fragment = find();
        if (fragment != null) {
            fragmentManager.beginTransaction()
                    .remove(fragment)
                    .commit();
        }
    }

    public void show() {
        if (find() == null) {
            fragmentManager.beginTransaction()
                    .add(createFragment(), tag)
                    .commit();
        }
    }

    private CountryPickerDialogFragment createFragment() {
        CountryPickerDialogFragment fragment = new CountryPickerDialogFragment();
        fragment.setCallbacks(callbacks);
        fragment.setScrollToCountryIsoCode(scrollToCountryIsoCode);
        fragment.setCountriesFilter(countriesFilter);
        fragment.setHideKeyboardOnDismiss(hideKeyboardOnDismiss);
        fragment.setCustomCountries(customCountries);
        return fragment;
    }

    public @Nullable CountryPickerDialogFragment find() {
        return (CountryPickerDialogFragment) fragmentManager.findFragmentByTag(tag);
    }

    protected void setCallbacks(CountryPickerCallbacks callbacks, CountryPickerDialogFragment fragment) {
        if (fragment != null) {
            fragment.setCallbacks(callbacks);
        }
    }

    protected void setScrollToCountryIsoCode(String scrollToCountryIsoCode, CountryPickerDialogFragment fragment) {
        if (fragment != null) {
            fragment.setScrollToCountryIsoCode(scrollToCountryIsoCode);
        }
    }

    protected void setCountriesFilter(CountriesFilter countriesFilter, CountryPickerDialogFragment fragment) {
        if (fragment != null) {
            fragment.setCountriesFilter(countriesFilter);
        }
    }

    protected void setCustomCountries(Countries customCountries, CountryPickerDialogFragment fragment) {
        if (fragment != null) {
            fragment.setCustomCountries(customCountries);
        }
    }

    protected void setHideKeyboardOnDismiss(boolean hideKeyboardOnDismiss, CountryPickerDialogFragment fragment) {
        if (fragment != null) {
            fragment.setHideKeyboardOnDismiss(hideKeyboardOnDismiss);
        }
    }

}
