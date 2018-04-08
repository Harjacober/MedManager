package com.example.original_tech.medmanager.reminder;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

/**
 * Created by Original-Tech on 4/8/2018.
 */

public class DemoJobcreator implements JobCreator {
    @Override
    public Job create(String tag) {
        switch (tag) {
            case ShowNotificationJob.TAG:
                return new ShowNotificationJob();
            default:
                return null;
        }
    }
}
