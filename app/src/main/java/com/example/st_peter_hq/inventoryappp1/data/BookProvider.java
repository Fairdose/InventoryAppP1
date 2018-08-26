package com.example.st_peter_hq.inventoryappp1.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.st_peter_hq.inventoryappp1.data.BookContract.BookEntry;

public class BookProvider extends ContentProvider {

    public static final String LOG_TAG = BookProvider.class.getSimpleName();
    private static final int INVENTORY = 600;
    private static final int INVENTORY_ITEM_ID = 601;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.INVENTORY_PATH, INVENTORY);
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.INVENTORY_PATH + "/#", INVENTORY_ITEM_ID);
    }

    private BookDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new BookDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor data;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                data = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case INVENTORY_ITEM_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                data = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("URI unresolved " + uri);
        }

        data.setNotificationUri(getContext().getContentResolver(), uri);
        return data;
    }


    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return insertItem(uri, contentValues);
            default:
                throw  new IllegalArgumentException("An error occurred " + uri);
        }
    }

    private Uri insertItem(Uri uri, ContentValues values) {

        String name = values.getAsString(BookEntry.BOOK_NAME);
        if (name == null) {
            Toast.makeText(getContext(), "Book name cannot be empty", Toast.LENGTH_SHORT).show();
            return null;
        }

        String contact = values.getAsString(BookEntry.SUPPLIER_CONTACT);
        if (contact == null) {
            Toast.makeText(getContext(), "You must enter contact information", Toast.LENGTH_SHORT).show();
            return null;
        }

        String quantity = values.getAsString(BookEntry.BOOK_QUANTITY);
        if (quantity == null) {
            Toast.makeText(getContext(), "You must enter how many?", Toast.LENGTH_SHORT).show();
            return null;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(BookEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        //Notify all listeners there is a change
        getContext().getContentResolver().notifyChange(uri, null);

        //Return the new URI
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);
        Log.v("Uri is ", String.valueOf(match));
        switch (match) {
            case INVENTORY:
                return updateItem(uri, contentValues, selection, selectionArgs);
            case INVENTORY_ITEM_ID:

                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateItem(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(BookEntry.BOOK_NAME)) {
            String name = values.getAsString(BookEntry.BOOK_NAME);
            if (name == null) { throw new IllegalArgumentException("Book name cannot be empty"); }
        }

        if (values.containsKey(BookEntry.SUPPLIER_CONTACT)) {
            String contact = values.getAsString(BookEntry.SUPPLIER_CONTACT);
            if (contact == null) {
                throw new IllegalArgumentException("You must enter contact information");
            }
        }

        if (values.containsKey(BookEntry.BOOK_QUANTITY)) {
            String quantity = values.getAsString(BookEntry.BOOK_QUANTITY);
            if (quantity == null) {
                throw new IllegalArgumentException("You must enter how many");
            }
        }

        if (values.size() == 0) { return 0; }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //Update the DB and get affected rows
        int rowsUpdated = database.update(BookEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) { getContext().getContentResolver().notifyChange(uri, null); }

        return rowsUpdated;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                //Delete all items
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case INVENTORY_ITEM_ID:
                //Delete single item
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Cannot delete " + uri);
        }

        if (rowsDeleted != 0){ getContext().getContentResolver().notifyChange(uri, null); }

        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return BookEntry.CONTENT_LIST_TYPE;
            case INVENTORY_ITEM_ID:
                return BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
