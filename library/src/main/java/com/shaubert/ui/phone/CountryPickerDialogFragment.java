package com.shaubert.ui.phone;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialogFragment;

public class CountryPickerDialogFragment extends AppCompatDialogFragment {

    private CountryPickerCallbacks callbacks;
    private String scrollToCountryIsoCode;
    private CountriesFilter countriesFilter;

    public void setCallbacks(CountryPickerCallbacks callbacks) {
        this.callbacks = callbacks;

        CountryPickerDialog dialog = getDialog();
        if (dialog != null) {
            dialog.setCallbacks(callbacks);
        }
    }

    public void setCountriesFilter(CountriesFilter countriesFilter) {
        this.countriesFilter = countriesFilter;

        CountryPickerDialog dialog = getDialog();
        if (dialog != null) {
            dialog.setCountriesFilter(countriesFilter);
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
        CountryPickerDialog pickerDialog = new CountryPickerDialog(getContext(), callbacks, scrollToCountryIsoCode);
        pickerDialog.setCountriesFilter(countriesFilter);
        return pickerDialog;
    }

    @Override
    public CountryPickerDialog getDialog() {
        return (CountryPickerDialog) super.getDialog();
    }

}
