package com.example.original_tech.medmanager.data;


import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.original_tech.medmanager.MainActivity;
import com.example.original_tech.medmanager.R;
import com.example.original_tech.medmanager.adapters.MedDisplayAdapter;
import com.example.original_tech.medmanager.utils.MedDataUtils;
import com.example.original_tech.medmanager.utils.MedicationDateUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Original-Tech on 4/1/2018.
 */

public class SearchResultActivity extends AppCompatActivity implements MedDisplayAdapter.ListItemCLickListener{

    private RecyclerView mRecyclerView;
    private MedDisplayAdapter mMedDisplayAdapter;
    ArrayList<JSONObject> data;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        data = new ArrayList<>();
        mMedDisplayAdapter = new MedDisplayAdapter(data, this, this);
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mMedDisplayAdapter);

        //search
        handleIntent();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent();
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query = intent.getStringExtra(SearchManager.QUERY).trim();
            //Do some stuffs
            new queryFromBackGround().execute(query);
            Toast.makeText(this, "Something just hapened", Toast.LENGTH_SHORT).show();
        }/*else if (Intent.ACTION_VIEW.equals(intent.getAction())){
            String selectedSuggestionRowId = intent.getDataString();
            //Exexution comes here when the item is selected from the search suggestions
            Toast.makeText(this, "Selected Search Suggestion "+selectedSuggestionRowId ,
                    Toast.LENGTH_SHORT).show();
        }*/
    }

    @Override
    public void onItemCLickListener(int clickedItemIndex) {

    }

    class queryFromBackGround extends AsyncTask<String, Void, Cursor>{

        @Override
        protected Cursor doInBackground(String... strings) {
            String query = strings[0];
            /*Cursor cursor = MedDataUtils.getMedNameMatches(query,
                    null,
                    MedicationContract.MedicationEntry.COLUMN_MED_NAME,
                    getApplicationContext());*/
            Cursor cursor = MedDataUtils.search(query, getApplicationContext());
            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            int i = 1;
            cursor.moveToNext();
            JSONObject jsonObject = new JSONObject();
            while (i <= cursor.getCount()) {
                try {
                    jsonObject.put(MainActivity.KEY_NAME,
                            cursor.getString(cursor.getColumnIndex(MedicationContract.MedicationEntry.COLUMN_MED_NAME)));
                    jsonObject.put(MainActivity.KEY_DESC,
                            cursor.getString(cursor.getColumnIndex(MedicationContract.MedicationEntry.COLUMN_MED_DESC)));
                    int interval = cursor.getInt(cursor.getColumnIndex(MedicationContract.MedicationEntry.COLUMN_MED_INTERVAL));
                    String intField = MedicationDateUtils.getReadableMedInterval(interval);
                    jsonObject.put(MainActivity.KEY_INTERVAL, intField);
                    data.add(jsonObject);
                    cursor.moveToNext();
                } catch (JSONException e) {
                }
                i++;
            }
            mMedDisplayAdapter.update(data);
            Toast.makeText(getApplicationContext(), "finally "+cursor.getCount(), Toast.LENGTH_SHORT).show();
        }
    }
}
