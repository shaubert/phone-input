package com.shaubert.ui.phone;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

import java.util.UUID;

public class CountryPickerImageButton extends ImageButton implements CountryPickerView {

    private String tag = UUID.randomUUID().toString();
    private Country country;
    private Countries countries;

    private CountryPickerDialogManager countryPickerDialogManager;
    private OnCountryChangedListener onCountryChangedListener;

    public CountryPickerImageButton(Context context) {
        super(context);
        init();
    }

    public CountryPickerImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CountryPickerImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CountryPickerImageButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        Context context = getContext();
        if (!(context instanceof FragmentActivity)) {
            throw new IllegalArgumentException("must be created with FragmentActivity");
        }

        countries = Countries.get(getContext());

        super.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openPicker();
            }
        });
    }

    public void openPicker() {
        setupDialogManager();
        countryPickerDialogManager.setScrollToCountryIsoCode(country != null ? country.getIsoCode() : null);
        countryPickerDialogManager.show();
    }

    private void setupDialogManager() {
        if (countryPickerDialogManager == null) {
            countryPickerDialogManager = new CountryPickerDialogManager(tag,
                    ((FragmentActivity) getContext()).getSupportFragmentManager());
            countryPickerDialogManager.setCallbacks(new CountryPickerCallbacks() {
                @Override
                public void onCountrySelected(Country country, int flagResId) {
                    setCountry(country);
                }
            });
        }
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        throw new IllegalStateException("you can not change click listener");
    }

    @Override
    public Country getCountry() {
        return country;
    }

    @Override
    public void setCountry(Country country) {
        if (country == this.country || (country != null && country.equals(this.country))) {
            return;
        }

        this.country = country;
        if (country != null) {
            setImageResource(countries.getFlagResId(country));
        } else {
            setImageDrawable(null);
        }

        if (onCountryChangedListener != null) {
            onCountryChangedListener.onCountryChanged(country);
        }
    }

    @Override
    public void setOnCountryChangedListener(OnCountryChangedListener onCountryChangedListener) {
        this.onCountryChangedListener = onCountryChangedListener;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        if (!TextUtils.isEmpty(ss.tag)) {
            tag = ss.tag;
        }

        Country country = countries.getCountryByIso(ss.countryIso);
        if (country != null) {
            setCountry(country);
        }

        setupDialogManager();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        SavedState ss = new SavedState(superState);
        ss.tag = tag;
        return ss;
    }

    public static class SavedState extends View.BaseSavedState {
        String tag;
        String countryIso;

        SavedState(Parcelable superState) {
            super(superState);
        }

        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel in) {
            super(in);
            tag = in.readString();
            countryIso = in.readString();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeString(tag);
            out.writeString(countryIso);
        }

        @Override
        public String toString() {
            return "PhoneInputDelegate.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " tag=" + tag
                    + " countryIso=" + countryIso
                    + "}";
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
