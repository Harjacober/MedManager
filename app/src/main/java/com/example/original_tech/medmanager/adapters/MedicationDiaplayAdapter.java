package com.example.original_tech.medmanager.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.original_tech.medmanager.R;
import com.example.original_tech.medmanager.data.MedicationContract;
import com.example.original_tech.medmanager.utils.MedicationDateUtils;

/**
 * Created by Original-Tech on 4/1/2018.
 */

public class MedicationDiaplayAdapter extends CursorAdapter {
    public MedicationDiaplayAdapter(Context context, Cursor c) {
        super(context, c,0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.item_medication_list_recyclerview, viewGroup,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView medName= view.findViewById(R.id.med_name);
        TextView medDesc= view.findViewById(R.id.med_description);
        TextView medInterval = view.findViewById(R.id.med_interval);

        medName.setText(cursor.getString(cursor.getColumnIndex(MedicationContract.MedicationEntry.COLUMN_MED_NAME)));
        medDesc.setText(cursor.getString(cursor.getColumnIndex(MedicationContract.MedicationEntry.COLUMN_MED_DESC)));
        int interval = cursor.getInt(cursor.getColumnIndex(MedicationContract.MedicationEntry.COLUMN_MED_INTERVAL));
        String intField = MedicationDateUtils.getReadableMedInterval(interval);
        medInterval.setText(intField);
    }
}
