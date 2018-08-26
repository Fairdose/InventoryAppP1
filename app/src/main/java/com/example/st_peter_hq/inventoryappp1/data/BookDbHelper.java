package com.example.st_peter_hq.inventoryappp1.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.st_peter_hq.inventoryappp1.data.BookContract.BookEntry;

public class BookDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "book_store.db";
    private static final int DATABASE_VERSION = 1;

    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @SuppressLint("SQLiteString")
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Define the SQL statement
        String SQL_CREATE_INVENTORY_TABLE = "CREATE TABLE "
                + BookEntry.TABLE_NAME + " ("
                + BookEntry.BOOK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookEntry.BOOK_NAME + " STRING NOT NULL, "
                + BookEntry.BOOK_PRICE + " INTEGER NOT NULL DEFAULT 0, "
                + BookEntry.BOOK_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + BookEntry.SUPPLIER + " STRING , "
                + BookEntry.SUPPLIER_CONTACT + " STRING NOT NULL DEFAULT \"000-00-00\");";

        db.execSQL(SQL_CREATE_INVENTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int currentVersion) { }

}

