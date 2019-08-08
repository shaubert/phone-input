package com.shaubert.ui.phone;

import android.content.Context;

public interface CountryPickerView {

    Context getContext();

    Country getCountry();

    void setCountry(Country country);

    void setCountryChangedListener(CountryChangedListener listener);

    void setCountriesFilter(CountriesFilter countriesFilter);

    void setCustomCountries(Countries countries);

    void setHideKeyboardOnDismiss(boolean hideKeyboardOnDismiss);

}
