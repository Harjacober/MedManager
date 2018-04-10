package com.example.original_tech.medmanager.reminder;

import android.content.Context;
import android.support.annotation.NonNull;

import com.evernote.android.job.JobCreator;
import com.evernote.android.job.JobManager;

/**
 * Created by Original-Tech on 4/10/2018.
 */

public class AddReceiver extends JobCreator.AddJobCreatorReceiver {
    @Override
    protected void addJobCreator(@NonNull Context context, @NonNull JobManager manager) {
        manager.addJobCreator(new DemoJobcreator());
    }
}
