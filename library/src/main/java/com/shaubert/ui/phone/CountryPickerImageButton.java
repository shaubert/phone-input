package com.shaubert.ui.phone;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

public class CountryPickerImageButton extends ImageButton implements CountryPickerView {

    private CountryPickerDelegate delegate;
    private OnCountryChangedListener onCountryChangedListener;

    public CountryPickerImageButton(Context context) {
        super(context);
        init(null);
    }

    public CountryPickerImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CountryPickerImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CountryPickerImageButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
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
        if (country != null) {
            setImageResource(delegate.getCountries().getFlagResId(country));
        } else {
            setImageDrawable(null);
        }
    }

    @Override
    public void setOnCountryChangedListener(OnCountryChangedListener onCountryChangedListener) {
        this.onCountryChangedListener = onCountryChangedListener;
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
