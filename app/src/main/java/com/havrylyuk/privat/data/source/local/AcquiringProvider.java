
package com.havrylyuk.privat.data.source.local;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import com.havrylyuk.privat.data.source.local.AcquiringContract.AcquiringEntry;
import com.havrylyuk.privat.data.source.local.AcquiringContract.LocationEntry;
import android.support.annotation.NonNull;


/**
 *
 * Created by Igor Havrylyuk on 25.01.2017.
 */

public class AcquiringProvider extends ContentProvider {


    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private AcquiringDBHelper openHelper;

    static final int ACQUIRING = 100;
    static final int ACQUIRING_WITH_ID = 101;
    static final int LOCATIONS = 102;
    static final int LOCATIONS_WITH_ID = 103;


    private static final SQLiteQueryBuilder sAqcByIdQueryBuilder;

    static {
        sAqcByIdQueryBuilder = new SQLiteQueryBuilder();
        sAqcByIdQueryBuilder.setTables(AcquiringEntry.TABLE_NAME
        );
    }



    private static final String aqcTsoByIdSelection = AcquiringEntry.TABLE_NAME + "." + AcquiringEntry._ID + " = ? ";

    private Cursor getAcquiringsById(Uri uri, String[] projection, String sortOrder) {
        String selectionId = String.valueOf(AcquiringEntry.getIdFromUri(uri));
        String selection = aqcTsoByIdSelection;
        String[] selectionArgs = new String[]{selectionId};
        return sAqcByIdQueryBuilder.query(openHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = AcquiringContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, AcquiringContract.PATH_ACQUIRING, ACQUIRING);
        matcher.addURI(authority, AcquiringContract.PATH_ACQUIRING +"/#", ACQUIRING_WITH_ID);
        matcher.addURI(authority, AcquiringContract.PATH_LOCATION, LOCATIONS);
        matcher.addURI(authority, AcquiringContract.PATH_LOCATION +"/#", LOCATIONS_WITH_ID);
        return matcher;
    }

    public AcquiringProvider() {

    }

    @Override
    public boolean onCreate() {
        openHelper = new AcquiringDBHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ACQUIRING:
                return AcquiringEntry.CONTENT_TYPE;
            case ACQUIRING_WITH_ID:
                return AcquiringEntry.CONTENT_ITEM_TYPE;
            case LOCATIONS:
                return LocationEntry.CONTENT_TYPE;
            case LOCATIONS_WITH_ID:
                return AcquiringEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case ACQUIRING:
                rowsDeleted = db.delete(
                        AcquiringEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case LOCATIONS:
                rowsDeleted = db.delete(
                        LocationEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case ACQUIRING: {
                long _id = db.insert(AcquiringEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = AcquiringEntry.buildAcquiringUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case LOCATIONS: {
                long _id = db.insert(LocationEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = LocationEntry.buildLocationUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "acquiring"
            case ACQUIRING: {
                retCursor = openHelper.getReadableDatabase().query(
                        AcquiringEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "acquiring/*"
            case ACQUIRING_WITH_ID: {
                retCursor = getAcquiringsById(uri, projection, sortOrder);
                break;
            }
            // "location"
            case LOCATIONS: {
                retCursor = openHelper.getReadableDatabase().query(
                        LocationEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;
        switch (match) {
            case ACQUIRING:
                rowsUpdated = db.update(AcquiringEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case LOCATIONS:
                rowsUpdated = db.update(LocationEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {
            case ACQUIRING:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(AcquiringEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
