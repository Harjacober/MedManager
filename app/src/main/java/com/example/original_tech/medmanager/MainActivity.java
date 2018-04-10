package com.example.original_tech.medmanager;


import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.original_tech.medmanager.adapters.MedicationDiaplayAdapter;
import com.example.original_tech.medmanager.authentication.UserProfileActivity;
import com.example.original_tech.medmanager.data.MedicationContract;

import java.sql.Timestamp;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
AdapterView.OnItemSelectedListener{

    private ProgressBar mLoadIndicator;
    private MedicationDiaplayAdapter mDisplayAdapter;
    private ListView mListView;
    private static final int PRODUCT_LOADER = 0;
    public static final String KEY_INTERVAL = "interval";
    private static final String PREF_KEY = "sort-key";
    private static final String PREF_NAME = "sort-order";
    private static final String PREF_CATEGORY = "month-category";
    private static final String PREF_MONTH = "month-key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        disableOptimization();//to be removed later

        mLoadIndicator = findViewById(R.id.progress_bar);
        TextView emptyText = findViewById(R.id.text_view);
        mDisplayAdapter = new MedicationDiaplayAdapter(this,null);
        mListView = findViewById(R.id.medication_list_view);
        mListView.setEmptyView(emptyText);
        mListView.setAdapter(mDisplayAdapter);
        getSupportLoaderManager().initLoader(PRODUCT_LOADER,null,this);

        setListViewListener();
    }

    public void onAddNewMedicationClicked(View view) {
        startAddNewMedActivity();
    }

    private void startAddNewMedActivity() {
        Intent intent = new Intent(this, AddNewMedActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        //Set up Spinner
        MenuItem item = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.monthsList, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        //Search View
        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView)
                menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.search){
            //Start search dialog
            super.onSearchRequested();
        }else if(id == R.id.view_profile){
            Intent intent = new Intent(this, UserProfileActivity.class);
            startActivity(intent);
        }else if(id == R.id.sort_by){
            createDialog();
        }else if(id == R.id.delete_all_med) {
            deleteAllMedications();
        }else if(id == R.id.add_medication) {
            startAddNewMedActivity();
        }else if(id == R.id.delete_due_med) {
            new DeleteDueMedicationFromDb().execute();
        }
        return true;
    }

    private void deleteAllMedications() {
        getContentResolver().delete(MedicationContract.MedicationEntry.CONTENT_URI,
                null,
                null);
    }

    public void setListViewListener(){
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String uId=String.valueOf(id);
                //Uri currentWorkerUri= ContentUris.withAppendedId(MedicationContract.MedicationEntry.CONTENT_URI, id);
                Intent intent=new Intent(MainActivity.this, MedicationDetailsActivity.class);
                //intent.setData(currentWorkerUri);
                intent.putExtra(Intent.EXTRA_TEXT, uId);
                startActivity(intent);
            }
        });
    }

    private void createDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(
                new ContextThemeWrapper(this,
                        R.style.DialogTheme));
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dia_sort_med_list, null);
        final RadioButton nameDescending = view.findViewById(R.id.name_desc);
        final RadioButton nameAscending = view.findViewById(R.id.name_asc);
        final RadioButton time = view.findViewById(R.id.time);
        builder.setView(view).setNeutralButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (time.isChecked()){
                    time.setChecked(true);
                    nameAscending.setChecked(false);
                    nameDescending.setChecked(false);

                    editor.putString(PREF_KEY, "time");
                    editor.commit();
                }else if (nameAscending.isChecked()){
                    time.setChecked(false);
                    nameAscending.setChecked(true);
                    nameDescending.setChecked(false);

                    editor.putString(PREF_KEY, "nameAsc");
                    editor.commit();
                }else if (nameDescending.isChecked()){
                    time.setChecked(false);
                    nameAscending.setChecked(false);
                    nameDescending.setChecked(true);

                    editor.putString(PREF_KEY, "nameDesc");
                    editor.commit();
                }
                SharedPreferences prefs = getSharedPreferences(PREF_NAME, 0);
                String order = prefs.getString(PREF_KEY, null);
                Log.i("mmmmmmmmm", order);
                getSupportLoaderManager().restartLoader(PRODUCT_LOADER,null,MainActivity.this);
            }
        });

        SharedPreferences prefs = getSharedPreferences(PREF_NAME, 0);
        String order = prefs.getString(PREF_KEY, "");
        if (order.equals("nameAsc")){
            nameAscending.setChecked(true);
        }else if (order.equals("nameDesc")){
            nameDescending.setChecked(true);
        }else{
            time.setChecked(true);
        }

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection={MedicationContract.MedicationEntry._ID,
                MedicationContract.MedicationEntry.COLUMN_MED_NAME,
                MedicationContract.MedicationEntry.COLUMN_MED_DESC,
                MedicationContract.MedicationEntry.COLUMN_MED_INTERVAL,
                MedicationContract.MedicationEntry.COLUMN_MONTH,
                MedicationContract.MedicationEntry.COLUMN_IMAGE };
        //Sort Order specified by the sort by in the overflow menu
        String sortOrder;
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, 0);
        String order = sharedPreferences.getString(PREF_KEY, "time");

        switch (order) {
            case "nameAsc":
                sortOrder = MedicationContract.MedicationEntry.COLUMN_MED_NAME + " ASC";
                break;
            case "nameDesc":
                sortOrder = MedicationContract.MedicationEntry.COLUMN_MED_NAME + " DESC";
                break;
            default:
                sortOrder = null;
                break;
        }

        //Where and where args specified by the spinner at the action bar
        String selection;
        String[] selectionArgs;
        SharedPreferences preferences = getSharedPreferences(PREF_CATEGORY, 0);
        String selectedMonth = preferences.getString(PREF_MONTH, "");
        if (selectedMonth.equals("All")){
            selection = null;
            selectionArgs = null;
        }else {
            selection = MedicationContract.MedicationEntry.COLUMN_MONTH + "=?";
            selectionArgs = new String[] {selectedMonth};
        }

        return new CursorLoader(this,
                MedicationContract.MedicationEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
//        new DeleteDueMedicationFromDb().execute(cursor);
        mDisplayAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mDisplayAdapter.swapCursor(null);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        String monthSelectedFromSpinner = String.valueOf(adapterView.getItemAtPosition(position));
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_CATEGORY, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_MONTH, monthSelectedFromSpinner);
        editor.commit();
        getSupportLoaderManager().restartLoader(PRODUCT_LOADER,null,this);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    class DeleteDueMedicationFromDb extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            long currentTime = System.currentTimeMillis();
            long endDateInMillis;
            Cursor cursor = getContentResolver().query(MedicationContract.MedicationEntry.CONTENT_URI,
                    new String[] {MedicationContract.MedicationEntry.COLUMN_END_DATE},
                    null,
                    null,
                    null);
            if (cursor != null) {
                cursor.moveToNext();
                for (int i = 0; i < cursor.getCount(); i++) {
                    endDateInMillis = Long.parseLong(cursor.getString(
                            cursor.getColumnIndex(MedicationContract.MedicationEntry.COLUMN_END_DATE)));
                    if (new Timestamp(endDateInMillis).before(new Timestamp(currentTime))
                            || new Timestamp(currentTime).equals(new Timestamp(endDateInMillis))) {
                        getApplicationContext().getContentResolver().delete(
                                MedicationContract.MedicationEntry.CONTENT_URI,
                                MedicationContract.MedicationEntry.COLUMN_END_DATE + "=?",
                                new String[]{String.valueOf(endDateInMillis)});
                    }
                    cursor.moveToNext();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void  avoid) {
            Toast.makeText(MainActivity.this, "Due Medications has been removed",
                    Toast.LENGTH_SHORT).show();
        }
    }

    void disableOptimization(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        }
    }
}
