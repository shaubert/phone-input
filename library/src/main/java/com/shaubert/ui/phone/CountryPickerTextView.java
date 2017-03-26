package com.shaubert.ui.phone;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.View;

public class CountryPickerTextView extends AppCompatTextView implements CountryPickerView {

    private CountryPickerDelegate delegate;
    private OnCountryChangedListener onCountryChangedListener;
    private CountryPickerLayout.IconAndTextProvider iconAndTextProvider = new CountryPickerLayout.DefaultIconAndTextProvider();

    public CountryPickerTextView(Context context) {
        super(context);
        init(null);
    }

    public CountryPickerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CountryPickerTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        delegate = new CountryPickerDelegate(this, attrs);
        delegate.setOnCountryChangedListener(new OnCountryChangedListener() {
            @Override
            public void onCountryChanged(@Nullable Country country, boolean fromUser) {
                refreshCountry();
                if (onCountryChangedListener != null) {
                    onCountryChangedListener.onCountryChanged(country, fromUser);
                }
            }
        });
        refreshCountry();

        super.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openPicker();
            }
        });
    }

    public void setIconAndTextProvider(CountryPickerLayout.IconAndTextProvider iconAndTextProvider) {
        this.iconAndTextProvider = iconAndTextProvider;
        refreshCountry();
    }

    public void openPicker() {
        delegate.openPicker();
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        throw new IllegalStateException("you can not change click listener");
    }

    @Override
    public Country getCountry() {
        return delegate.getCountry();
    }

    @Override
    public void setCountry(Country country) {
        delegate.setCountry(country);
        refreshCountry();
    }

    private void refreshCountry() {
        Country country = delegate.getCountry();
        if (country != null) {
            setText(iconAndTextProvider.getName(delegate.getCountries(), country));

            int flagResId = iconAndTextProvider.getIconResId(delegate.getCountries(), country);
            Drawable scaledIcon = delegate.getScaledIcon(flagResId);
            if (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                setCompoundDrawables(null, scaledIcon, null, null);
            } else {
                setCompoundDrawables(scaledIcon, null, null, null);
            }
        } else {
            setCompoundDrawables(null, null, null, null);
            setText(null);
        }
    }

    @Override
    public void setOnCountryChangedListener(OnCountryChangedListener onCountryChangedListener) {
        this.onCountryChangedListener = onCountryChangedListener;
    }

    @Override
    public void setCountriesFilter(CountriesFilter countriesFilter) {
        delegate.setCountriesFilter(countriesFilter);
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        BaseSavedState ss = (BaseSavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        delegate.dispatchOnRestoreInstanceState(ss);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return delegate.dispatchOnSaveInstanceState(superState);
    }

}
