package com.shaubert.ui.phone;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialogFragment;

public class CountryPickerDialogFragment extends AppCompatDialogFragment {

    private CountryPickerCallbacks callbacks;
    private String scrollToCountryIsoCode;

    public void setCallbacks(CountryPickerCallbacks callbacks) {
        this.callbacks = callbacks;

        CountryPickerDialog dialog = getDialog();
        if (dialog != null) {
            dialog.setCallbacks(callbacks);
        }
    }

    public void setScrollToCountryIsoCode(String scrollToCountryIsoCode) {
        this.scrollToCountryIsoCode = scrollToCountryIsoCode;

        CountryPickerDialog dialog = getDialog();
        if (dialog != null) {
            dialog.scrollToCountry(scrollToCountryIsoCode);
        }
    }

    @Override
    public @NonNull Dialog onCreateDialog(Bundle savedInstanceState) {
        return new CountryPickerDialog(getContext(), callbacks, scrollToCountryIsoCode);
    }

    @Override
    public CountryPickerDialog getDialog() {
        return (CountryPickerDialog) super.getDialog();
    }

}
