package com.example.original_tech.medmanager.data;


import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.original_tech.medmanager.MainActivity;
import com.example.original_tech.medmanager.MedicationDetailsActivity;
import com.example.original_tech.medmanager.R;
import com.example.original_tech.medmanager.adapters.MedDisplayAdapter;
import com.example.original_tech.medmanager.utils.MedDataUtils;
import com.example.original_tech.medmanager.utils.MedicationDateUtils;
import com.example.original_tech.medmanager.utils.NotificationUtils;

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
    private TextView mEmptyView;
    public static final String KEY_NAME = "name";
    public static final String KEY_DESC = "description";
    public static final String UNIQUE_ID = "unique-id";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        mEmptyView = findViewById(R.id.empty_view);
        data = new ArrayList<>();
        mMedDisplayAdapter = new MedDisplayAdapter(data, this, this);
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mMedDisplayAdapter);

        android.support.v7.app.ActionBar actionbar=getSupportActionBar();
        if (actionbar!=null){
            actionbar.setDisplayHomeAsUpEnabled(true);
        }
        //search
        handleIntent();
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
        }
    }

    @Override
    public void onItemCLickListener(String clickedItemUniqueId) {
        Intent intent=new Intent(SearchResultActivity.this, MedicationDetailsActivity.class);
        intent.putExtra(NotificationUtils.UNIQUE_ID_KEY, clickedItemUniqueId);
        startActivity(intent);
    }


    class queryFromBackGround extends AsyncTask<String, Void, Cursor>{

        @Override
        protected Cursor doInBackground(String... strings) {
            String query = strings[0];
            Cursor cursor = MedDataUtils.search(query, getApplicationContext());
            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor == null){
                mEmptyView.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.INVISIBLE);
            }else {
                cursor.moveToNext();
                JSONObject jsonObject = new JSONObject();
                for (int i=0; i<cursor.getCount(); i++) {
                    try {
                        jsonObject.put(KEY_NAME,
                                cursor.getString(cursor.getColumnIndex(MedicationContract.MedicationEntry.COLUMN_MED_NAME)));
                        jsonObject.put(KEY_DESC,
                                cursor.getString(cursor.getColumnIndex(MedicationContract.MedicationEntry.COLUMN_MED_DESC)));
                        jsonObject.put(UNIQUE_ID,
                                cursor.getString(cursor.getColumnIndex(MedicationContract.MedicationEntry.UNIQUE_ID)));
                        int interval = cursor.getInt(cursor.getColumnIndex(MedicationContract.MedicationEntry.COLUMN_MED_INTERVAL));
                        String intField = MedicationDateUtils.getReadableMedInterval(interval);
                        jsonObject.put(MainActivity.KEY_INTERVAL, intField);
                        data.add(jsonObject);
                        cursor.moveToNext();
                    } catch (JSONException e) {
                    }
                }
                mEmptyView.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                mMedDisplayAdapter.update(data);
            }
        }
    }
}
