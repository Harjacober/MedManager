package com.example.original_tech.medmanager.services;

import android.annotation.TargetApi;
import android.content.Context;;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.example.original_tech.medmanager.sync.ReminderTask;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by Original-Tech on 4/1/2018.
 */

public class MedicationReminderFirebaseJobService extends JobService {
    private AsyncTask mBackgroundTask;

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        Toast.makeText(this, "Task Started", Toast.LENGTH_SHORT).show();
        mBackgroundTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                Bundle bundle = jobParameters.getExtras();
                Context context = MedicationReminderFirebaseJobService.this;
                ReminderTask.executeTask(context, ReminderTask.ACTION_MEDICATION_REMINDER,
                        bundle.getString("med-name"),
                        bundle.getString("med-desc"),
                        bundle.getString("unique-id"));
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                jobFinished(jobParameters, false);
            }
        };
        mBackgroundTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (mBackgroundTask != null) mBackgroundTask.cancel(true);
        return true;
    }

}


