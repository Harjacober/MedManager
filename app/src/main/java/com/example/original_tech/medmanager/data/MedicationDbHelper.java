package com.example.original_tech.medmanager.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Original-Tech on 3/30/2018.
 */

public class MedicationDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME="MedManager.db";
    private static final int DATABASE_VERSION=1;

    public MedicationDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_MED_MANAGER_TABLE="CREATE TABLE "+ MedicationContract.MedicationEntry.TABLE_NAME+"("
                + MedicationContract.MedicationEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MedicationContract.MedicationEntry.COLUMN_MED_NAME+" TEXT NOT NULL, "
                + MedicationContract.MedicationEntry.COLUMN_MED_INTERVAL+" INTEGER NOT NULL, "
                + MedicationContract.MedicationEntry.COLUMN_MED_DESC+" TEXT NOT NULL, "
                + MedicationContract.MedicationEntry.COLUMN_START_DATE+" TEXT NOT NULL, "
                + MedicationContract.MedicationEntry.COLUMN_END_DATE+" TEXT NOT NULL, "
                + MedicationContract.MedicationEntry.UNIQUE_ID+" TEXT NOT NULL, "
                + MedicationContract.MedicationEntry.COLUMN_IMAGE+" TEXT, "
                + MedicationContract.MedicationEntry.COLUMN_MONTH+" TEXT, "
                + MedicationContract.MedicationEntry.COLUMN_PREF_START_TIME+" INTEGER NOT NULL);";
        sqLiteDatabase.execSQL(CREATE_MED_MANAGER_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String DROP_TABLE="DROP IF EXIST "+ MedicationContract.MedicationEntry.TABLE_NAME+";";
        sqLiteDatabase.execSQL(DROP_TABLE);
        onCreate(sqLiteDatabase);
    }
}
