package com.example.nanohana.storeapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * Created by nanohana on 2018/5/29.
 */

public class BookDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG=BookDbHelper.class.getSimpleName();
    private static final  String DB_NAME="bookstore.db";
    private static final int DB_VERSION=1;
    public BookDbHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_BOOK_TABLE =  "CREATE TABLE " + ProductionContract.ProductionEntry.TABLE_NAME + " ("
                + ProductionContract.ProductionEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductionContract.ProductionEntry.BOOKNAME+ " TEXT NOT NULL, "
                + ProductionContract.ProductionEntry.COMPANY + " TEXT NOT NULL, "
                + ProductionContract.ProductionEntry.COMPANY_PHONE + " TEXT NOT NULL, "
                + ProductionContract.ProductionEntry.COMPANY_EMAIL+" TEXT, "
                + ProductionContract.ProductionEntry.PD_PRICE + " INTEGER NOT NULL, "
                + ProductionContract.ProductionEntry.PD_number+ " INTEGER NOT NULL DEFAULT 0);";
        sqLiteDatabase.execSQL(SQL_CREATE_BOOK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
//nothing to do
    }
}
