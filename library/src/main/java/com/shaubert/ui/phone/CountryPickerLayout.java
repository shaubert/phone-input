package com.shaubert.ui.phone;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CountryPickerLayout extends LinearLayout implements CountryPickerView {

    private CountryPickerDelegate delegate;
    private OnCountryChangedListener onCountryChangedListener;
    private IconAndTextProvider iconAndTextProvider = new DefaultIconAndTextProvider();

    private ImageView iconView;
    private TextView nameView;

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

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);

        if (child instanceof ImageView) {
            setIconView((ImageView) child);
        } else if (child instanceof TextView) {
            setNameView((TextView) child);
        }
    }

    private void setIconView(ImageView imageView) {
        if (this.iconView != null) {
            throw new IllegalStateException("ImageView was added before");
        }
        this.iconView = imageView;

        refreshCountry();
    }

    private void setNameView(TextView nameView) {
        if (this.nameView != null) {
            throw new IllegalStateException("TextView was added before");
        }
        this.nameView = nameView;

        refreshCountry();
    }

    public void setIconAndTextProvider(IconAndTextProvider iconAndTextProvider) {
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
        setIcon(iconAndTextProvider.getIconResId(delegate.getCountries(), country));
        setText(iconAndTextProvider.getName(delegate.getCountries(), country));
    }

    private void setText(CharSequence text) {
        if (nameView == null) return;
        nameView.setText(text);
    }

    private void setIcon(int iconResId) {
        if (iconView == null) return;

        if (iconResId != IconAndTextProvider.NO_RES_ID) {
            iconView.setImageResource(iconResId);
        } else {
            iconView.setImageDrawable(null);
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

    public interface IconAndTextProvider {
        int NO_RES_ID = -1;

        int getIconResId(Countries countries, @Nullable Country country);
        CharSequence getName(Countries countries, @Nullable Country country);
    }

    public static class DefaultIconAndTextProvider implements IconAndTextProvider {
        @Override
        public int getIconResId(Countries countries, @Nullable Country country) {
            return country != null ? countries.getFlagResId(country) : NO_RES_ID;
        }

        @Override
        public CharSequence getName(Countries countries, @Nullable Country country) {
            return country != null ? countries.getDisplayCountryName(country) : null;
        }
    }
}
