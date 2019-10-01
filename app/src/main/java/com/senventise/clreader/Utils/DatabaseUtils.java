package com.senventise.clreader.Utils;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.senventise.clreader.MyApplication;
import com.senventise.clreader.MySqlHelper;

public class DatabaseUtils {

    public static void addToFav(String title, String path){
        MySqlHelper mySqlHelper = new MySqlHelper(MyApplication.getInstance(), "data.db", null, 1);
        SQLiteDatabase db =  mySqlHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        path = path.replace(MyApplication.getRootUrl(), "{root}");
        deleteExistFav(path);
        contentValues.put("title", title);
        contentValues.put("path", path);
        db.insert("fav", null, contentValues);
        Toast.makeText(MyApplication.getInstance(), "已添加", Toast.LENGTH_SHORT).show();
        db.close();
    }

    private static void deleteExistFav(String path){
        MySqlHelper mySqlHelper = new MySqlHelper(MyApplication.getInstance(), "data.db", null, 1);
        SQLiteDatabase db = mySqlHelper.getWritableDatabase();
        db.delete("fav", "path=?",new String[]{path});
        db.close();
    }

    public static void addToHistory(String title, String path){
        MySqlHelper mySqlHelper = new MySqlHelper(MyApplication.getInstance(), "data.db", null, 1);
        SQLiteDatabase db =  mySqlHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        path = path.replace(MyApplication.getRootUrl(), "{root}");
        deleteExistHistory(path);
        contentValues.put("title", title);
        contentValues.put("path", path);
        db.insert("history", null, contentValues);
        db.close();
    }

    private static void deleteExistHistory(String path){
        MySqlHelper mySqlHelper = new MySqlHelper(MyApplication.getInstance(), "data.db", null, 1);
        SQLiteDatabase db = mySqlHelper.getWritableDatabase();
        db.delete("history", "path=?",new String[]{path});
        db.close();
    }

}