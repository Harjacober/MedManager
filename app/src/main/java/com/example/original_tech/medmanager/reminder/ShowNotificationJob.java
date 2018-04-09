package com.example.original_tech.medmanager.reminder;

import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;
import com.example.original_tech.medmanager.sync.ReminderTask;

import java.util.concurrent.TimeUnit;

/**
 * Created by Original-Tech on 4/8/2018.
 */

public class ShowNotificationJob extends Job {

    static final String TAG = "show_notification_job_tag";
    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        PersistableBundleCompat bundleCompat = params.getExtras();
        ReminderTask.executeTask(getContext(), ReminderTask.ACTION_MEDICATION_REMINDER,
                bundleCompat.getString("unique-id", ""),
                bundleCompat.getString("med-name", ""),
                bundleCompat.getString("med-desc", ""));
        return Result.SUCCESS;
    }

    public static void schedulePeriodic(long interval, long duration, PersistableBundleCompat bundle) {
        new JobRequest.Builder(ShowNotificationJob.TAG)
                .setPeriodic(TimeUnit.MINUTES.toMillis(interval), TimeUnit.MINUTES.toMillis(duration))
                .setUpdateCurrent(true)
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
