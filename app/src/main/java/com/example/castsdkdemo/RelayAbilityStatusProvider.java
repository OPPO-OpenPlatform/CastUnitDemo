package com.example.castsdkdemo;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.example.castsdkdemo.Config.CP_AUTHORITY;
import static com.example.castsdkdemo.Config.DB_NAME;
import static com.example.castsdkdemo.Config.TABLE_NAME;

/**
 * Ability content provider.
 */
public class RelayAbilityStatusProvider extends ContentProvider {
    private static final String TAG = "RelayAbilityStatusProvider";
    public static final int CPABILITY_ITEM = 0;
    public static final int CPABILITY_DIR = 1;
    private RelayAbilityDatabaseHelper dbHelper = null;
    private static UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(CP_AUTHORITY, "cpability", CPABILITY_DIR);
        uriMatcher.addURI(CP_AUTHORITY, "cpability/#", CPABILITY_ITEM);
    }

    public RelayAbilityStatusProvider() {
    }

    @Override
    public boolean onCreate() {
        dbHelper = new RelayAbilityDatabaseHelper(getContext(), DB_NAME, null, 1);
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Log.i(TAG, "query() uri: " + uri + "  matchUri: " + uriMatcher.match(uri));
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        switch (uriMatcher.match(uri)) {
            case CPABILITY_DIR:
                cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CPABILITY_ITEM:
                String cpabilityId = uri.getPathSegments().get(1);
                cursor = db.query(TABLE_NAME, projection, "id = ?", new String[] {cpabilityId}, null, null, sortOrder);
                break;
            default:
                break;
        }
        return cursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        Log.i(TAG, "getType() not implemented yet");
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        Log.i(TAG, "insert() not implemented yet");
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.i(TAG, "delete() not implemented yet");
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.i(TAG, "update() not implemented yet");
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
