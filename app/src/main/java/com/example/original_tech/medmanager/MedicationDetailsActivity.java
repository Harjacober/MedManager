package com.example.original_tech.medmanager;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.original_tech.medmanager.data.MedicationContract;
import com.example.original_tech.medmanager.utils.MedicationDateUtils;
import com.example.original_tech.medmanager.utils.NotificationUtils;

import java.text.ParseException;

public class MedicationDetailsActivity extends AppCompatActivity {

    private ImageView mMedImage;
    private TextView mMedName;
    private TextView mMedDesc;
    private TextView mMedInterval;
    private TextView mMedStartDate;
    private TextView mMedEndDate;
    private TextView mMedDaysUsed;
    private TextView mMedDaysRemaining;
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
        Log.i("ppppppppp", initialized + "--" + uniqueId);

        displayMedDetails();
        mMedImage = findViewById(R.id.med_image);
        mMedName = findViewById(R.id.med_name);
        mMedDesc = findViewById(R.id.med_desc);
        mMedInterval = findViewById(R.id.med_interval);
        mMedStartDate = findViewById(R.id.med_start_date);
        mMedEndDate = findViewById(R.id.med_end_date);
        mMedDaysUsed = findViewById(R.id.med_days_used);
        mMedDaysRemaining = findViewById(R.id.med_days_remaining);


        android.support.v7.app.ActionBar actiobar=getSupportActionBar();
        if (actiobar!=null){
            actiobar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if (id==android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }
        else if (id==R.id.delete_medication){
            if (mMedicationWithId != null) {
                getContentResolver().delete(mMedicationWithId,
                        null,
                        null);
                finish();
            }else{
                getContentResolver().delete(MedicationContract.MedicationEntry.CONTENT_URI,
                        MedicationContract.MedicationEntry.UNIQUE_ID + "=?",
                        new String[] {uniqueId});
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayMedDetails(){
        String[] projection={MedicationContract.MedicationEntry._ID,
                MedicationContract.MedicationEntry.COLUMN_MED_NAME,
                MedicationContract.MedicationEntry.COLUMN_MED_DESC,
                MedicationContract.MedicationEntry.COLUMN_MED_INTERVAL,
                MedicationContract.MedicationEntry.COLUMN_START_DATE,
                MedicationContract.MedicationEntry.COLUMN_END_DATE,
                MedicationContract.MedicationEntry.COLUMN_IMAGE};
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
                        MedicationContract.MedicationEntry.UNIQUE_ID + "=?",
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
                    String interval = String.valueOf(cursor.getInt(cursor.getColumnIndex(MedicationContract.MedicationEntry.COLUMN_MED_INTERVAL)));
                    mMedInterval.setText(interval);
                    String startDate = MedicationDateUtils.dateInNewFormat(
                            Long.parseLong(cursor.getString(
                                    cursor.getColumnIndex(
                                            MedicationContract.MedicationEntry.COLUMN_START_DATE))));
                    mMedStartDate.setText(startDate);
                    String endDate = MedicationDateUtils.dateInNewFormat(
                            Long.parseLong(cursor.getString(
                                    cursor.getColumnIndex(
                                            MedicationContract.MedicationEntry.COLUMN_END_DATE))));
                    mMedEndDate.setText(endDate);
                    try {
                        mMedDaysUsed.setText(MedicationDateUtils.getNumOfDaysUsed(startDate)+"");
                        mMedDaysRemaining.setText(MedicationDateUtils.getNumOfDaysRemaining(endDate)+"");
                    } catch (ParseException e) {
                        Toast.makeText(MedicationDetailsActivity.this, "there was an excepton", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                    String filePath = cursor.getString(cursor.getColumnIndex(MedicationContract.MedicationEntry.COLUMN_IMAGE));
                    new decodeByte().execute(filePath);
                }
            }finally {
                cursor.close();
            }
        }
    }


    class decodeByte extends AsyncTask<String,Void,Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {
            String filePath = strings[0];
            Bitmap bImage = BitmapFactory.decodeFile(filePath);
            return bImage;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null){
                mMedImage.setImageBitmap(bitmap);
            }
        }
    }
}
