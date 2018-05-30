package com.shaubert.ui.phone;

import android.support.annotation.Nullable;

public interface CountryChangedListener {
    void onCountryChanged(@Nullable Country country, boolean fromUser);
}
