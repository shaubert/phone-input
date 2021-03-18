package com.shaubert.ui.phone.sample;

import android.content.Context;
import android.os.Bundle;
import androidx.emoji.text.EmojiCompat;
import androidx.emoji.text.FontRequestEmojiCompatConfig;
import androidx.core.provider.FontRequest;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.shaubert.ui.phone.AndroidCurrentLocaleProvider;
import com.shaubert.ui.phone.Countries;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolbar();

        Countries.setCurrentLocaleProvider(new AndroidCurrentLocaleProvider(this));
        loadEmojiFont(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_content, new MainFragment())
                    .commit();
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.ab_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    private static void loadEmojiFont(Context context) {
        final FontRequest fontRequest = new FontRequest(
                "com.google.android.gms.fonts",
                "com.google.android.gms",
                "Noto Color Emoji Compat",
                R.array.com_google_android_gms_fonts_certs);
        EmojiCompat.init(new FontRequestEmojiCompatConfig(context, fontRequest));
    }
}