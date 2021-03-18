package com.shaubert.ui.phone;

import android.content.Context;
import android.os.Build;

import java.util.Locale;

public class AndroidCurrentLocaleProvider implements Countries.CurrentLocaleProvider {

    private final Context context;

    public AndroidCurrentLocaleProvider(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public Locale getCurrentLocale() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.getResources().getConfiguration().getLocales().get(0);
        } else {
            return context.getResources().getConfiguration().locale;
        }
    }
}
