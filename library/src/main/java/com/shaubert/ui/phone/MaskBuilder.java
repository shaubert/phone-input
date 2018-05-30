package com.shaubert.ui.phone;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public interface MaskBuilder {

    Mask getMask(Country country,
                 PhoneNumberUtil.PhoneNumberFormat phoneNumberFormat);

    String getValueForMask(Phonenumber.PhoneNumber phoneNumber,
                           Country country,
                           PhoneNumberUtil.PhoneNumberFormat phoneNumberFormat);

}
