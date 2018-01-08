package com.shaubert.ui.phone.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.shaubert.ui.phone.*;

public class PhoneInputsSampleFragment extends Fragment {

    private CountryPickerDialogManager pickerDialogManager;

    private Button setRandomPhoneButton;
    private Button pickCountryButton;
    private PhoneInputView[] phoneInputs;

    private Country selectedCountry;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pickerDialogManager = new CountryPickerDialogManager("country-picker", getChildFragmentManager());
        pickerDialogManager.setCallbacks(new CountryPickerCallbacks() {
            @Override
            public void onCountrySelected(Country country) {
                setSelectedCountry(country);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.phone_inputs_sample, container, false);

        phoneInputs = new PhoneInputView[] {
                view.findViewById(R.id.phone_input_edit_text),
                view.findViewById(R.id.phone_input_masked_edit_text),
                view.findViewById(R.id.phone_input_masked_met_edit_text),
        };

        ((CheckBox) view.findViewById(R.id.national_format)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                for (PhoneInputView input : phoneInputs) {
                    input.setPhoneNumberFormat(isChecked
                            ? PhoneNumberUtil.PhoneNumberFormat.NATIONAL
                            : PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
                }
            }
        });

        pickCountryButton = view.findViewById(R.id.pick_country);
        pickCountryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedCountry != null) {
                    pickerDialogManager.setScrollToCountryIsoCode(selectedCountry.getIsoCode());
                }
                pickerDialogManager.show();
            }
        });

        setRandomPhoneButton = view.findViewById(R.id.set_random_phone);
        setRandomPhoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRandomPhone();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            return;
        }

        Countries countries = Countries.get(getContext());
        String[] possibleRegions = Phones.getPossibleRegions(getContext());
        for (String region : possibleRegions) {
            Country country = countries.getCountryByIso(region);
            if (country != null) {
                setSelectedCountry(country);
                break;
            }
        }
    }

    private void setRandomPhone() {
        Phonenumber.PhoneNumber phoneNumber = Util.getRandomPhone(Countries.get(getContext()));
        if (phoneNumber != null) {
            Country country = Phones.getCountyFromPhone(phoneNumber, getContext());
            setSelectedCountry(country);

            for (PhoneInputView inputView : phoneInputs) {
                inputView.setPhoneNumber(phoneNumber);
            }
        }
    }

    public void setSelectedCountry(Country selectedCountry) {
        this.selectedCountry = selectedCountry;

        pickCountryButton.setText(selectedCountry.getUnicodeSymbol() + " " + selectedCountry.getDisplayName());
        for (PhoneInputView input : phoneInputs) {
            input.setCountry(selectedCountry);
        }
    }

}
