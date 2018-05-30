package com.shaubert.ui.phone;

import android.widget.EditText;

import com.google.i18n.phonenumbers.PhoneNumberUtil;

public class PlainEditTextMaskHelper {

    public static CustomPhoneNumberFormattingTextWatcher setMask(EditText editText,
                                                          PhoneInputDelegate delegate,
                                                          Mask mask,
                                                          CustomPhoneNumberFormattingTextWatcher formattingTextWatcher) {
        editText.setHint(mask.defaultNumber);
        CustomPhoneNumberFormattingTextWatcher result = null;
        if (formattingTextWatcher != null) {
            editText.removeTextChangedListener(formattingTextWatcher);
        }

        String oldText = editText.getText().toString();
        int digitsBefore = Utils.getDigitsBefore(oldText, editText.getSelectionStart());
        editText.setText(null);

        Country country = delegate.getCountry();
        if (country != null) {
            result = new CustomPhoneNumberFormattingTextWatcher(country);
            editText.addTextChangedListener(result);
        }

        if (delegate.isDisplayCountryCode()) {
            digitsBefore += Utils.getDigitsDifference(
                    delegate.getCountryCode(oldText),
                    mask.prefix);
            editText.setText(delegate.replaceCountryCode(oldText, mask.prefix));
        } else {
            editText.setText(PhoneNumberUtil.normalizeDiallableCharsOnly(oldText));
        }
        editText.setSelection(Utils.getDigitsBeforePos(editText.getText().toString(), digitsBefore));
        return result;
    }
}