package com.example.original_tech.medmanager;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import com.example.original_tech.medmanager.R;
import com.example.original_tech.medmanager.data.MedicationContract;
import com.example.original_tech.medmanager.utils.MedicationDateUtils;
import com.example.original_tech.medmanager.utils.NotificationUtils;

public class MedicationDetailsActivity extends AppCompatActivity {

    private ImageView mMedImage;
    private TextView mMedName;
    private TextView mMedDesc;
    private TextView mMedInterval;
    private TextView mMedStartDate;
    private TextView mMedEndDate;
    private String mId;
    private Uri mMedicationWithId;
    private String uniqueId;
    private boolean initialized;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication_details);

        final Intent intent=getIntent();
        if (intent.hasExtra(Intent.EXTRA_TEXT)) {
            mId = intent.getStringExtra(Intent.EXTRA_TEXT);
            mMedicationWithId = MedicationContract.MedicationEntry.buildMedicationUri(Long.parseLong(mId));
            initialized = true;
        }else if (intent.hasExtra(NotificationUtils.UNIQUE_ID_KEY)){
            uniqueId = intent.getStringExtra(NotificationUtils.UNIQUE_ID_KEY);
            initialized = false;
        }

        displayMedDetails();
        //mMedImage = findViewById(R.id.med_image);
        mMedName = findViewById(R.id.med_name);
        mMedDesc = findViewById(R.id.med_desc);
        mMedInterval = findViewById(R.id.med_interval);
        mMedStartDate = findViewById(R.id.med_start_date);
        mMedEndDate = findViewById(R.id.med_end_date);
    }

    private void displayMedDetails(){
        String[] projection={MedicationContract.MedicationEntry._ID,
                MedicationContract.MedicationEntry.COLUMN_MED_NAME,
                MedicationContract.MedicationEntry.COLUMN_MED_DESC,
                MedicationContract.MedicationEntry.COLUMN_MED_INTERVAL,
                MedicationContract.MedicationEntry.COLUMN_START_DATE,
                MedicationContract.MedicationEntry.COLUMN_END_DATE};
        new FetchMedicationDetails().execute(projection);

    }

    class FetchMedicationDetails extends AsyncTask<String[],Void,Cursor> {

        @Override
        protected Cursor doInBackground(String[]... strings) {
            String[] projection=strings[0];
            Cursor cursor;
            if (initialized) {
                cursor = getContentResolver().query(mMedicationWithId,
                        projection,
                        null,
                        null,
                        null);
            }else {
                cursor = getContentResolver().query(MedicationContract.MedicationEntry.CONTENT_URI,
                        projection,
                        MedicationContract.MedicationEntry.UNIQUE_ID + "?",
                        new String[] {uniqueId},
                        null);
            }
            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            cursor.moveToNext();
            try {
                if (cursor != null) {
                    mMedName.setText(cursor.getString(cursor.getColumnIndex(MedicationContract.MedicationEntry.COLUMN_MED_NAME)));
                    mMedDesc.setText(cursor.getString(cursor.getColumnIndex(MedicationContract.MedicationEntry.COLUMN_MED_DESC)));
                    String interval = cursor.getInt(cursor.getColumnIndex(MedicationContract.MedicationEntry.COLUMN_MED_NAME)) + " hours";
                    mMedInterval.setText(interval);
                    String startDate = MedicationDateUtils.getFriendlyDateString(MedicationDetailsActivity.this,
                            cursor.getLong(cursor.getColumnIndex(MedicationContract.MedicationEntry.COLUMN_START_DATE)), true);
                    mMedStartDate.setText(startDate);
                    String endDate = MedicationDateUtils.getFriendlyDateString(MedicationDetailsActivity.this,
                            cursor.getLong(cursor.getColumnIndex(MedicationContract.MedicationEntry.COLUMN_END_DATE)), true);
                    mMedEndDate.setText(endDate);
                }
            }finally {
                cursor.close();
            }
        }
    }
}
