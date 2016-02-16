package com.shaubert.ui.phone.sample;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.shaubert.ui.phone.*;

public class CountryPickerSampleFragment extends Fragment {

    private CountryPickerDialogManager pickerDialogManager;

    private Button pickCountryButton;
    private Country selectedCountry;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pickerDialogManager = new CountryPickerDialogManager("country-picker", getChildFragmentManager());
        pickerDialogManager.setCallbacks(new CountryPickerCallbacks() {
            @Override
            public void onCountrySelected(Country country, int flagResId) {
                setSelectedCountry(country);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.country_picker_sample, container, false);

        pickCountryButton = (Button) view.findViewById(R.id.pick_country);
        pickCountryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedCountry != null) {
                    pickerDialogManager.setScrollToCountryIsoCode(selectedCountry.getIsoCode());
                }
                pickerDialogManager.show();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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

    public void setSelectedCountry(Country selectedCountry) {
        this.selectedCountry = selectedCountry;

        Countries countries = Countries.get(getContext());
        Drawable drawable = getResources().getDrawable(countries.getFlagResId(selectedCountry));
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        pickCountryButton.setCompoundDrawables(drawable, null, null, null);
        pickCountryButton.setText(countries.getDisplayCountryName(selectedCountry));
    }

}
