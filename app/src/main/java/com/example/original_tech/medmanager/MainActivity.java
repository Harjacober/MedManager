package com.example.original_tech.medmanager;


import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SearchView;
import android.widget.TextView;
import com.example.original_tech.medmanager.adapters.MedicationDiaplayAdapter;
import com.example.original_tech.medmanager.authentication.UserProfileActivity;
import com.example.original_tech.medmanager.data.MedicationContract;
import com.example.original_tech.medmanager.sync.ReminderTask;
import com.example.original_tech.medmanager.utils.NotificationUtils;
import com.example.original_tech.medmanager.utils.ReminderUtilities;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private ProgressBar mLoadIndicator;
    private MedicationDiaplayAdapter mDisplayAdapter;
    private ListView mListView;
    private static final int PRODUCT_LOADER = 0;
    public static final String KEY_NAME = "name";
    public static final String KEY_DESC = "description";
    public static final String KEY_INTERVAL = "interval";
    private static final String PREF_KEY = "sort_key";
    private static final String PREF_NAME = "sort_order";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadIndicator = findViewById(R.id.progress_bar);

        TextView emptyText = findViewById(R.id.text_view);
        mDisplayAdapter = new MedicationDiaplayAdapter(this,null);
        mListView = findViewById(R.id.medication_list_view);
        mListView.setEmptyView(emptyText);
        mListView.setAdapter(mDisplayAdapter);
        getSupportLoaderManager().initLoader(PRODUCT_LOADER,null,this);

        setListViewListener();
        ReminderUtilities.scheduleMedicationReminder(this, 4, new Bundle());
    }

    public void onAddNewMedicationClicked(View view) {
        Intent intent = new Intent(this, AddNewMedActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

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
        }
        return true;
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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
            }
        });

        SharedPreferences prefs = getSharedPreferences(PREF_NAME, 0);
        String order = prefs.getString(PREF_KEY, null);
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
                MedicationContract.MedicationEntry.COLUMN_MED_INTERVAL};
        String sortOrder;
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, 0);
        String order = sharedPreferences.getString(PREF_KEY, "time");
        Log.i("lllllllllll", order);
        if (order.equals("nameAsc")){
            sortOrder = MedicationContract.MedicationEntry.COLUMN_MED_NAME + " ASC";
        }else if (order.equals("nameDesc")){
            sortOrder = MedicationContract.MedicationEntry.COLUMN_MED_NAME + " DESC";
        }else{
            sortOrder = null;
        }
        return new CursorLoader(this,
                MedicationContract.MedicationEntry.CONTENT_URI,
                projection,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mDisplayAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mDisplayAdapter.swapCursor(null);
    }

}
