# Phone Input
Widget to enter phone numbers in international format

### Gradle
    
    repositories {
        maven{url "https://github.com/shaubert/maven-repo/raw/master/releases"}
    }
    dependencies {
        compile 'com.shaubert.ui.phone:library:1.7'
    }

or use `com.shaubert.ui.phone:masked` for masked input, or `com.shaubert.ui.phone:masked-met` for masked MaterialEditText input.

Init EmojiCompat in your Application.onCreate() method.

    private static void loadEmojiFont(Context context) {
            final FontRequest fontRequest = new FontRequest(
                    "com.google.android.gms.fonts",
                    "com.google.android.gms",
                    "Noto Color Emoji Compat",
                    R.array.com_google_android_gms_fonts_certs);
            EmojiCompat.init(new FontRequestEmojiCompatConfig(context, fontRequest));
    }

[Font certs file](https://github.com/googlesamples/android-EmojiCompat/blob/master/app/src/main/res/values/font_certs.xml)

### Thanks
 * [heetch for Android-Country-Picker](https://github.com/heetch/Android-country-picker);
 * [Rimoto for IntlPhoneInput](https://github.com/Rimoto/IntlPhoneInput).
