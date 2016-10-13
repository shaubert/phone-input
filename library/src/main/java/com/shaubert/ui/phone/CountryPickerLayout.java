package com.shaubert.ui.phone;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

public class CountryPickerLayout extends AppCompatImageButton implements CountryPickerView {

    int FLAG_DISPLAY_TYPE_ICON = 0x0;
    int FLAG_DISPLAY_TYPE_NAME = 0x2;
    int FLAG_DISPLAY_TYPE_CODE = 0x4;

    private CountryPickerDelegate delegate;
    private OnCountryChangedListener onCountryChangedListener;
    private int displayType;

    public CountryPickerLayout(Context context) {
        super(context);
        init(null);
    }

    public CountryPickerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CountryPickerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CountryPickerLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.pi_PhoneInputStyle);
            displayType = typedArray.getInt(R.styleable.pi_CountryPickerStyle_displayType, FLAG_DISPLAY_TYPE_ICON);
            typedArray.recycle();
        }

        delegate = new CountryPickerDelegate(this, attrs);
        delegate.setOnCountryChangedListener(new OnCountryChangedListener() {
            @Override
            public void onCountryChanged(@Nullable Country country) {
                refreshCountry();
                if (onCountryChangedListener != null) {
                    onCountryChangedListener.onCountryChanged(country);
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
        if (country != null && isDisplay(FLAG_DISPLAY_TYPE_ICON)) {
            setImageResource(delegate.getCountries().getFlagResId(country));
        } else {
            setImageDrawable(null);
        }

        String text;
        if (country != null) {
        }
        setText
    }

    private boolean isDisplay(int flag) {
        return (displayType & flag) == flag;
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
    protected void onRestoreInstanceState(Parcelable state) {
        BaseSavedState ss = (BaseSavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        delegate.dispatchOnRestoreInstanceState(ss);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return delegate.dispatchOnSaveInstanceState(superState);
    }

}
