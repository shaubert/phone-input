package com.shaubert.ui.phone;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

public class CountryPickerDialogFragment extends AppCompatDialogFragment {

    private CountryPickerCallbacks callbacks;
    private String scrollToCountryIsoCode;
    private CountriesFilter countriesFilter;
    private boolean hideKeyboardOnDismiss;

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

    public void setHideKeyboardOnDismiss(boolean hideKeyboardOnDismiss) {
        this.hideKeyboardOnDismiss = hideKeyboardOnDismiss;

        CountryPickerDialog dialog = getDialog();
        if (dialog != null) {
            dialog.setHideKeyboardOnDismiss(hideKeyboardOnDismiss);
        }
    }

    @Override
    public @NonNull Dialog onCreateDialog(Bundle savedInstanceState) {
        CountryPickerDialog pickerDialog = new CountryPickerDialog(getContext(), callbacks, scrollToCountryIsoCode);
        pickerDialog.setCountriesFilter(countriesFilter);
        pickerDialog.setHideKeyboardOnDismiss(hideKeyboardOnDismiss);
        return pickerDialog;
    }

    @Override
    public CountryPickerDialog getDialog() {
        return (CountryPickerDialog) super.getDialog();
    }

}
