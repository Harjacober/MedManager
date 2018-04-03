package com.example.original_tech.medmanager.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Original-Tech on 3/30/2018.
 */

public class MedicationContract {
    public static final String CONTENT_AUTHORITY="com.example.original_tech.medmanager";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    private MedicationContract(){}

    public static final class MedicationEntry implements BaseColumns {
        public final static String _ID = BaseColumns._ID;
        public static final String TABLE_NAME = "Medication";
        public static final String COLUMN_MED_NAME = "med_name";
        public static final String COLUMN_MED_DESC = "med_desc";
        public static final String COLUMN_MED_INTERVAL = "med_interval";
        public static final String COLUMN_START_DATE = "start_date";
        public static final String COLUMN_END_DATE = "end_date";
        public static final String COLUMN_PREF_START_TIME = "pref_start_time";
        public static final String UNIQUE_ID = "unique_id";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        public static Uri buildMedicationUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
