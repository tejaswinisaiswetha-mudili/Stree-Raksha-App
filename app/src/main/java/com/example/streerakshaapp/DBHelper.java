package com.example.streerakshaapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    public static final  String DBNAME="login.db";
    public DBHelper(@Nullable Context context) {
        super(context, "login.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table users(username TEXT primary key,password TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
       sqLiteDatabase.execSQL("drop table if exists users");
    }
    public Boolean insertData(String username,String password){
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username",username);
        values.put("password",password);

        long result=sqLiteDatabase.insert("users",null,values);
        if(result==-1) return false;
        else
            return true;
    }
    public Boolean checkusername(String username) {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from users where username=?",new String[] {username});
        if(cursor.getCount()>0)
            return true;
        else
            return false;
    }
    public Boolean checkusernamepassword(String username,String password){
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from users where username=? and password=?",new String[] {username,password});
        if(cursor.getCount()>0)
            return true;
        else
            return false;
    }

}
