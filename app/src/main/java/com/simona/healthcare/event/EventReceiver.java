package com.simona.healthcare.event;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.simona.healthcare.MainActivity;
import com.simona.healthcare.R;
import com.simona.healthcare.utils.DatabaseHelper;

import static com.simona.healthcare.utils.Constants.EXTRA_KEY;
import static com.simona.healthcare.utils.Constants.EXTRA_KEY_EVENTS;
import static com.simona.healthcare.utils.Constants.TAG;

public class EventReceiver extends WakefulBroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        int eventId = intent.getIntExtra(EXTRA_KEY, 0);
        Log.d(TAG, "onReceive() " + eventId);


        Event event = new Event();
        if (DatabaseHelper.getInstance(context).getEventForId(eventId) != null) {
            event = DatabaseHelper.getInstance(context).getEventForId(eventId);
        }

        Log.d(TAG, "onReceive() " + event);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            CharSequence name = event.getName();
            String description = event.getDescription();
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            String CHANNEL_ID = event.getName();


            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            // Go to events when user taps on notification.
            Intent notificationIntent = new Intent(context, MainActivity.class);
            notificationIntent.putExtra(EXTRA_KEY, EXTRA_KEY_EVENTS);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                    notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);


            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle(event.getName())
                    .setContentText(event.getDescription())
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(event.getDescription()))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(contentIntent).setAutoCancel(true);

            NotificationManagerCompat manager = NotificationManagerCompat.from(context);

            manager.notify(event.getId(), mBuilder.build());
        } else {
            // TO DO Notification for SKD < O
        }
    }
}
