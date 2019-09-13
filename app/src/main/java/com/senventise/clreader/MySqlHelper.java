package com.senventise.clreader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class MySqlHelper extends SQLiteOpenHelper {
    private Context context;
    public MySqlHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_FAV = "create table fav( " +
                "title text," +
                "path text" +
                ");";
        String CREATE_HISTORY = "create table history( " +
                "title text," +
                "path text" +
                ");";
        sqLiteDatabase.execSQL(CREATE_FAV);
        sqLiteDatabase.execSQL(CREATE_HISTORY);
        Log.d("SQL", "成功创建");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
