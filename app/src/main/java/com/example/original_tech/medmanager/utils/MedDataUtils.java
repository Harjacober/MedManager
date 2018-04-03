package com.example.original_tech.medmanager.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.original_tech.medmanager.data.MedicationContract;
import com.example.original_tech.medmanager.data.MedicationDbHelper;

import java.util.Random;

/**
 * Created by Original-Tech on 4/1/2018.
 */

public class MedDataUtils {

    public static Cursor getMedNameMatches(String query, String[] projections,
                                           String columnName, Context context){
        String selection = columnName + " MATCH ?";
        String[] selectionArgs = new String[] {query+"*"};
        return query(selection, selectionArgs, projections, context);
    }

    private static Cursor query(String selection, String[] selectionArgs,
                                String[] projections, Context context){
        MedicationDbHelper medicationDbHelper = new MedicationDbHelper(context);
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(MedicationContract.MedicationEntry.TABLE_NAME);

        Cursor cursor = builder.query(medicationDbHelper.getReadableDatabase(),
                projections,
                selection,
                selectionArgs,
                null,
                null,
                null);
        if (cursor == null){
            return null;
        }else if (!cursor.moveToFirst()){
            cursor.close();
            return null;
        }
        return cursor;
    }

    public static Cursor search(String query, Context context){
        query = "%" + query + "%";
        String selection = MedicationContract.MedicationEntry.COLUMN_MED_NAME + " LIKE ?";
        String[] selectionArgs = new String[] {query};
        Cursor cursor;
        cursor = context.getContentResolver().query(MedicationContract.MedicationEntry.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null);

        return cursor;
    }

    public static String generateUniqueidForEachMedication(){
        String strings = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder uniqueId = new StringBuilder();
        Random random = new Random();
        while (uniqueId.length() <= 30) {
            int num = (int) (random.nextFloat()*uniqueId.length());
            uniqueId.append(strings.charAt(num));
        }
        return uniqueId.toString();
    }
}
