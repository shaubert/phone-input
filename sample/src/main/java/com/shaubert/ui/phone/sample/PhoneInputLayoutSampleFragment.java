package com.shaubert.ui.phone.sample;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.shaubert.ui.phone.Countries;
import com.shaubert.ui.phone.Country;
import com.shaubert.ui.phone.CountryPickerTextView;
import com.shaubert.ui.phone.PhoneInputLayout;

import java.util.Arrays;

public class PhoneInputLayoutSampleFragment extends Fragment {

    private Button setRandomPhoneButton;
    private Button validatePhoneButton;
    private CountryPickerTextView countryPickerTextView;
    private CountryPickerTextView countryPickerTextView2;
    private PhoneInputLayout phoneInputLayout3;
    private CountryPickerTextView countryPickerTextView3;
    private PhoneInputLayout[] phoneInputLayouts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.phone_input_layout_sample, container, false);

        countryPickerTextView = view.findViewById(R.id.country_textview);
        countryPickerTextView.setTextProvider(new CountryPickerTextView.OnlyIconTextProvider());

        countryPickerTextView2 = view.findViewById(R.id.country_textview2);
        countryPickerTextView2.setTextProvider(new CountryPickerTextView.OnlyIconTextProvider());

        phoneInputLayout3 = view.findViewById(R.id.phone_input_layout3);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                phoneInputLayout3.setCustomCountries(new Countries(Arrays.asList(
                        new Country("UK", "United Kingdom Custom", "UU"),
                        new Country("CZ", "Test Test", "TT")
                )));
            }
        }, 1000);
        countryPickerTextView3 = view.findViewById(R.id.country_textview3);
        countryPickerTextView3.setTextProvider(new CountryPickerTextView.OnlyIconTextProvider());

        phoneInputLayouts = new PhoneInputLayout[]{
                view.findViewById(R.id.phone_input_layout),
                view.findViewById(R.id.phone_input_layout2),
                phoneInputLayout3
        };

        setRandomPhoneButton = view.findViewById(R.id.set_random_phone);
        setRandomPhoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRandomPhone();
            }
        });

        validatePhoneButton = view.findViewById(R.id.validate);
        validatePhoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validatePhone();
            }
        });

        return view;
    }

    private void validatePhone() {
        for (PhoneInputLayout phoneInputLayout : phoneInputLayouts) {
            if (phoneInputLayout.isValidPhoneNumber()) {
                Phonenumber.PhoneNumber number = phoneInputLayout.getPhoneNumber();
                String phoneNumber = phoneInputLayout.getFormattedPhoneNumber(PhoneNumberUtil.PhoneNumberFormat.E164);
                Toast.makeText(getContext(), "Valid: " + phoneNumber, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Invalid", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setRandomPhone() {
        Phonenumber.PhoneNumber phoneNumber = Util.getRandomPhone(Countries.get(getContext()));
        if (phoneNumber != null) {
            for (PhoneInputLayout phoneInputLayout : phoneInputLayouts) {
                phoneInputLayout.setPhoneNumber(phoneNumber);
            }
        }
    }

}
