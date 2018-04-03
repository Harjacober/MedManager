package com.example.original_tech.medmanager.sync;

import android.content.Context;

import com.example.original_tech.medmanager.utils.NotificationUtils;

/**
 * Created by Original-Tech on 4/1/2018.
 */

public class ReminderTask {

    public static final String ACTION_MEDICATION_TAKEN = "dismiss-notification";
    public static final String ACTION_DISMISS_NOTIFICATION = "dismiss-notification";
    public static final String ACTION_MEDICATION_REMINDER = "medication-reminder";

    public static void executeTask(Context context, String action, String medName,
                                   String medDesc, String uniqueId){
        if (ACTION_MEDICATION_TAKEN.equals(action)){
            medicationTaken(context);
        }else if (ACTION_DISMISS_NOTIFICATION.equals(action)) {
            NotificationUtils.clearAllNotifications(context);
        }else if (ACTION_MEDICATION_REMINDER.equals(action)){
            remindUserAboutMedication(context, medName, medDesc, uniqueId);
        }
    }

    private static void remindUserAboutMedication(Context context, String medName, String medDesc, String uniqueId) {
        NotificationUtils.remindUserToTakeMedication(context, medName, medDesc, uniqueId);
    }

    private static void medicationTaken(Context context) {
        //handle when user checked that he has taken his medication

        NotificationUtils.clearAllNotifications(context);
    }
}
