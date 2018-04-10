package com.example.original_tech.medmanager.reminder;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;
import com.example.original_tech.medmanager.sync.ReminderTask;

import java.util.concurrent.TimeUnit;

/**
 * Created by Original-Tech on 4/8/2018.
 */
// This schedule the Exact time reminder about notification should start
public class ShowNotificationJob extends Job {

    static final String TAG = "show_notification_job_tag";
    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        Log.i("jobstarted", "this job has actually started");
        PersistableBundleCompat bundleCompat = params.getExtras();
        long interval = intervaltoMinutes(bundleCompat.getInt("interval", 0));
        ScheduleReminderJob.schedulePeriodic(interval, bundleCompat);
        return Result.SUCCESS;
    }

    public static void scheduleExact(long startTime, PersistableBundleCompat bundle) {
        new JobRequest.Builder(ShowNotificationJob.TAG)
//                .setPeriodic(TimeUnit.MINUTES.toMillis(interval), TimeUnit.MINUTES.toMillis(duration))
                .setExact(startTime)
                .setUpdateCurrent(false)
                .setPersisted(true)
                .setExtras(bundle)
                .build()
                .schedule();
    }

    public static long intervaltoMinutes(int noOfTimes){
        //Medication intake interval in hours
        long remainderIntervalHours = 24/noOfTimes;
        //Medication intake interval in seconds
        return (TimeUnit.HOURS.toMinutes(remainderIntervalHours));
    }
}
