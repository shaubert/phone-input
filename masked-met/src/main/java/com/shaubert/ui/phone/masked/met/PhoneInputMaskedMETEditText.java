package com.shaubert.ui.phone.masked.met;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.shaubert.maskedinput.MaskChar;
import com.shaubert.maskedinput.metextension.MaskedMETEditText;
import com.shaubert.ui.phone.Country;
import com.shaubert.ui.phone.MaskBuilder;
import com.shaubert.ui.phone.PhoneInputDelegate;
import com.shaubert.ui.phone.PhoneInputView;
import com.shaubert.ui.phone.masked.MaskedPhoneUtils;

public class PhoneInputMaskedMETEditText extends MaskedMETEditText implements PhoneInputView {

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
    public @Nullable String getFormattedPhoneNumber(PhoneNumberUtil.PhoneNumberFormat format) {
        return delegate.getFormattedPhoneNumber(getTextFromMask(), format);
    }

    @Override
    public boolean isValidPhoneNumber() {
        return delegate.isValidPhoneNumber(getTextFromMask());
    }

    @Override
    public @Nullable Phonenumber.PhoneNumber getPhoneNumber() {
        return delegate.getPhoneNumber(getTextFromMask());
    }

    @Override
    public void setPhoneNumber(@Nullable Phonenumber.PhoneNumber phoneNumber) {
        delegate.setCountryFromPhoneNumber(phoneNumber);
        setTextInMask(delegate.getValueForMask(phoneNumber));
    }

    @Override
    public void setPhoneNumberString(@Nullable String phoneNumber) {
        Phonenumber.PhoneNumber number = delegate.parsePhoneNumber(phoneNumber);
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
        delegate.dispatchOnRestoreInstanceState(ss);

        super.onRestoreInstanceState(ss.getSuperState());
    }

    @Override
    public void setMaskBuilder(MaskBuilder maskBuilder) {
        delegate.setMaskBuilder(maskBuilder);
    }

    @Override
    public Character getMaskChar() {
        MaskChar maskChar = MaskedPhoneUtils.findNumericMaskChar(this);
        return maskChar != null ? maskChar.getMaskChar() : null;
    }
}
