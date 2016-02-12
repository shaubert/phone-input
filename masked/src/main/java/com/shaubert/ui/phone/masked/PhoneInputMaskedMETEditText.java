package com.shaubert.ui.phone.masked;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.shaubert.maskedinput.MaskChar;
import com.shaubert.maskedinput.metextension.MaskedMETEditText;
import com.shaubert.ui.phone.*;

public class PhoneInputMaskedMETEditText extends MaskedMETEditText implements PhoneInput {

    private PhoneInputDelegate delegate;

    public PhoneInputMaskedMETEditText(Context context) {
        super(context);
        init(null);
    }

    public PhoneInputMaskedMETEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PhoneInputMaskedMETEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        delegate = new PhoneInputDelegate(this, attrs);
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
    public @Nullable String getFormattedPhoneNumber(PhoneNumberUtil.PhoneNumberFormat format) {
        if (isMaskFilled()) {
            return delegate.getFormattedPhoneNumber(getText().toString(), format);
        } else {
            return null;
        }
    }

    @Override
    public boolean isValidPhoneNumber() {
        return isMaskFilled() && delegate.isValidPhoneNumber(getText().toString());
    }

    @Override
    public @Nullable Phonenumber.PhoneNumber getPhoneNumber() {
        if (isMaskFilled()) {
            return delegate.getPhoneNumber(getText().toString());
        } else {
            return null;
        }
    }

    @Override
    public void setPhoneNumber(@Nullable Phonenumber.PhoneNumber phoneNumber) {
        setPhoneNumberString(delegate.getValueForMask(phoneNumber));
    }

    @Override
    public void setPhoneNumberString(@Nullable String phoneNumber) {
        Phonenumber.PhoneNumber number = delegate.getPhoneNumber(phoneNumber);
        if (number != null) {
            setPhoneNumber(number);
        } else {
            setTextInMask(phoneNumber);
        }
    }

    @Override
    public void addMaskChar(MaskChar... maskChars) {
        super.addMaskChar(maskChars);
        delegate.refreshMask();
    }

    @Override
    public void removeMaskChar(MaskChar maskChar) {
        super.removeMaskChar(maskChar);
        delegate.refreshMask();
    }

    @Override
    public void removeMaskChar(char ch) {
        super.removeMaskChar(ch);
        delegate.refreshMask();
    }

    @Override
    public void clearMaskChars() {
        super.clearMaskChars();
        delegate.refreshMask();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return delegate.dispatchOnSaveInstanceState(superState);
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        BaseSavedState ss = (BaseSavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        delegate.dispatchOnRestoreInstanceState(ss);
    }

    @Override
    public void setMaskBuilder(MaskBuilder maskBuilder) {
        delegate.setMaskBuilder(maskBuilder);
    }

    @Override
    public Character getMaskChar() {
        MaskChar maskChar = Utils.findNumericMaskChar(this);
        return maskChar != null ? maskChar.getMaskChar() : null;
    }
}
