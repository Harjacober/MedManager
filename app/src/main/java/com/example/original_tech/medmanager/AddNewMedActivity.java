package com.example.original_tech.medmanager;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.original_tech.medmanager.data.MedicationContract;
import com.example.original_tech.medmanager.services.MedicationReminderFirebaseJobService;
import com.example.original_tech.medmanager.utils.MedDataUtils;
import com.example.original_tech.medmanager.utils.MedicationDateUtils;
import com.example.original_tech.medmanager.utils.ReminderUtilities;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class AddNewMedActivity extends AppCompatActivity {

    private ImageView mMedImage;
    private EditText mMedName;
    private EditText mMedDesc;
    private EditText mMedInterval;
    private EditText mMedStartDate;
    private EditText mMedEndDate;
    private static final int DIALOG_START_DATE = 999;
    private static final int DIALOG_END_DATE = 768;
    private int year,month,day;
    private long mUtcStartDate;
    private long mUUtcEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_med);

        mMedImage = findViewById(R.id.med_image);
        mMedName = findViewById(R.id.med_name);
        mMedDesc = findViewById(R.id.med_desc);
        mMedInterval = findViewById(R.id.med_interval);
        mMedStartDate = findViewById(R.id.med_start_date);
        mMedEndDate = findViewById(R.id.med_end_date);

        mMedStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(DIALOG_START_DATE);
            }
        });
        mMedEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(DIALOG_END_DATE);
            }
        });

    }

    private void showDate(int year, int month, int day, EditText date) {
        if(day<10 && month<10){
            date.setText(new StringBuilder().append(year).
                    append("/0").append(month).
                    append("/0").append(day));
        } else if (month<10){
            date.setText(new StringBuilder().append(year).
                    append("/0").append(month).
                    append("/").append(day));
        } else if (day<10) {
            date.setText(new StringBuilder().append(year).
                    append("/").append(month).
                    append("/0").append(day));
        }
        else {
            date.setText(new StringBuilder().append(year).
                    append("/").append(month).
                    append("/").append(day));
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_START_DATE){
            return new DatePickerDialog(this,
                    startDateListener, year, month, day);
        }else if (id == DIALOG_END_DATE){
            return new DatePickerDialog(this,
                    endDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener startDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            //Process the date set by the user in order to add to database
            Calendar calendar = new GregorianCalendar(year, month, day);
            long localDate = calendar.getTimeInMillis();
            mUtcStartDate = MedicationDateUtils.getUTCDateFromLocal(localDate);
            //Display date in the start date editTextField
            showDate(year, month+1, day, mMedStartDate);
        }
    };

    private DatePickerDialog.OnDateSetListener endDateListener = new DatePickerDialog.OnDateSetListener(){

        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            //Process the date set by the user in order to add to database
            Calendar calendar = new GregorianCalendar(year, month, day);
            long localDate = calendar.getTimeInMillis();
            mUUtcEndDate = MedicationDateUtils.getUTCDateFromLocal(localDate);
            //Display date in the end date editTextField
            showDate(year, month+1, day, mMedEndDate);
        }
    };
    private void addMedicationToDatabase() {
        String uniqueId = MedDataUtils.generateUniqueidForEachMedication();
        String name = mMedName.getText().toString().trim();
        String desc = mMedDesc.getText().toString().trim();
        Integer interval = Integer.valueOf(mMedInterval.getText().toString().trim());
        Integer startDate = (int) mUtcStartDate;
        Integer endDate = (int) mUUtcEndDate;
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(desc) ||
                TextUtils.isEmpty(mMedInterval.getText().toString())
                || TextUtils.isEmpty(mMedStartDate.getText().toString()) ||
                TextUtils.isEmpty(mMedEndDate.getText().toString())){
            Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
        }else {

            ContentValues values = new ContentValues();
            values.put(MedicationContract.MedicationEntry.UNIQUE_ID, uniqueId);
            values.put(MedicationContract.MedicationEntry.COLUMN_MED_NAME, name);
            values.put(MedicationContract.MedicationEntry.COLUMN_MED_DESC, desc);
            values.put(MedicationContract.MedicationEntry.COLUMN_MED_INTERVAL, interval);
            values.put(MedicationContract.MedicationEntry.COLUMN_START_DATE, startDate);
            values.put(MedicationContract.MedicationEntry.COLUMN_END_DATE, endDate);
            //Fix this later to take user preferred start time
            values.put(MedicationContract.MedicationEntry.COLUMN_PREF_START_TIME, startDate);
            getContentResolver().insert(MedicationContract.MedicationEntry.CONTENT_URI,
                    values);
            Toast.makeText(this, "Medication saved."+uniqueId, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AddNewMedActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        Bundle bundle = new Bundle();
        bundle.putString("med-name", name);
        bundle.putString("med-desc", desc);
        bundle.putString("unique-id", uniqueId);

        ReminderUtilities.scheduleMedicationReminder(this, interval, bundle);
    }


    public void onFabDoneClicked(View view) {
        addMedicationToDatabase();
    }
}
