package com.simona.healthcare.event;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import com.simona.healthcare.program.Program;

import static com.simona.healthcare.utils.Constants.EXTRA_KEY;
import static com.simona.healthcare.utils.Constants.TAG;

public class EventManager {

    private static EventManager sInstance;
    private Context mContext;
    private AlarmManager mAlarmManager;

    public static EventManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new EventManager(context);
        }
        return sInstance;
    }

    private EventManager(Context context) {
        this.mContext = context;
        mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
    }

    /**
     * Schedule Event.
     */
    public void scheduleEvent(Event event) {
        Log.d(TAG, "scheduleEvent() " + event);
        Intent intent = new Intent(mContext, EventReceiver.class);
        intent.putExtra(EXTRA_KEY, event.getId());
        PendingIntent alarmIntent = PendingIntent.getBroadcast(mContext, event.getId(), intent, 0);

        // Convert to ms
        int interval = event.getInterval() * 1000 * 60;
        mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + interval, interval, alarmIntent);
    }

    /**
     * Cancel scheduled event.
     */
    public void cancelEvent(Event event) {
        Log.d(TAG, "cancelEvent() " + event);
        Intent myIntent = new Intent(mContext, EventReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(mContext,event.getId(), myIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmIntent.cancel();
        mAlarmManager.cancel(alarmIntent);

    }
}
