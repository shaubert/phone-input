package com.shaubert.ui.phone;

import android.content.Context;
import android.support.annotation.Nullable;

public interface CountryPickerView {

    Context getContext();

    Country getCountry();

    void setCountry(Country country);

    void setOnCountryChangedListener(OnCountryChangedListener listener);

    void setCountriesFilter(CountriesFilter countriesFilter);

    interface OnCountryChangedListener {
        void onCountryChanged(@Nullable Country country, boolean fromUser);
    }

}
