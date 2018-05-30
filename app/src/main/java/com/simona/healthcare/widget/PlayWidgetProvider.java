package com.simona.healthcare.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.simona.healthcare.R;
import com.simona.healthcare.playing.PlayService;

import static com.simona.healthcare.utils.Constants.ACTION_WIDGET_NEXT;
import static com.simona.healthcare.utils.Constants.ACTION_WIDGET_PLAY;
import static com.simona.healthcare.utils.Constants.ACTION_WIDGET_PREVIOUS;
import static com.simona.healthcare.utils.Constants.SERVICE_ACTION_EXTRA;
import static com.simona.healthcare.utils.Constants.TAG;

public class PlayWidgetProvider extends AppWidgetProvider {


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        final int count = appWidgetIds.length;

        for (int i = 0; i < count; i++) {
            int widgetId = appWidgetIds[i];

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget_layout);
            remoteViews.setTextViewText(R.id.programNameText, "Workout");
            remoteViews.setTextViewText(R.id.exercisesTextView, "Pull Ups");

            // Previous Button
            Intent prev = new Intent(context, PlayWidgetProvider.class);
            prev.setAction(ACTION_WIDGET_PREVIOUS);
            PendingIntent prevPending = PendingIntent.getBroadcast(context, 0, prev, 0);
            remoteViews.setOnClickPendingIntent(R.id.prevButton, prevPending);

            // Play Button
            Intent play = new Intent(context, PlayWidgetProvider.class);
            play.setAction(ACTION_WIDGET_PLAY);
            PendingIntent playPending = PendingIntent.getBroadcast(context, 0, play, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.playButton, playPending);


            // Next Button
            Intent next = new Intent(context, PlayWidgetProvider.class);
            next.setAction(ACTION_WIDGET_NEXT);
            PendingIntent nextPending = PendingIntent.getBroadcast(context, 0, next, 0);
            remoteViews.setOnClickPendingIntent(R.id.nextButton, nextPending);

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d(TAG, "onReceive() " + intent.getAction());



        switch (intent.getAction()) {
            case ACTION_WIDGET_PREVIOUS:
            case ACTION_WIDGET_PLAY:
            case ACTION_WIDGET_NEXT:
                Intent serviceIntent = new Intent(context, PlayService.class);
                serviceIntent.putExtra(SERVICE_ACTION_EXTRA, intent.getAction());
                context.startService(serviceIntent);
                break;
            default:
                break;
        }
    }
}