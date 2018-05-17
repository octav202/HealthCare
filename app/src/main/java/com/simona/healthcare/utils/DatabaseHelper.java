package com.simona.healthcare.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.simona.healthcare.event.Event;
import com.simona.healthcare.exercise.Exercise;
import com.simona.healthcare.program.Program;

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
    private static final String TABLE_PROGRAMS = "PROGRAMS";
    private static final String TABLE_PROGRAMS_EXERCISES = "PROGRAMS_EXERCISES";
    private static final String TABLE_PROGRAMS_DAYS = "PROGRAMS_DAYS";
    private static final String TABLE_EVENTS = "EVENTS";

    private static final String TABLE_CATEGORIES = "CATEGORIES";

    private static final String TABLE_RECIPES = "RECIPES";

    // TABLE_EXERCISES Columns
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_SETS = "sets";
    private static final String KEY_REPS = "reps";
    private static final String KEY_SET_DURATION = "set_duration";
    private static final String KEY_BREAK_DURATION = "break_duration";
    private static final String KEY_DESCRIPTION = "description";

    // TABLE_PROGRAMS_EXERCISES Columns - used for mapping program - exercise
    private static final String KEY_PROGRAM_ID = "program_id";
    private static final String KEY_EXERCISE_ID = "exercise_id";

    // TABLE_PROGRAMS_DAYS Columns - used for mapping program - days
    private static final String KEY_DAY_ID = "day_id";

    // TABLE_EVENTS Columns
    private static final String KEY_INTERVAL = "interval";
    private static final String KEY_ACTIVE = "active";

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

        // Create Exercises Table
        String CREATE_EXERCISES_TABLE = "CREATE TABLE " + TABLE_EXERCISES + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_SETS + " INTEGER,"
                + KEY_REPS + " INTEGER,"
                + KEY_SET_DURATION + " INTEGER,"
                + KEY_BREAK_DURATION + " INTEGER,"
                + KEY_DESCRIPTION + " TEXT" + ")";
        db.execSQL(CREATE_EXERCISES_TABLE);

        // Create Programs Table
        String CREATE_PROGRAMS_TABLE = "CREATE TABLE " + TABLE_PROGRAMS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT" + ")";
        db.execSQL(CREATE_PROGRAMS_TABLE);

        // Create PROGRAMS_EXERCISE Table.
        String CREATE_PROGRAMS_EXERCISE_TABLE = "CREATE TABLE " + TABLE_PROGRAMS_EXERCISES + "("
                + KEY_PROGRAM_ID + " INTEGER ,"
                + KEY_EXERCISE_ID + " INTEGER" + ")";
        db.execSQL(CREATE_PROGRAMS_EXERCISE_TABLE);

        // Create PROGRAMS_DAYS Table.
        String CREATE_PROGRAMS_DAYS_TABLE = "CREATE TABLE " + TABLE_PROGRAMS_DAYS + "("
                + KEY_PROGRAM_ID + " INTEGER ,"
                + KEY_DAY_ID + " INTEGER" + ")";
        db.execSQL(CREATE_PROGRAMS_DAYS_TABLE);

        // Create EVENTS Table
        String CREATE_EVENTS_TABLE = "CREATE TABLE " + TABLE_EVENTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_DESCRIPTION + " INTEGER,"
                + KEY_INTERVAL + " INTEGER,"
                + KEY_ACTIVE + " INTEGER "+ ")";
        db.execSQL(CREATE_EVENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISES);

        // Create tables again
        onCreate(db);
    }


    // ------------------------------------------------------
    // ____________________ EXERCISES _______________________
    // ______________________________________________________

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
     * Update Exercise
     * @param ex
     */
    public boolean updateExercise(Exercise ex) {
        Log.d(TAG, "DB updateExercise() " + ex);

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, ex.getId());
        values.put(KEY_NAME, ex.getName());
        values.put(KEY_SETS, ex.getSets());
        values.put(KEY_REPS, ex.getRepsPerSet());
        values.put(KEY_SET_DURATION, ex.getSetDuration());
        values.put(KEY_BREAK_DURATION, ex.getBreak());
        values.put(KEY_DESCRIPTION, ex.getDescription());
        long status = db.update(TABLE_EXERCISES, values,
                KEY_ID + " = ?",
                new String[] { String.valueOf(ex.getId()) });

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
     * Delete Exercise
     * @param ex
     */
    public boolean deleteExercise(Exercise ex) {
        Log.d(TAG, "DB deleteExercise() " + ex);

        if (!canDeleteExercise(ex)) {
            Log.d(TAG, "DB Cannot Delete Exercise - Used in programs.");
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        long status = db.delete(TABLE_EXERCISES, KEY_ID + " = ?",
                new String[] { String.valueOf(ex.getId()) });

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
            Log.d(TAG, " getExercises - No exercises Found");
        }
        return exercises;
    }

    /**
     * Check if exercise is used by any program before deleting it.
     * @param ex
     * @return
     */
    public boolean canDeleteExercise(Exercise ex) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PROGRAMS_EXERCISES,
                new String[]{KEY_PROGRAM_ID, KEY_EXERCISE_ID},
                KEY_EXERCISE_ID + "=?",
                new String[]{String.valueOf(ex.getId())},
                null, null, null, null);

        if (cursor.getCount() >0) {
            // Programs found using this exercise. DO NOT delete it.
            return false;
        }
        return true;
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

    // ------------------------------------------------------
    // ____________________ PROGRAMS ________________________
    // ______________________________________________________

    /**
     * Add Program
     * @param prog
     */
    public boolean addProgram(Program prog) {
        Log.d(TAG, "DB addProgram() " + prog);

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        // If Id is 0, get next valid id from database
        if (prog.getId() == 0) {
            prog.setId(getNextProgramId());
        }

        // Add program to PROGRAMS database
        ContentValues values = new ContentValues();
        values.put(KEY_ID, prog.getId());
        values.put(KEY_NAME, prog.getName());
        long status = db.insertOrThrow(TABLE_PROGRAMS, null, values);

        if (status < 0) {
            Log.d(TAG, "addProgram() FAILED");
            return false;
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

        // Add exercises for program
        for (Exercise e : prog.getExercises()) {
            addExerciseForProgram(prog, e);
        }

        // Add days for program
        for (Integer day : prog.getDays()) {
            addDayForProgram(prog,day);
        }
        return true;
    }

    /**
     * Get Program for Id
     * @param id
     * @return
     */
    public Program getProgramForId(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PROGRAMS,
                new String[]{KEY_ID, KEY_NAME},
                KEY_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        Program prog = new Program();
        prog.setId(cursor.getInt(0));
        prog.setName(cursor.getString(1));

        return prog;
    }

    /**
     * Get all programs.
     * @return list of programs.
     */
    public List<Program> getPrograms() {
        Log.d(TAG, "getPrograms()");
        List<Program> programs = new ArrayList<Program>();
        String selectQuery = "SELECT  * FROM " + TABLE_PROGRAMS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Program prog = new Program();
                prog.setId(cursor.getInt(0));
                prog.setName(cursor.getString(1));
                programs.add(prog);
                Log.d(TAG, prog.toString());
            } while (cursor.moveToNext());
        } else {
            Log.d(TAG, " getPrograms - No programs Found");
        }
        return programs;
    }

    /**
     * Get the next valid program id.
     * Used when adding a new program.
     * @return id
     */
    private int getNextProgramId() {
        String selectQuery = "SELECT MAX(id) FROM " + TABLE_PROGRAMS;
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

        Log.d(TAG, " getNextProgramId() " + nextId);
        return nextId;
    }

    /**
     * Delete Program
     * @param program
     */
    public boolean deleteProgram(Program program) {
        Log.d(TAG, "DB deleteProgram() " + program);

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        long status = db.delete(TABLE_PROGRAMS, KEY_ID + " = ?",
                new String[] { String.valueOf(program.getId()) });

        if (status < 0) {
            Log.d(TAG, "addExercise() FAILED");
            return false;
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

        // Delete entries from PROGRAM_EXERCISES
        deleteExercisesForProgram(program);

        // Delete entries from PROGRAM_DAYS
        deleteDaysForProgram(program);
        return true;
    }

    // ----------------------------------------------------------------
    // ____________________ PROGRAMS_EXERCISES ________________________
    // ________________________________________________________________

    /**
     * Added Exercise for Program.
     *
     * @param prog - program
     * @param ex - exercise to be added for program
     * @return operation status.
     */
    public boolean addExerciseForProgram(Program prog, Exercise ex) {
        Log.d(TAG, "DB addExerciseForProgram() " + prog + " " + ex);

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        // Add exercise for program
        ContentValues values = new ContentValues();
        values.put(KEY_PROGRAM_ID, prog.getId());
        values.put(KEY_EXERCISE_ID, ex.getId());
        long status = db.insertOrThrow(TABLE_PROGRAMS_EXERCISES, null, values);

        if (status < 0) {
            Log.d(TAG, "addExerciseForProgram() FAILED");
            return false;
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

        return true;
    }

    /**
     * Get Exercises for Program Id
     * @param id
     * @return
     */
    public List<Exercise> getExercisesForProgramId(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PROGRAMS_EXERCISES,
                new String[]{KEY_PROGRAM_ID, KEY_EXERCISE_ID},
                KEY_PROGRAM_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null);


        List<Exercise> list = new ArrayList<Exercise>();

        if (cursor.moveToFirst()) {
            do {
                Exercise ex = getExerciseForId(cursor.getInt(1));
                list.add(ex);
            } while (cursor.moveToNext());
        } else {
            Log.d(TAG, " getExercisesForProgramId - Not Found for program " + id);
        }

        return list;
    }

    /**
     * Delete Exercises for Program
     * @param program
     */
    public boolean deleteExercisesForProgram(Program program) {
        Log.d(TAG, "DB deleteExercisesForProgram() " + program);

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        long status = db.delete(TABLE_PROGRAMS_EXERCISES, KEY_PROGRAM_ID + " = ?",
                new String[] { String.valueOf(program.getId()) });

        if (status < 0) {
            Log.d(TAG, "addExercise() FAILED");
            return false;
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        return true;
    }

    // ----------------------------------------------------------------
    // ____________________ PROGRAMS_DAYS ____________________________
    // ________________________________________________________________

    /**
     * Added Day for Program.
     *
     * @param prog - program
     * @param day - exercise to be added for program
     * @return operation status.
     */
    public boolean addDayForProgram(Program prog, Integer day) {
        Log.d(TAG, "DB addDayForProgram() " + prog + " " + day);

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        // Add exercise for program
        ContentValues values = new ContentValues();
        values.put(KEY_PROGRAM_ID, prog.getId());
        values.put(KEY_DAY_ID, day);
        long status = db.insertOrThrow(TABLE_PROGRAMS_DAYS, null, values);

        if (status < 0) {
            Log.d(TAG, "addDayForProgram() FAILED");
            return false;
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

        return true;
    }

    /**
     * Get Days for Program Id
     * @param id
     * @return
     */
    public List<Integer> getDaysForProgramId(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PROGRAMS_DAYS,
                new String[]{KEY_PROGRAM_ID, KEY_DAY_ID},
                KEY_PROGRAM_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null);


        List<Integer> list = new ArrayList<Integer>();

        if (cursor.moveToFirst()) {
            do {
                Integer day = cursor.getInt(1);
                list.add(day);
            } while (cursor.moveToNext());
        } else {
            Log.d(TAG, " getDaysForProgramId - Not Found for program " + id);
        }

        return list;
    }

    /**
     * Get Programs For Dau
     * @param id
     * @return
     */
    public List<Program> getProgramsForDay(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PROGRAMS_DAYS,
                new String[]{KEY_PROGRAM_ID, KEY_DAY_ID},
                KEY_DAY_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null);


        List<Program> list = new ArrayList<Program>();

        if (cursor.moveToFirst()) {
            do {
                Program program = getProgramForId(cursor.getInt(0));
                list.add(program);
            } while (cursor.moveToNext());
        } else {
            Log.d(TAG, " getProgramsForDays() - Not Found for program " + id);
        }

        return list;
    }

    /**
     * Delete Days for Program
     * @param program
     */
    public boolean deleteDaysForProgram(Program program) {
        Log.d(TAG, "DB deleteDaysForProgram() " + program);

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        long status = db.delete(TABLE_PROGRAMS_DAYS, KEY_PROGRAM_ID + " = ?",
                new String[] { String.valueOf(program.getId()) });

        if (status < 0) {
            Log.d(TAG, "addExercise() FAILED");
            return false;
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        return true;
    }

    // ------------------------------------------------------
    // ____________________ EVENTS __________________________
    // ______________________________________________________

    /**
     * Add Event
     * @param event
     */
    public boolean addEvent(Event event) {
        Log.d(TAG, "DB addEvent() " + event);

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, getNextEventId());
        values.put(KEY_NAME, event.getName());
        values.put(KEY_DESCRIPTION, event.getDescription());
        values.put(KEY_INTERVAL, event.getInterval());
        values.put(KEY_ACTIVE, event.isActive() ? 1 : 0);
        long status = db.insertOrThrow(TABLE_EVENTS, null, values);

        if (status < 0) {
            Log.d(TAG, "addEvent() FAILED");
            return false;
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

        return true;
    }

    /**
     * Update Event
     * @param event
     */
    public boolean updateEvent(Event event) {
        Log.d(TAG, "DB updateEvent() " + event);

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, event.getId());
        values.put(KEY_NAME, event.getName());
        values.put(KEY_DESCRIPTION, event.getDescription());
        values.put(KEY_INTERVAL, event.getInterval());
        values.put(KEY_ACTIVE, event.isActive() ? 1 : 0);
        long status = db.update(TABLE_EVENTS, values,
                KEY_ID + " = ?",
                new String[] { String.valueOf(event.getId()) });

        if (status < 0) {
            Log.d(TAG, "updateEvent() FAILED");
            return false;
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

        return true;
    }

    /**
     * Delete Event
     * @param event
     */
    public boolean deleteEvent(Event event) {
        Log.d(TAG, "DB deleteEvent() " + event);

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        long status = db.delete(TABLE_EVENTS, KEY_ID + " = ?",
                new String[] { String.valueOf(event.getId()) });

        if (status < 0) {
            Log.d(TAG, "deleteEvent() FAILED");
            return false;
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

        return true;
    }

    /**
     * Get Event for Id
     * @param id
     * @return
     */
    public Event getEventForId(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_EVENTS,
                new String[]{KEY_ID, KEY_NAME, KEY_DESCRIPTION, KEY_INTERVAL, KEY_ACTIVE},
                KEY_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        Event event = new Event();
        event.setId(cursor.getInt(0));
        event.setName(cursor.getString(1));
        event.setDescription(cursor.getString(2));
        event.setInterval(cursor.getInt(3));
        event.setActive(cursor.getInt(4) == 1 ? true : false);

        return event;
    }

    /**
     * Get all events.
     * @return list of events.
     */
    public List<Event> getEvents() {
        List<Event> events = new ArrayList<Event>();
        String selectQuery = "SELECT  * FROM " + TABLE_EVENTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Event event = new Event();
                event.setId(cursor.getInt(0));
                event.setName(cursor.getString(1));
                event.setDescription(cursor.getString(2));
                event.setInterval(cursor.getInt(3));
                event.setActive(cursor.getInt(4) == 1 ? true : false);
                events.add(event);
            } while (cursor.moveToNext());
        } else {
            Log.d(TAG, " getEvents - No events Found");
        }
        return events;
    }

    /**
     * Get the next valid event id.
     * Used when adding a new event.
     * @return id
     */
    private int getNextEventId() {
        String selectQuery = "SELECT MAX(id) FROM " + TABLE_EVENTS;
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

        Log.d(TAG, " getNextEventId() " + nextId);
        return nextId;
    }
}
