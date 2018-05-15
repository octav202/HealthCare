package com.simona.healthcare.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.simona.healthcare.exercise.Exercise;

import java.util.ArrayList;
import java.util.List;

import static com.simona.healthcare.utils.Constants.TAG;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper sInstance;

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "HealthCare";

    // Table Names
    private static final String TABLE_EXERCISES = "EXERCISES";
    private static final String TABLE_CATEGORIES = "CATEGORIES";
    private static final String TABLE_PROGRAMS = "PROGRAMS";
    private static final String TABLE_EVENTS = "EVENTS";
    private static final String TABLE_RECIPES = "RECIPES";

    // TABLE_EXERCISES columns
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "text";
    private static final String KEY_SETS = "sets";
    private static final String KEY_REPS = "reps";
    private static final String KEY_SET_DURATION = "set_duration";
    private static final String KEY_BREAK_DURATION = "break_duration";
    private static final String KEY_DESCRIPTION = "description";

    public static DatabaseHelper getInstance(Context context){
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context);
        }
        return  sInstance;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "DB OnCreate()");

        // Create Tables
        String CREATE_EXERCISES_TABLE = "CREATE TABLE " + TABLE_EXERCISES + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_SETS + " INTEGER,"
                + KEY_REPS + " INTEGER,"
                + KEY_SET_DURATION + " INTEGER,"
                + KEY_BREAK_DURATION + " INTEGER,"
                + KEY_DESCRIPTION + " TEXT" + ")";
        db.execSQL(CREATE_EXERCISES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISES);

        // Create tables again
        onCreate(db);
    }

    /**
     * Add Exercise
     * @param ex
     */
    public boolean addExercise(Exercise ex) {
        Log.d(TAG, "DB addExercise() " + ex);

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, getNextExerciseId());
        values.put(KEY_NAME, ex.getName());
        values.put(KEY_SETS, ex.getSets());
        values.put(KEY_REPS, ex.getRepsPerSet());
        values.put(KEY_SET_DURATION, ex.getSetDuration());
        values.put(KEY_BREAK_DURATION, ex.getBreak());
        values.put(KEY_DESCRIPTION, ex.getDescription());
        long status = db.insertOrThrow(TABLE_EXERCISES, null, values);

        if (status < 0) {
            Log.d(TAG, "addExercise() FAILED");
            return false;
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

        return true;
    }

    /**
     * Get Exercises for Id
     * @param id
     * @return
     */
    public Exercise getExerciseForId(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_EXERCISES,
                new String[]{KEY_ID, KEY_NAME, KEY_SETS, KEY_REPS,
                        KEY_SET_DURATION, KEY_BREAK_DURATION, KEY_DESCRIPTION},
                KEY_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        Exercise ex = new Exercise();
        ex.setId(cursor.getInt(0));
        ex.setName(cursor.getString(1));
        ex.setSets(cursor.getInt(2));
        ex.setRepsPerSet(cursor.getInt(3));
        ex.setSetDuration(cursor.getInt(4));
        ex.setBreak(cursor.getInt(5));
        ex.setDescription(cursor.getString(6));

        return ex;
    }

    /**
     * Get all exercises.
     * @return list of exercises.
     */
    public List<Exercise> getExercises() {
        List<Exercise> exercises = new ArrayList<Exercise>();
        String selectQuery = "SELECT  * FROM " + TABLE_EXERCISES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Exercise ex = new Exercise();
                ex.setId(cursor.getInt(0));
                ex.setName(cursor.getString(1));
                ex.setSets(cursor.getInt(2));
                ex.setRepsPerSet(cursor.getInt(3));
                ex.setSetDuration(cursor.getInt(4));
                ex.setBreak(cursor.getInt(5));
                ex.setDescription(cursor.getString(6));
                exercises.add(ex);
            } while (cursor.moveToNext());
        } else {
            Log.d(TAG, " getFilters - No Filters Found");
        }
        return exercises;
    }

    /**
     * Get the next valid exercise id.
     * Used when adding a new exercise.
     * @return id
     */
    private int getNextExerciseId() {
        String selectQuery = "SELECT MAX(id) FROM " + TABLE_EXERCISES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        int nextId = 0;
        if (cursor.moveToFirst()) {
            String currentId = cursor.getString(0);
            if (currentId != null) {
                nextId = Integer.parseInt(currentId) + 1;
            } else {
                nextId = 0;
            }
        }

        Log.d(TAG, " getNextExerciseId() " + nextId);
        return nextId;
    }

}
