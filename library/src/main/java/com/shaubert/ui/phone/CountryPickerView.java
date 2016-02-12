package com.shaubert.ui.phone;

import android.support.annotation.Nullable;

public interface CountryPickerView {

    Country getCountry();

    void setCountry(Country country);

    void setOnCountryChangedListener(OnCountryChangedListener listener);

    interface OnCountryChangedListener {
        void onCountryChanged(@Nullable Country country);
    }

}
