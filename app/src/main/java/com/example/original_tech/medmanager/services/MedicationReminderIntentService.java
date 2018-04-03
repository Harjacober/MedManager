package com.example.original_tech.medmanager.services;

import android.app.IntentService;

import android.content.Intent;
import android.support.annotation.Nullable;

import com.example.original_tech.medmanager.sync.ReminderTask;

/**
 * Created by Original-Tech on 4/1/2018.
 */

public class MedicationReminderIntentService extends IntentService {

    public MedicationReminderIntentService() {
        super("MedicationReminderIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        ReminderTask.executeTask(this, intent.getAction(),
                null,
                null,
                null);
    }
}
