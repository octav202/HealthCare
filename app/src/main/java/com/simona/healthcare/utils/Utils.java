package com.simona.healthcare.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.simona.healthcare.utils.Constants;

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
        Boolean status = preferences.getBoolean(PREFS_PROGRAM_TTS_KEY, false);
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
        Boolean status = preferences.getBoolean(PREFS_REPS_TTS_KEY, false);
        return status;
    }

}
