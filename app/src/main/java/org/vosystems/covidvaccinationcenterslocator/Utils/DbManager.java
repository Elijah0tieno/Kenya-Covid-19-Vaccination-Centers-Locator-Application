package org.vosystems.covidvaccinationcenterslocator.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbManager extends SQLiteOpenHelper {
    private static String dbName = "reminder";

    public DbManager(@Nullable Context context) {
        super(context, dbName,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //sql query to write data in sqlite
        String query = "create table tbl_reminder(id integer primary key autoincrement, title text, date text, time text)";
        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXISTS tbl_reminder";
        //sql query to check table with the name or not
        db.execSQL(query);
        onCreate(db);

    }

    public String addreminder(String title, String date, String time){
        SQLiteDatabase database = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title",title);
        //writes data to sqlite db
        contentValues.put("date",date);
        contentValues.put("time",time);

        //returns -1 if data successfully writes to db
        float result = database.insert("tbl_reminder", null, contentValues);

        if (result == -1){
            return "Sorry, couldn't write record to database";
        }else {
            return "Successfully written data to database";
        }
    }

    public Cursor readallreminders(){
        SQLiteDatabase database = this.getWritableDatabase();
        //query to retrieve data
        String query = "select * from tbl_reminder order by id desc";
        Cursor cursor = database.rawQuery(query,null);
        return cursor;
    }
}
