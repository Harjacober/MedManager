package com.example.original_tech.medmanager.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;


import com.example.original_tech.medmanager.MainActivity;
import com.example.original_tech.medmanager.MedicationDetailsActivity;
import com.example.original_tech.medmanager.R;
import com.example.original_tech.medmanager.services.MedicationReminderIntentService;
import com.example.original_tech.medmanager.sync.ReminderTask;

/**
 * Created by Original-Tech on 4/1/2018.
 */

public class NotificationUtils {
    private static final int MEDICATION_RWMINDER_PENDING_INTENT_ID = 64;
    private static final int MEDICATION_RWMINDER_NOTIFICATION_ID = 644;
    private static final int ACTION_IGNORE_PENDING_INTENT_ID = 355;
    private static final int ACTION_MEDICATION_PENDING_INTENT_ID = 254;
    public static final String UNIQUE_ID_KEY = "uniqueId-key";

    public static void clearAllNotifications(Context context){
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static void remindUserToTakeMedication(Context context, String uniqueId,
                                                  String medName, String medDesc){
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_edit_black_24dp)
                .setLargeIcon(largeIcon(context))
                .setContentTitle("Med Manager")
                .setContentText("Time to take your medication " + medName)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        medDesc
                )).setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context, uniqueId))
                .addAction(medicationTakenAction(context))
                .addAction(ignoreReminderAction(context))
                .setAutoCancel(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        }

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(MEDICATION_RWMINDER_NOTIFICATION_ID,
                notificationBuilder.build());


    }

    private static PendingIntent contentIntent(Context context, String uniqueId){
        Intent startActivityIntent = new Intent(context, MedicationDetailsActivity.class);
        startActivityIntent.putExtra(UNIQUE_ID_KEY, uniqueId);

        return PendingIntent.getActivity(context,
                MEDICATION_RWMINDER_PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static Bitmap largeIcon(Context context){
        Resources res = context.getResources();
        Bitmap largeIcon = BitmapFactory.decodeResource(res, R.drawable.ic_search_white_24dp);
        return largeIcon;
    }

    private static NotificationCompat.Action ignoreReminderAction(Context context){
        Intent ignoreReminderIntent = new Intent(context, MedicationReminderIntentService.class);
        ignoreReminderIntent.setAction(ReminderTask.ACTION_DISMISS_NOTIFICATION);
        PendingIntent ignoreReminderPendingIntent = PendingIntent.getService(context,
                ACTION_IGNORE_PENDING_INTENT_ID,
                ignoreReminderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action ignoreReminderAction = new NotificationCompat.Action(R.drawable.ic_edit_black_24dp,
                "No thanks",
                ignoreReminderPendingIntent);
        return ignoreReminderAction;
    }

    private static NotificationCompat.Action medicationTakenAction(Context context){
        Intent ignoreReminderIntent = new Intent(context, MedicationReminderIntentService.class);
        ignoreReminderIntent.setAction(ReminderTask.ACTION_MEDICATION_TAKEN);
        PendingIntent ignoreReminderPendingIntent = PendingIntent.getService(context,
                ACTION_MEDICATION_PENDING_INTENT_ID,
                ignoreReminderIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Action ignoreReminderAction = new NotificationCompat.Action(R.drawable.ic_edit_black_24dp,
                "I took it",
                ignoreReminderPendingIntent);
        return ignoreReminderAction;
    }
}
