# Phone Input
Widget to enter phone numbers in international format

### Gradle
    
    repositories {
        maven{url "https://github.com/shaubert/maven-repo/raw/master/releases"}
    }
    dependencies {
        compile 'com.shaubert.ui.phone:library:1.6.0'
    }

or use `com.shaubert.ui.phone:masked` for masked input, or `com.shaubert.ui.phone:masked-met` for masked MaterialEditText input.

Call Countries.init(context) from your Application.onCreate() method.

### Thanks
 * [heetch for Android-Country-Picker](https://github.com/heetch/Android-country-picker);
 * [Rimoto for IntlPhoneInput](https://github.com/Rimoto/IntlPhoneInput).
