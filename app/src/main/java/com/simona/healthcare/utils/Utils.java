package com.simona.healthcare.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Locale;

import static com.simona.healthcare.utils.Constants.PREFS_BREAK_TTS_KEY;
import static com.simona.healthcare.utils.Constants.PREFS_FREQUENCE_TTS_KEY;
import static com.simona.healthcare.utils.Constants.PREFS_LANG_TTS_KEY;
import static com.simona.healthcare.utils.Constants.PREFS_PITCH_TTS_KEY;
import static com.simona.healthcare.utils.Constants.PREFS_PROGRAM_TTS_KEY;
import static com.simona.healthcare.utils.Constants.PREFS_REPS_TTS_KEY;

public class Utils {


    // Program TTS
    public static void setProgramTTS(Context context, boolean status) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PREFS_PROGRAM_TTS_KEY, status);
        editor.apply();
    }

    public static boolean getProgramTTS(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean status = preferences.getBoolean(PREFS_PROGRAM_TTS_KEY, true);
        return status;
    }

    // Reps TTS
    public static void setRepTTS(Context context, boolean status) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PREFS_REPS_TTS_KEY, status);
        editor.apply();
    }

    public static boolean getRepsTTS(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean status = preferences.getBoolean(PREFS_REPS_TTS_KEY, true);
        return status;
    }

    // Break TTS
    public static void setBreakTTS(Context context, boolean status) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PREFS_BREAK_TTS_KEY, status);
        editor.apply();
    }

    public static boolean getBreakTTS(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean status = preferences.getBoolean(PREFS_BREAK_TTS_KEY, true);
        return status;
    }

    // Language TTS
    public static void setLanguageTTS(Context context, String lang) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREFS_LANG_TTS_KEY, lang);
        editor.apply();
    }

    public static String getLanguageTTS(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String  lang = preferences.getString(PREFS_LANG_TTS_KEY, "United States");
        return lang;
    }

    public static Locale stringToLocale(String lang) {
        Locale locale = null;
        switch (lang) {
            case "German":
                locale = Locale.GERMANY;
                break;
            case "United Kingdom":
                locale = Locale.UK;
                break;
            case "United States":
                locale = Locale.US;
                break;
            case "French":
                locale = Locale.FRANCE;
                break;
            case "Italian":
                locale = Locale.ITALY;
                break;
            case "Chinese":
                locale = Locale.CHINA;
                break;
            case "Japanese":
                locale = Locale.JAPAN;
                break;
            case "Korean":
                locale = Locale.KOREA;
                break;
            default:
                locale = Locale.US;
                break;
        }

        return locale;
    }

    // Frequence TTS
    public static void setFrequenceTTS(Context context, float pitch) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(PREFS_FREQUENCE_TTS_KEY, pitch);
        editor.apply();
    }

    public static float getFrequenceTTS(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        float frequence = preferences.getFloat(PREFS_FREQUENCE_TTS_KEY, 0.9f);
        return frequence;
    }

    // Pitch TTS
    public static void setPitchTTS(Context context, float pitch) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(PREFS_PITCH_TTS_KEY, pitch);
        editor.apply();
    }

    public static float getPitchTTS(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        float pitch = preferences.getFloat(PREFS_PITCH_TTS_KEY, 0.9f);
        return pitch;
    }

}
