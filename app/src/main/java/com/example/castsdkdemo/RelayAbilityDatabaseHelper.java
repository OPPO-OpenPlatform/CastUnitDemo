package com.example.castsdkdemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import static com.example.castsdkdemo.Config.RELAY_STATUS_KEY;
import static com.example.castsdkdemo.Config.TABLE_NAME;

/**
 * Ability database open helper.
 */
public class RelayAbilityDatabaseHelper extends SQLiteOpenHelper {
    public static final String CREATE_RELAY_ABILITY = "create table " + TABLE_NAME + " ("
            + "id integer primary key autoincrement, "
            + RELAY_STATUS_KEY + " integer)";
    private static final String TAG = "RelayAbilityDatabaseHelper";
    private Context mContext;

    public RelayAbilityDatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_RELAY_ABILITY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
