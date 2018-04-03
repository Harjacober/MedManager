package com.example.original_tech.medmanager.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Original-Tech on 3/30/2018.
 */

public class MedicationProvider extends ContentProvider {
    private MedicationDbHelper mMedicationDbHelper;
    private static final int MEDICATION = 100;
    private static final int MEDICATION_WITH_ID = 101;
    private static final UriMatcher surimatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher(){
        final UriMatcher matcher=new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(MedicationContract.CONTENT_AUTHORITY, MedicationContract.MedicationEntry.TABLE_NAME, MEDICATION);
        matcher.addURI(MedicationContract.CONTENT_AUTHORITY, MedicationContract.MedicationEntry.TABLE_NAME+ "/#", MEDICATION_WITH_ID);
        return matcher;
    }
    @Override
    public boolean onCreate() {
        mMedicationDbHelper = new MedicationDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortorder) {
        SQLiteDatabase sqLiteDatabase = mMedicationDbHelper.getReadableDatabase();
        int match = surimatcher.match(uri);
        Cursor cursor;
        switch (match) {
            case MEDICATION:
                cursor= sqLiteDatabase.query(MedicationContract.MedicationEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortorder);
                break;
            case MEDICATION_WITH_ID:
                selection= MedicationContract.MedicationEntry._ID+"=?";
                selectionArgs=new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor= sqLiteDatabase.query(MedicationContract.MedicationEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortorder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }
    ;
    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase sqLiteDatabase = mMedicationDbHelper.getWritableDatabase();
        Uri returnUri;
        int match = surimatcher.match(uri);
        switch (match){
            case MEDICATION: {
                long id = sqLiteDatabase.insert(MedicationContract.MedicationEntry.TABLE_NAME,
                        null,
                        contentValues);
                if (id > 0) {
                    returnUri = MedicationContract.MedicationEntry.buildMedicationUri(id);
                } else {
                    throw new SQLException("failed to insert row into: " + uri);
                }
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown Uri " + uri);
            }
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase sqLiteDatabase=mMedicationDbHelper.getWritableDatabase();
        int numdeleted;
        int match=surimatcher.match(uri);
        switch (match){
            case MEDICATION:
                numdeleted=sqLiteDatabase.delete(MedicationContract.MedicationEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '"
                        + MedicationContract.MedicationEntry.TABLE_NAME+"'");
                break;
            case MEDICATION_WITH_ID:
                numdeleted=sqLiteDatabase.delete(MedicationContract.MedicationEntry.TABLE_NAME,
                        MedicationContract.MedicationEntry._ID+"=?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))});
                sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '"
                        + MedicationContract.MedicationEntry.TABLE_NAME+"'");
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri "+uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return numdeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        SQLiteDatabase sqLiteDatabase=mMedicationDbHelper.getWritableDatabase();
        int numupdated=0;
        if (contentValues==null){
            throw new IllegalArgumentException("cannot have null content values");
        }
        int match=surimatcher.match(uri);
        switch (match){
            case MEDICATION:
                numupdated=sqLiteDatabase.update(MedicationContract.MedicationEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            case MEDICATION_WITH_ID:
                numupdated=sqLiteDatabase.update(MedicationContract.MedicationEntry.TABLE_NAME,
                        contentValues,
                        MedicationContract.MedicationEntry._ID+"=?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri "+uri);
        }
        if (numupdated>0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return numupdated;
    }
}
