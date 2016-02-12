package com.shaubert.ui.phone;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class CountryListAdapter extends RecyclerView.Adapter<ItemCountryPresenter> {

    private Countries countries;
    private List<Country> countriesList = new ArrayList<>();
    private ItemClickListener itemClickListener;
    private String query;

    public CountryListAdapter() {
        setHasStableIds(true);
    }

    public void setCountries(Countries countries) {
        this.countries = countries;
        countriesList.clear();
        countriesList.addAll(countries.getCountries());

        notifyDataSetChanged();
    }

    public void setQuery(String query) {
        if (TextUtils.isEmpty(query) || query.trim().length() == 0) {
            query = null;
        }
        if (TextUtils.equals(query, this.query)) {
            return;
        }
        this.query = query;

        if (query == null) {
            countriesList.clear();
            countriesList.addAll(countries.getCountries());
        } else {
            filterCountries(query);
        }
        notifyDataSetChanged();
    }

    private void filterCountries(String query) {
        countriesList.clear();

        for (Country country : countries.getCountries()) {
            if (isMatch(country, query)) {
                countriesList.add(country);
            }
        }
    }

    private boolean isMatch(Country country, String query) {
        String countryStr = country.getIsoCode()
                + " " + countries.getDisplayCountryName(country)
                + " +" + country.getCountryCode();
        return countryStr.toLowerCase().contains(query.toLowerCase());
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
        holder.swapData(getItem(position), countries);
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