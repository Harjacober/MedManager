package com.example.original_tech.medmanager.utils;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.example.original_tech.medmanager.services.MedicationReminderFirebaseJobService;
import com.firebase.jobdispatcher.Constraint;

import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

/**
 * Created by Original-Tech on 4/1/2018.
 */

public class ReminderUtilities {
    private static boolean sInitialized;
    private static final String REMINDER_JON_TAG = "medication-reminder-tag";
    private static final int REMINDER_INTERVAL_MIN = 15;
    private static final int REMINDER_INTERVAL_SEC = (int) (TimeUnit.MINUTES.toSeconds(REMINDER_INTERVAL_MIN));
    private static final int SYNC_FLEX_TIME_SEC = REMINDER_INTERVAL_SEC;

    public static void scheduleMedicationReminder(Context context, int interval, Bundle bundle){
        if (sInitialized) return;
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher firebaseJobDispatcher = new FirebaseJobDispatcher(driver);
        Job reminderJob = firebaseJobDispatcher.newJobBuilder()
                .setService(MedicationReminderFirebaseJobService.class)
                .setTag(REMINDER_JON_TAG)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        0
                        , 60 ))
                .setReplaceCurrent(false)
                .setExtras(bundle)
                .build();
        firebaseJobDispatcher.mustSchedule(reminderJob);
        Toast.makeText(context, "Task Scheduled", Toast.LENGTH_SHORT).show();
        sInitialized = true;

    }

    private static int start(int interval){
        int remainderIntervalHours = 24/interval;
        int remainderInttervalSeconds = (int) (TimeUnit.HOURS.toSeconds(remainderIntervalHours));
        return remainderInttervalSeconds;
    }

    private static int end(int interval){
        int remainderIntervalHours = 24/interval;
        int remainderInttervalSeconds = (int) (TimeUnit.HOURS.toSeconds(remainderIntervalHours));
        int syncFlexTimeSeconds = remainderInttervalSeconds;
        return remainderInttervalSeconds + syncFlexTimeSeconds;
    }
}
