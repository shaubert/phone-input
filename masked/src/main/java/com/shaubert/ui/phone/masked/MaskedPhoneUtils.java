package com.shaubert.ui.phone.masked;

import android.view.inputmethod.EditorInfo;
import com.shaubert.maskedinput.MaskChar;
import com.shaubert.maskedinput.MaskedInputView;

public class MaskedPhoneUtils {

    public static MaskChar findNumericMaskChar(MaskedInputView inputView) {
        MaskChar numericChar = null;
        MaskChar[] maskChars = inputView.getMaskChars();
        if (maskChars != null) {
            for (MaskChar maskChar : maskChars) {
                if (maskChar.getInputTypeClass() == EditorInfo.TYPE_CLASS_NUMBER) {
                    numericChar = maskChar;
                    break;
                }
            }
        }
        return numericChar;
    }

}
