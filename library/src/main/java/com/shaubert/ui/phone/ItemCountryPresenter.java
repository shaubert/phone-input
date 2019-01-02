package com.shaubert.ui.phone;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ItemCountryPresenter extends RecyclerView.ViewHolder {
    private TextView icon;
    private TextView name;

    private Country data;

    public ItemCountryPresenter(LayoutInflater inflater, ViewGroup parent) {
        this(inflater.inflate(R.layout.pi_item_country, parent, false));
    }

    public ItemCountryPresenter(View view) {
        super(view);
        icon = view.findViewById(R.id.icon);
        name = view.findViewById(R.id.name);

        if (icon instanceof CountryPickerTextView) {
            ((CountryPickerTextView) icon).setTextProvider(
                    new CountryPickerTextView.OnlyIconTextProvider()
            );
        }
    }

    public Country getData() {
        return data;
    }

    public void refresh() {
        if (data != null) {
            itemView.setVisibility(View.VISIBLE);

            if (icon instanceof CountryPickerTextView) {
                ((CountryPickerTextView) icon).setCountry(data);
            } else {
                icon.setText(data.getUnicodeSymbol());
            }
            name.setText(data.getDisplayName());
        } else {
            itemView.setVisibility(View.GONE);
        }
    }

    public void swapData(Country data) {
        this.data = data;
        refresh();
    }

}