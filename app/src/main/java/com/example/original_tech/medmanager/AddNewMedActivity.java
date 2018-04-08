package com.example.original_tech.medmanager;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.evernote.android.job.JobManager;
import com.evernote.android.job.util.support.PersistableBundleCompat;
import com.example.original_tech.medmanager.authentication.SignInActivity;
import com.example.original_tech.medmanager.data.MedicationContract;
import com.example.original_tech.medmanager.reminder.DemoJobcreator;
import com.example.original_tech.medmanager.reminder.ShowNotificationJob;
import com.example.original_tech.medmanager.services.MedicationReminderFirebaseJobService;
import com.example.original_tech.medmanager.utils.BitmapUtils;
import com.example.original_tech.medmanager.utils.MedDataUtils;
import com.example.original_tech.medmanager.utils.MedicationDateUtils;
import com.example.original_tech.medmanager.utils.ReminderUtilities;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class AddNewMedActivity extends AppCompatActivity {

    private ImageView mMedImage;
    private EditText mMedName;
    private EditText mMedDesc;
    private EditText mMedInterval;
    private EditText mMedStartDate;
    private EditText mMedEndDate;
    private EditText mPrefferedStartTime;
    private static final int DIALOG_START_DATE = 999;
    private static final int DIALOG_END_DATE = 768;
    private int year,month,day;
    private long mUtcStartDate;
    private long mUUtcEndDate;
    private static final int SELECT_PICTURE = 100;
    private static final int REQUEST_CODE = 43;
    public static final String TIME_IN_MILLIS = "time-in-millis";
    public static final String FIRST_MED_CREATED = "first-med-created";
    private boolean created;
    long mTimeFromPickerInMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_med);

        //create Job Manager Instance
        JobManager.create(this).addJobCreator(new DemoJobcreator());
        JobManager.instance().getConfig().setAllowSmallerIntervalsForMarshmallow(true);
        //request permission if permission not granted
        requestAllPermission();
        //Find all views by id
        mMedImage = findViewById(R.id.med_image);
        mMedName = findViewById(R.id.med_name);
        mMedDesc = findViewById(R.id.med_desc);
        mMedInterval = findViewById(R.id.med_interval);
        mMedStartDate = findViewById(R.id.med_start_date);
        mMedEndDate = findViewById(R.id.med_end_date);
        mPrefferedStartTime= findViewById(R.id.med_pref_time);

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

        FloatingActionButton fabChoose= findViewById(R.id.image_chooser);
        fabChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(Intent.ACTION_GET_CONTENT);
                intent1.setType("image/*");
                startActivityForResult(Intent.createChooser(intent1,"Choose Image"),SELECT_PICTURE);
            }
        });

        android.support.v7.app.ActionBar actiobar=getSupportActionBar();
        if (actiobar!=null){
            actiobar.setDisplayHomeAsUpEnabled(true);
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode==RESULT_OK){
            if (requestCode==SELECT_PICTURE){
                Uri selectedImageUri=data.getData();
                if (selectedImageUri!=null){
                    Bitmap bitmap=null;
                    try {
                        bitmap = BitmapUtils.getThumbnail(selectedImageUri, this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mMedImage.setImageBitmap(bitmap);
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if (id==android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_START_DATE){
            return new DatePickerDialog(this,
                    R.style.DialogTheme,
                    startDateListener, year, month, day);
        }else if (id == DIALOG_END_DATE){
            return new DatePickerDialog(this,
                    R.style.DialogTheme,
                    endDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener startDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            //Process the date set by the user in order to add to database
            Calendar calendar = new GregorianCalendar(year, month, day);
            mUtcStartDate = calendar.getTimeInMillis() + mTimeFromPickerInMillis;
            //Display date in the start date editTextField
            MedicationDateUtils.showDate(year, month+1, day, mMedStartDate);
        }
    };

    private DatePickerDialog.OnDateSetListener endDateListener = new DatePickerDialog.OnDateSetListener(){

        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            long endTimeInMillis = (((23 * 60) + 59) * 60) * 1000;
            //Process the date set by the user in order to add to database
            Calendar calendar = new GregorianCalendar(year, month, day);
            mUUtcEndDate = calendar.getTimeInMillis() + endTimeInMillis;
            //Display date in the end date editTextField
            MedicationDateUtils.showDate(year, month+1, day, mMedEndDate);
        }
    };
    private void addMedicationToDatabase() {
        Timestamp startDateTimeStamp = new Timestamp(mUtcStartDate);
        Timestamp endDateTimeStamp = new Timestamp(mUUtcEndDate);
        String uniqueId = MedDataUtils.generateUniqueidForEachMedication();
        String name = mMedName.getText().toString().trim();
        String desc = mMedDesc.getText().toString().trim();
        Integer interval = Integer.valueOf(mMedInterval.getText().toString().trim());
        //Medication Start Date
        String startDate = String.valueOf( mUtcStartDate);
        //Medication Emd Date
        String endDate = String.valueOf( mUUtcEndDate);
        //month Medication was added
        String month = MedicationDateUtils.getMonthFromTimeInMillis(mUtcStartDate);
        //Path to save medication image
        String filePath = BitmapUtils.saveImageToSDCard(BitmapUtils.getIntentimage(mMedImage));
        //Check user input
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(desc) ||
                TextUtils.isEmpty(mMedInterval.getText().toString())
                || TextUtils.isEmpty(mMedStartDate.getText().toString()) ||
                TextUtils.isEmpty(mMedEndDate.getText().toString())){
            Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();

        }else if(startDateTimeStamp.after(endDateTimeStamp)
        || startDateTimeStamp.equals(endDateTimeStamp)){
            Toast.makeText(this, "start date must be less than end date", Toast.LENGTH_SHORT).show();
        }else if (endDateTimeStamp.before(new Timestamp(System.currentTimeMillis()))){
            Toast.makeText(this, "End date must be after today", Toast.LENGTH_SHORT).show();
        }else if (startDateTimeStamp.before(new Timestamp(System.currentTimeMillis()))){
            Toast.makeText(this, "Start date must be after today", Toast.LENGTH_SHORT).show();
        }
        else {

            //Create Content values
            ContentValues values = new ContentValues();
            values.put(MedicationContract.MedicationEntry.UNIQUE_ID, uniqueId);
            values.put(MedicationContract.MedicationEntry.COLUMN_MED_NAME, name);
            values.put(MedicationContract.MedicationEntry.COLUMN_MED_DESC, desc);
            values.put(MedicationContract.MedicationEntry.COLUMN_MED_INTERVAL, interval);
            values.put(MedicationContract.MedicationEntry.COLUMN_START_DATE, startDate);
            values.put(MedicationContract.MedicationEntry.COLUMN_END_DATE, endDate);
            values.put(MedicationContract.MedicationEntry.COLUMN_MONTH, month);
            values.put(MedicationContract.MedicationEntry.COLUMN_IMAGE, filePath);
            //Fix this later to take user preferred start time
            values.put(MedicationContract.MedicationEntry.COLUMN_PREF_START_TIME, startDate);
            getContentResolver().insert(MedicationContract.MedicationEntry.CONTENT_URI,
                    values);
            Toast.makeText(this, "Medication saved.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AddNewMedActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
       //Remind User about medication
        long duration = mUtcStartDate - System.currentTimeMillis();
        PersistableBundleCompat bundleCompat = new PersistableBundleCompat();
        bundleCompat.putString("med-name", name);
        bundleCompat.putString("med-desc", desc);
        bundleCompat.putString("unique-id", uniqueId);
        ShowNotificationJob.schedulePeriodic(duration, bundleCompat);
    }


    public void onFabDoneClicked(View view) {
        getFirstMedCreatedTime();
        addMedicationToDatabase();
    }

    //Get the time the very first medication was created
    private void getFirstMedCreatedTime() {
        if (created) return;
        long timeInMills = System.currentTimeMillis();
        SharedPreferences preferences = getSharedPreferences(FIRST_MED_CREATED, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(TIME_IN_MILLIS, timeInMills);
        editor.apply();
        created = true;
    }

    public void requestAllPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            }else {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE){
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            }
        }else{
            Toast.makeText(this, "All Permission needs to be granted", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void onPrefTimeClicked(View view) {
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);
        TimePickerDialog timepicker = new TimePickerDialog(new ContextThemeWrapper(
                this, R.style.DialogTheme),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourHere, int minuteHere) {
                        mPrefferedStartTime.setText(hourHere + ":" + minuteHere);
                        mTimeFromPickerInMillis = (((hourHere * 60) + minuteHere) * 60) * 1000;
                    }
                }, hour, minute, true);
        timepicker.setTitle("preferred Start Time");
        timepicker.show();
    }
}
