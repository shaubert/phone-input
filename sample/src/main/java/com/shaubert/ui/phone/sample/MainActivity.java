package com.shaubert.ui.phone.sample;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.shaubert.ui.phone.*;

import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    private Countries countries;
    private CountryPickerDialogManager pickerDialogManager;

    private Button setRandomPhoneButton;
    private Button pickCountryButton;
    private PhoneInputView[] phoneInputs;
    private PhoneInputLayout phoneInputLayout;
    private Country selectedCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolbar();

        countries = Countries.get(getApplicationContext());

        phoneInputs = new PhoneInputView[] {
                (PhoneInputView) findViewById(R.id.phone_input_edit_text),
                (PhoneInputView) findViewById(R.id.phone_input_masked_edit_text),
                (PhoneInputView) findViewById(R.id.phone_input_masked_met_edit_text),
        };

        phoneInputLayout = (PhoneInputLayout) findViewById(R.id.phone_input_layout);

        ((CheckBox) findViewById(R.id.national_format)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                for (PhoneInputView input : phoneInputs) {
                    input.setPhoneNumberFormat(isChecked
                            ? PhoneNumberUtil.PhoneNumberFormat.NATIONAL
                            : PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
                }
            }
        });

        pickCountryButton = (Button) findViewById(R.id.pick_country);
        pickCountryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedCountry != null) {
                    pickerDialogManager.setScrollToCountryIsoCode(selectedCountry.getIsoCode());
                }
                pickerDialogManager.show();
            }
        });

        setRandomPhoneButton = (Button) findViewById(R.id.set_random_phone);
        setRandomPhoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRandomPhone();
            }
        });

        pickerDialogManager = new CountryPickerDialogManager("country-picker", getSupportFragmentManager());
        pickerDialogManager.setCallbacks(new CountryPickerCallbacks() {
            @Override
            public void onCountrySelected(Country country, int flagResId) {
                setSelectedCountry(country);
            }
        });

        String[] possibleRegions = Phones.getPossibleRegions(this);
        for (String region : possibleRegions) {
            Country country = countries.getCountryByIso(region);
            if (country != null) {
                setSelectedCountry(country);
                break;
            }
        }
    }

    private void setRandomPhone() {
        List<Country> countries = this.countries.getCountries();
        Country country = countries.get(new Random().nextInt(countries.size()));
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber phoneNumber =
                phoneNumberUtil.getExampleNumberForType(country.getIsoCode(), PhoneNumberUtil.PhoneNumberType.MOBILE);
        if (phoneNumber == null) {
            phoneNumber = phoneNumberUtil.getExampleNumber(country.getIsoCode());
        }
        if (phoneNumber != null) {
            setSelectedCountry(country);
            for (PhoneInputView inputView : phoneInputs) {
                inputView.setPhoneNumber(phoneNumber);
            }

            phoneInputLayout.setPhoneNumber(phoneNumber);
        }
    }

    public void setSelectedCountry(Country selectedCountry) {
        this.selectedCountry = selectedCountry;

        Drawable drawable = getResources().getDrawable(countries.getFlagResId(selectedCountry));
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        pickCountryButton.setCompoundDrawables(drawable, null, null, null);
        pickCountryButton.setText(countries.getDisplayCountryName(selectedCountry));

        for (PhoneInputView input : phoneInputs) {
            input.setCountry(selectedCountry);
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.ab_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

}
