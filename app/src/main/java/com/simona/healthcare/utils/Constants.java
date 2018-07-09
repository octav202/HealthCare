package com.simona.healthcare.utils;

public class Constants {
    public static final String TAG = "HEALTH_";
    public static final String EXTRA_KEY = "EXTRA_KEY";
    public static final String EXTRA_KEY_EVENTS = "EXTRA_KEY_EVENTS";
    public static final String EXTRA_KEY_PROGRAMS = "EXTRA_KEY_PROGRAMS";

    // Photo Picker
    public static final int GALLERY_INTENT = 1;

    // Settings
    public static final String PREFS_PROGRAM_TTS_KEY = "program_tts";
    public static final String PREFS_REPS_TTS_KEY = "reps_tts";
    public static final String PREFS_BREAK_TTS_KEY = "break_tts";
    public static final String PREFS_LANG_TTS_KEY = "lang_tts";
    public static final String PREFS_FREQUENCE_TTS_KEY = "frequence_tts";
    public static final String PREFS_PITCH_TTS_KEY = "pitch_tts";

    // PhotoPicker
    public static final int TYPE_EXERCISE = 100;
    public static final int TYPE_RECIPE = 200;

    public static final int STATE_PAUSED = 0;
    public static final int STATE_PLAYING = 1;
    public static final int STATE_STOPPED = 2;

    // Widget
    public static final String ACTION_WIDGET_PREVIOUS = "com.simona.healthcare.PREV";
    public static final String ACTION_WIDGET_PLAY = "com.simona.healthcare.PLAY";
    public static final String ACTION_WIDGET_NEXT = "com.simona.healthcare.NEXT";
    public static final String ACTION_WIDGET_SELECT = "com.simona.healthcare.SELECT";
    public static final String SERVICE_ACTION_EXTRA = "service_action_extra";
}
