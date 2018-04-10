package com.example.original_tech.medmanager.reminder;

import android.support.annotation.NonNull;
import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;
import com.example.original_tech.medmanager.sync.ReminderTask;

import java.util.concurrent.TimeUnit;

/**
 * Created by Original-Tech on 4/10/2018.
 */

public class ScheduleReminderJob extends Job {

    public static final String TAG = "schedule-notification-job";
    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        Log.i("popopoopop", "this job has actually started");
        PersistableBundleCompat bundleCompat = params.getExtras();
        ReminderTask.executeTask(getContext(), ReminderTask.ACTION_MEDICATION_REMINDER,
                bundleCompat.getString("unique-id", ""),
                bundleCompat.getString("med-name", ""),
                bundleCompat.getString("med-desc", ""));
        return Result.SUCCESS;
    }

    public static void schedulePeriodic(long interval, PersistableBundleCompat bundle) {
        new JobRequest.Builder(TAG)
                .setPeriodic(TimeUnit.MINUTES.toMillis(interval), TimeUnit.MINUTES.toMillis(5))
                .setUpdateCurrent(false)
                .setPersisted(true)
                .setExtras(bundle)
                .build()
                .schedule();
    }

    public static int interval(int noOfTimes){
        //Medication intake interval in hours
        long remainderIntervalHours = 24/noOfTimes;
        //Medication intake interval in seconds
        return (int) (TimeUnit.HOURS.toSeconds(remainderIntervalHours));
    }
}
