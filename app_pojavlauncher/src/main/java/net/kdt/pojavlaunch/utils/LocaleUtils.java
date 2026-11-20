package net.kdt.pojavlaunch.utils;


import static net.kdt.pojavlaunch.prefs.LauncherPreferences.DEFAULT_PREF;

import android.content.*;
import android.content.res.*;
import android.os.Build;
import android.os.LocaleList;

import androidx.preference.*;
import java.util.*;

public class LocaleUtils extends ContextWrapper {
    public static final String PREF_KEY_APP_LANGUAGE = "app_language";
    public static final String LANGUAGE_EN = "en";
    public static final String LANGUAGE_RU = "ru";

    public LocaleUtils(Context base) {
        super(base);
    }

    /** @return true if the user has already picked an app language */
    public static boolean isLanguageChosen(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(PREF_KEY_APP_LANGUAGE, null) != null;
    }

    /** Saves the app language. Does not apply it, the activity needs to be recreated for that. */
    public static void saveLanguage(Context context, String language) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_KEY_APP_LANGUAGE, language).apply();
    }

    private static Locale getLocaleFor(String language) {
        if (LANGUAGE_RU.equals(language)) return new Locale(LANGUAGE_RU);
        // English is the default, even if the system language is different
        return Locale.ENGLISH;
    }

    public static ContextWrapper setLocale(Context context) {
        if (DEFAULT_PREF == null) {
            DEFAULT_PREF = PreferenceManager.getDefaultSharedPreferences(context);
        }
        // Too early to initialize all prefs here, as this is called by PojavApplication
        // before storage checks are done and before the storage paths are initialized.
        // So only read the language pref directly.
        Locale locale = getLocaleFor(DEFAULT_PREF.getString(PREF_KEY_APP_LANGUAGE, null));

        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();

        configuration.setLocale(locale);
        Locale.setDefault(locale);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            LocaleList localeList = new LocaleList(locale);
            LocaleList.setDefault(localeList);
            configuration.setLocales(localeList);
        }

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1){
            context = context.createConfigurationContext(configuration);
        }

        return new LocaleUtils(context);
    }
}
