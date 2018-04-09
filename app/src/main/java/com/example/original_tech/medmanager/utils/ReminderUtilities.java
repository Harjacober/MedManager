package com.example.original_tech.medmanager.utils;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.example.original_tech.medmanager.MedicationDetailsActivity;
import com.example.original_tech.medmanager.services.MedicationReminderFirebaseJobService;
import com.firebase.jobdispatcher.Constraint;

import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

/**
 * Created by Original-Tech on 4/1/2018.
 */

public class ReminderUtilities {
    private static boolean sInitialized;
    private static final String REMINDER_JON_TAG = "medication-reminder-tag";

    public static void scheduleMedicationReminder(Context context, int interval,
                                                  long duration, Bundle bundle){
        if (sInitialized) return;
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher firebaseJobDispatcher = new FirebaseJobDispatcher(driver);
        Job reminderJob = firebaseJobDispatcher.newJobBuilder()
                .setService(MedicationReminderFirebaseJobService.class)
                .setTag(REMINDER_JON_TAG)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        start(duration),
                        end(interval, duration)))
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setReplaceCurrent(false)
                .setExtras(bundle)
                .build();
        firebaseJobDispatcher.mustSchedule(reminderJob);
        Toast.makeText(context, "Reminder Scheduled for this Medication", Toast.LENGTH_SHORT).show();
        sInitialized = true;
//        Log.e("qqqqqqqqq","Id "+(firebaseJobDispatcher.schedule(reminderJob) == SCHEDULE_RESULT_SUCCESS?"Success":"Fail");
    }
/**returns the duration between the start time of medication and current time
    so as to know when to start reminding user about medication*/
    private static int start(long startTime){

        return (int) (TimeUnit.MILLISECONDS.toSeconds(startTime));
    }

    private static int end(int interval, long time){
        //Medication intake interval in hours
        long remainderIntervalHours = 24/interval;
        //Medication intake interval in seconds
        int remainderIntervalSeconds = (int) (TimeUnit.HOURS.toSeconds(remainderIntervalHours));
        int syncFlexTimeSeconds = start(time);
        return syncFlexTimeSeconds + remainderIntervalSeconds;
    }

}
