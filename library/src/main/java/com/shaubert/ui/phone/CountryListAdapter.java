package com.shaubert.ui.phone;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class CountryListAdapter extends RecyclerView.Adapter<ItemCountryPresenter> {

    private Countries countries;
    private List<Country> countriesList = new ArrayList<>();
    private CountriesFilter countriesFilter;
    private ItemClickListener itemClickListener;
    private String query;

    public CountryListAdapter() {
        setHasStableIds(true);
    }

    public void setCountries(Countries countries) {
        this.countries = countries;
        countriesList.clear();
        if (countries != null) {
            countriesList.addAll(countries.getCountries());
            Collections.sort(countriesList);
        }

        notifyDataSetChanged();
    }

    public void setCountriesFilter(CountriesFilter countriesFilter) {
        if (this.countriesFilter != countriesFilter) {
            this.countriesFilter = countriesFilter;

            filterCountries();
            notifyDataSetChanged();
        }
    }

    public void setQuery(String query) {
        if (TextUtils.isEmpty(query) || query.trim().length() == 0) {
            query = null;
        }
        if (TextUtils.equals(query, this.query)) {
            return;
        }
        this.query = query;

        filterCountries();
        notifyDataSetChanged();
    }

    private void filterCountries() {
        countriesList.clear();

        if (countries != null) {
            for (Country country : countries.getCountries()) {
                if (isMatch(country)) {
                    countriesList.add(country);
                }
            }
        }
    }

    private boolean isMatch(Country country) {
        boolean result = true;

        if (countriesFilter != null) {
            result = countriesFilter.filter(country);
        }

        if (result && !TextUtils.isEmpty(query)) {
            String countryStr = country.getIsoCode()
                    + " " + country.getDisplayName()
                    + " +" + country.getCountryCode();
            result = countryStr.toLowerCase(Locale.getDefault()).contains(query.toLowerCase(Locale.getDefault()));
        }

        return result;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public Country getItem(int position) {
        return countriesList.get(position);
    }

    @Override
    public ItemCountryPresenter onCreateViewHolder(ViewGroup parent, int viewType) {
        final ItemCountryPresenter presenter = new ItemCountryPresenter(LayoutInflater.from(parent.getContext()), parent);
        presenter.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClicked(v, presenter.getData());
                }
            }
        });
        return presenter;
    }

    @Override
    public void onBindViewHolder(ItemCountryPresenter holder, int position) {
        holder.swapData(getItem(position));
    }

    @Override
    public long getItemId(int position) {
        return Math.abs(getItem(position).getIsoCode().hashCode());
    }

    @Override
    public int getItemCount() {
        return countriesList.size();
    }

    public interface ItemClickListener {
        void onItemClicked(View view, Country country);
    }

}