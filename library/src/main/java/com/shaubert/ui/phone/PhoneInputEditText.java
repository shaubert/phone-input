package com.shaubert.ui.phone;

import android.content.Context;
import android.os.Parcelable;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.widget.EditText;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public class PhoneInputEditText extends AppCompatEditText implements PhoneInputView {

    private PhoneInputDelegate delegate;
    private CustomPhoneNumberFormattingTextWatcher formattingTextWatcher;

    public PhoneInputEditText(Context context) {
        super(context);
        init(null);
    }

    public PhoneInputEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PhoneInputEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        delegate = new PhoneInputDelegate(this);
        delegate.init(attrs);
    }

    @Override
    public Country getDefaultCountry() {
        return delegate.getDefaultCountry();
    }

    @Override
    public void setCountryIso(String countryIso) {
        delegate.setCountryIso(countryIso);
    }

    @Override
    public void setCountry(Country country) {
        delegate.setCountry(country);
    }

    @Override
    public void setPhoneNumberFormat(PhoneNumberUtil.PhoneNumberFormat phoneNumberFormat) {
        delegate.setPhoneNumberFormat(phoneNumberFormat);
    }

    @Override
    public PhoneNumberUtil.PhoneNumberFormat getPhoneNumberFormat() {
        return delegate.getPhoneNumberFormat();
    }

    @Override
    public String getFormattedPhoneNumber(PhoneNumberUtil.PhoneNumberFormat format) {
        return delegate.getFormattedPhoneNumber(getText().toString(), format);
    }

    @Override
    public boolean isValidPhoneNumber() {
        return delegate.isValidPhoneNumber(getText().toString());
    }

    @Override
    public Phonenumber.PhoneNumber getPhoneNumber() {
        return delegate.getPhoneNumber(getText().toString());
    }

    @Override
    public void setPhoneNumber(Phonenumber.PhoneNumber phoneNumber) {
        setPhoneNumberString(delegate.formatPhoneNumber(phoneNumber));
    }

    @Override
    public void setPhoneNumberString(String phoneNumber) {
        delegate.setCountryFromPhoneNumber(phoneNumber);
        setText(phoneNumber);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return delegate.dispatchOnSaveInstanceState(superState);
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        BaseSavedState ss = (BaseSavedState) state;
        delegate.dispatchOnRestoreInstanceState(ss);

        super.onRestoreInstanceState(ss.getSuperState());
    }

    @Override
    public void setMaskBuilder(MaskBuilder maskBuilder) {
        delegate.setMaskBuilder(maskBuilder);
    }

    @Override
    public Character getMaskChar() {
        return null;
    }

    @Override
    public void setMask(String mask) {
        setHint(mask);
        if (formattingTextWatcher != null) {
            removeTextChangedListener(formattingTextWatcher);
            formattingTextWatcher = null;
        }
        Country country = delegate.getCountry();
        if (country != null) {
            formattingTextWatcher = new CustomPhoneNumberFormattingTextWatcher(country);
            addTextChangedListener(formattingTextWatcher);
        }
    }
}
