package com.shaubert.ui.phone;

import androidx.annotation.Nullable;

public interface CountryChangedListener {
    void onCountryChanged(@Nullable Country country, boolean fromUser);
}
