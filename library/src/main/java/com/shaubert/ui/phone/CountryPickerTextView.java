package com.shaubert.ui.phone;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.text.emoji.widget.EmojiAppCompatTextView;
import android.util.AttributeSet;
import android.view.View;

public class CountryPickerTextView extends EmojiAppCompatTextView implements CountryPickerView {

    private CountryPickerDelegate delegate;
    private CountryChangedListener countryChangedListener;
    private TextProvider textProvider = new DefaultTextProvider();

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
        delegate.setCountryChangedListener(new CountryChangedListener() {
            @Override
            public void onCountryChanged(@Nullable Country country, boolean fromUser) {
                refreshCountry();
                if (countryChangedListener != null) {
                    countryChangedListener.onCountryChanged(country, fromUser);
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

    public void setTextProvider(TextProvider textProvider) {
        this.textProvider = textProvider;
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
            setText(textProvider.getName(country));
        } else {
            setText(null);
        }
    }

    @Override
    public void setCountryChangedListener(CountryChangedListener countryChangedListener) {
        this.countryChangedListener = countryChangedListener;
    }

    @Override
    public void setCountriesFilter(CountriesFilter countriesFilter) {
        delegate.setCountriesFilter(countriesFilter);
    }

    @Override
    public void setHideKeyboardOnDismiss(boolean hideKeyboardOnDismiss) {
        delegate.setHideKeyboardOnDismiss(hideKeyboardOnDismiss);
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

    public interface TextProvider {
        CharSequence getName(@Nullable Country country);
    }

    public static class DefaultTextProvider implements TextProvider {
        @Override
        public CharSequence getName(@Nullable Country country) {
            return country != null
                    ? country.getUnicodeSymbol() + " " + country.getDisplayName()
                    : null;
        }
    }

    public static class OnlyIconTextProvider implements TextProvider {
        @Override
        public CharSequence getName(@Nullable Country country) {
            return country != null
                    ? country.getUnicodeSymbol()
                    : null;
        }
    }

}
