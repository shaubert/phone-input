package com.shaubert.ui.phone;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;

/**
 * Created by GODARD Tuatini on 07/05/15.
 */
public class CountryPickerDialog extends AppCompatDialog {

    private Countries countries;

    private SearchView searchView;
    private RecyclerView recyclerView;
    private CountryListAdapter adapter;

    private CountryPickerCallbacks callbacks;
    private String scrollToCountryIsoCode;

    private Handler handler;

    public CountryPickerDialog(Context context) {
        this(context, null);
    }

    public CountryPickerDialog(Context context, @Nullable CountryPickerCallbacks callbacks) {
        this(context, callbacks, null);
    }

    /**
     * You can set the scrollToCountryIsoCode to scroll to your favorite country
     * @param context
     * @param callbacks
     * @param scrollToCountryIsoCode
     */
    public CountryPickerDialog(Context context, @Nullable CountryPickerCallbacks callbacks, @Nullable String scrollToCountryIsoCode) {
        super(context);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        handler = new Handler();

        this.callbacks = callbacks;
        this.scrollToCountryIsoCode = scrollToCountryIsoCode;

        countries = Countries.get(getContext());
    }

    public void setCallbacks(CountryPickerCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pi_country_picker);

        recyclerView = (RecyclerView) findViewById(R.id.pi_country_picker_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CountryListAdapter();
        adapter.setCountries(countries);
        adapter.setItemClickListener(new CountryListAdapter.ItemClickListener() {
            @Override
            public void onItemClicked(View view, Country country) {
                dismiss();

                if (callbacks != null) {
                    callbacks.onCountrySelected(country, Utils.getCountryResId(getContext(), country));
                }
            }
        });

        recyclerView.setAdapter(adapter);

        searchView = (SearchView) findViewById(R.id.pi_search_view);
        searchView.setIconified(false);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                dismiss();
                return true;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterCountries();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterCountries();
                return true;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterCountries();
            }
        });

        if (savedInstanceState == null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    scrollToCountry(scrollToCountryIsoCode);
                }
            });
        }
    }

    private void filterCountries() {
        adapter.setQuery(searchView.getQuery().toString());
    }

    public void scrollToCountry(String countryIsoCode) {
        if (TextUtils.isEmpty(countryIsoCode)) {
            return;
        }

        for (int i = 0; i < adapter.getItemCount(); i++) {
            Country country = adapter.getItem(i);
            if (country.getIsoCode().equalsIgnoreCase(countryIsoCode)) {
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(i, 0);
                } else {
                    recyclerView.scrollToPosition(i);
                }
                break;
            }
        }
    }

}