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
                bundleCompat.getString("med-name", ""),
                bundleCompat.getString("med-desc", ""),
                bundleCompat.getString("unique-id", ""));
        return Result.SUCCESS;
    }

    public static void schedulePeriodic(long duration, PersistableBundleCompat bundle) {
        new JobRequest.Builder(ShowNotificationJob.TAG)
                .setPeriodic(TimeUnit.MINUTES.toMillis(duration), TimeUnit.MINUTES.toMillis(1))
                .setUpdateCurrent(true)
                .setPersisted(true)
                .setExtras(bundle)
                .build()
                .schedule();
    }
}
