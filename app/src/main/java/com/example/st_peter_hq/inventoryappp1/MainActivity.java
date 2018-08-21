package com.example.st_peter_hq.inventoryappp1;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.st_peter_hq.inventoryappp1.data.BookContract.BookEntry;
import com.example.st_peter_hq.inventoryappp1.data.BookDbHelper;

public class MainActivity extends AppCompatActivity {

    private BookDbHelper bdbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bdbHelper = new BookDbHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDb();
    }

    private void displayDb() {
        //Open DB if exist
        SQLiteDatabase db = bdbHelper.getReadableDatabase();

        String[] projection = {
                BookEntry._ID,
                BookEntry.BOOK_NAME,
                BookEntry.BOOK_PRICE,
                BookEntry.BOOK_QUANTITY,
                BookEntry.SUPPLIER,
                BookEntry.SUPPLIER_CONTACT};

        //Query the the table
        Cursor cursor = db.query(BookEntry.TABLE_NAME, projection, null, null, null, null, null);

        TextView displayView = findViewById(R.id.current_inventory);
        TextView columnsView = findViewById(R.id.columns_element);
        TextView inventoryView = findViewById(R.id.current_element);

        try {

            //Display the query
            displayView.setText(getString(R.string.inventory_item_available_string, cursor.getCount()));

            columnsView.append(
                    BookEntry._ID + " - " +
                            BookEntry.BOOK_NAME + " - " +
                            BookEntry.BOOK_PRICE + " - " +
                            BookEntry.BOOK_QUANTITY + " - " +
                            BookEntry.SUPPLIER + " - " +
                            BookEntry.SUPPLIER_CONTACT + "\n");

            int idIndex = cursor.getColumnIndex(BookEntry._ID);
            int productNameIndex = cursor.getColumnIndex(BookEntry.BOOK_NAME);
            int priceIndex = cursor.getColumnIndex(BookEntry.BOOK_PRICE);
            int quantityIndex = cursor.getColumnIndex(BookEntry.BOOK_QUANTITY);
            int supplierIndex = cursor.getColumnIndex(BookEntry.SUPPLIER);
            int supplierContactIndex = cursor.getColumnIndex(BookEntry.SUPPLIER_CONTACT);

            //Iterate throughout the cursor
            while (cursor.moveToNext()) {
                //Extract the values
                int currentID = cursor.getInt(idIndex);
                String currentName = cursor.getString(productNameIndex);
                int currentPrice = cursor.getInt(priceIndex);
                int currentQuantity = cursor.getInt(quantityIndex);
                String currentSupplier = cursor.getString(supplierIndex);
                String currentContact = cursor.getString(supplierContactIndex);

                inventoryView.append((currentID + " - " + currentName + " - " + currentPrice + " - " + currentQuantity + " - " + currentSupplier + " - " + currentContact + "\n"));
            }

        } finally {
            //Close the cursor
            cursor.close();
        }
    }

    private void addToInventory(String bName, int bPrice, int bQuantity, String bSupplier, String bContact) {

        //Gets the database in write mode
        SQLiteDatabase db = bdbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BookEntry.BOOK_NAME, bName);
        values.put(BookEntry.BOOK_PRICE, bPrice);
        values.put(BookEntry.BOOK_QUANTITY, bQuantity);
        values.put(BookEntry.SUPPLIER, bSupplier);
        values.put(BookEntry.SUPPLIER_CONTACT, bContact);

        long newRowId = db.insert(BookEntry.TABLE_NAME, null, values);
    }
}
