package com.shaubert.ui.phone;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemCountryPresenter extends RecyclerView.ViewHolder {
    private ImageView icon;
    private TextView name;

    private Country data;
    private Countries countries;

    public ItemCountryPresenter(LayoutInflater inflater, ViewGroup parent) {
        this(inflater.inflate(R.layout.pi_item_country, parent, false));
    }

    public ItemCountryPresenter(View view) {
        super(view);
        icon = (ImageView) view.findViewById(R.id.icon);
        name = (TextView) view.findViewById(R.id.name);
    }

    public Country getData() {
        return data;
    }

    public void refresh() {
        if (data != null) {
            itemView.setVisibility(View.VISIBLE);

            name.setText(name.getResources().getString(R.string.pi_item_country_format,
                    countries.getDisplayCountryName(data),
                    data.getDialingCode()));
            icon.setImageResource(Utils.getCountryResId(itemView.getContext(), data));
        } else {
            itemView.setVisibility(View.GONE);
        }
    }

    public void swapData(Country data, Countries countries) {
        this.data = data;
        this.countries = countries;
        refresh();
    }

}