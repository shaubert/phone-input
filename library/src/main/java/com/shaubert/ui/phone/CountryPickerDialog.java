package com.shaubert.ui.phone;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by GODARD Tuatini on 07/05/15.
 */
public class CountryPickerDialog extends AppCompatDialog {

    private Countries countries;

    private SearchView searchView;
    private RecyclerView recyclerView;
    private CountryListAdapter adapter;
    private CountriesFilter countriesFilter;

    private CountryPickerCallbacks callbacks;
    private String scrollToCountryIsoCode;

    private Handler handler;
    private boolean hideKeyboardOnDismiss;

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

        Countries.get(getContext(), new Countries.Callback() {
            @Override
            public void onLoaded(Countries loadedCountries) {
                countries = loadedCountries;
                if (adapter != null) {
                    adapter.setCountries(countries);
                }
            }
        });
    }

    public void setCountriesFilter(CountriesFilter countriesFilter) {
        this.countriesFilter = countriesFilter;
        if (adapter != null) {
            adapter.setCountriesFilter(countriesFilter);
        }
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
        adapter.setCountriesFilter(countriesFilter);
        adapter.setItemClickListener(new CountryListAdapter.ItemClickListener() {
            @Override
            public void onItemClicked(View view, Country country) {
                dismiss();

                if (callbacks != null) {
                    callbacks.onCountrySelected(country);
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

    @Override
    public void dismiss() {
        hideKeyboardOnDismissMaybe();
        super.dismiss();
    }

    @Override
    public void hide() {
        hideKeyboardOnDismissMaybe();
        super.hide();
    }

    private void hideKeyboardOnDismissMaybe() {
        if (!hideKeyboardOnDismiss) return;
        if (searchView == null) return;

        InputMethodManager imm = (InputMethodManager) searchView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(searchView.getApplicationWindowToken(), 0);
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

    public void setHideKeyboardOnDismiss(boolean hideKeyboardOnDismiss) {
        this.hideKeyboardOnDismiss = hideKeyboardOnDismiss;
    }

}