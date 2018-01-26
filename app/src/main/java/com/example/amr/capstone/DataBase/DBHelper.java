package com.example.amr.capstone.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Favourite";

    public static final String TableName = "FavouriteTable";
    public static final String Table_ID = "id";
    public static final String Book_ID = "idd";
    public static final String Book_Image = "imageposter";
    public static final String Book_Image2 = "imageback";
    public static final String Book_Title = "title";
    public static final String Book_SubTitle = "subtitle";
    public static final String Book_Rate = "rate";
    public static final String Book_Year = "year";
    public static final String Book_publisher = "publisher";
    public static final String Book_Overview = "overview";
    Context context;

    public static final String CREATE_TABLE_Movie = "create table " + TableName +
            "( " + Table_ID + " integer primary key autoincrement ," +
            Book_ID + " text ," + Book_Image + " text , " +
            Book_Image2 + " text ," +
            Book_Title + " text , " + Book_SubTitle + " text , " + Book_Rate + " text , " +
            Book_Year + " text , " + Book_publisher + " text , " + Book_Overview + " text ) ;";

    public static final int DATABASE_VERSION = 1;
    SQLiteDatabase mSQLiteDatabase;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_Movie);
    }

    public void addBook(String _Book_ID, String _Book_Image, String _Book_Image2, String _Book_Title, String _Book_SubTitle, Double _Book_Rate, String _Book_Year, String _Book_Overview, String Publisher) {
//        mSQLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Book_ID, _Book_ID);
        contentValues.put(Book_Image, _Book_Image);
        contentValues.put(Book_Image2, _Book_Image2);
        contentValues.put(Book_Title, _Book_Title);
        contentValues.put(Book_SubTitle, _Book_SubTitle);
        contentValues.put(Book_Rate, _Book_Rate);
        contentValues.put(Book_Year, _Book_Year);
        contentValues.put(Book_publisher, Publisher);
        contentValues.put(Book_Overview, _Book_Overview);
//        mSQLiteDatabase.insert(TableName, null, contentValues);
        context.getContentResolver().insert(BookProvider.Book_CONTENT_URI, contentValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TableName);
        onCreate(db);
    }

    public Cursor checkmovie(String s) {
        mSQLiteDatabase = getReadableDatabase();
        return mSQLiteDatabase.rawQuery("select * from " + TableName + " where " + Book_ID + " = " + "'" + s + "'", null);
    }

    public void deleterow(String ID) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(TableName, Book_ID + " = ?", new String[]{ID});
    }
}