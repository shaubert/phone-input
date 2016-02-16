package com.shaubert.ui.phone.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);

        view.findViewById(R.id.phone_input_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(PhoneInputLayoutSampleFragment.class);
            }
        });

        view.findViewById(R.id.country_picker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(CountryPickerSampleFragment.class);
            }
        });

        view.findViewById(R.id.phone_inputs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(PhoneInputsSampleFragment.class);
            }
        });

        return view;
    }

    private void openFragment(Class<? extends Fragment> fragmentClass) {
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_content, Fragment.instantiate(getContext(), fragmentClass.getName()))
                .addToBackStack(null)
                .commit();
    }

}
