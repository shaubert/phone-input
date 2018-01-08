package com.shaubert.ui.phone;

import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ItemCountryPresenter extends RecyclerView.ViewHolder {
    private TextView icon;
    private TextView name;

    private Country data;
    private float originalIconTextSize;
    private float codeIconTextSize;

    public ItemCountryPresenter(LayoutInflater inflater, ViewGroup parent) {
        this(inflater.inflate(R.layout.pi_item_country, parent, false));
    }

    public ItemCountryPresenter(View view) {
        super(view);
        icon = view.findViewById(R.id.icon);
        name = view.findViewById(R.id.name);

        originalIconTextSize = icon.getTextSize();
        codeIconTextSize = name.getTextSize();
    }

    public Country getData() {
        return data;
    }

    public void refresh() {
        if (data != null) {
            itemView.setVisibility(View.VISIBLE);

            String unicodeSymbol = data.getUnicodeSymbol();
            if (unicodeSymbol.startsWith("+")) {
                icon.setTextSize(TypedValue.COMPLEX_UNIT_PX, codeIconTextSize);
                icon.setMinEms(2);
            } else {
                icon.setTextSize(TypedValue.COMPLEX_UNIT_PX, originalIconTextSize);
                icon.setMinEms(0);
            }
            icon.setText(unicodeSymbol);

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