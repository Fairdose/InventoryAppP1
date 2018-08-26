package com.example.st_peter_hq.inventoryappp1;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.st_peter_hq.inventoryappp1.data.BookContract.BookEntry;
import com.example.st_peter_hq.inventoryappp1.data.BookDbHelper;

import javax.xml.datatype.Duration;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int INV_LOADER = 0;
    BookDbHelper bdbHelper;
    InvCursorAdapter invCursorAdapter;
    FrameLayout emptyDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bdbHelper = new BookDbHelper(this);

        ListView invListView = findViewById(R.id.inventory_list);
        emptyDb = findViewById(R.id.empty_db_background);

        invCursorAdapter = new InvCursorAdapter(this, null);
        invListView.setAdapter(invCursorAdapter);


        //Setup the click listener
        invListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, DetailActivity.class);

                Uri currentUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);

                intent.setData(currentUri);
                startActivity(intent);
            }
        });

        //Start the loader
        getLoaderManager().initLoader(INV_LOADER, null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            //Add new book item to inventory
            case R.id.add_new_book_item:
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivity(intent);
                return true;
            //Add dummy item
            case R.id.add_dummy_item:
                addToInventory("Book of Udaciousnes", 999, 0, "Peter Black", "533-335-45-45");
                return true;
            //Dump the Table
            case R.id.delete_inventory:
                dumpTheInventory();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                BookEntry.BOOK_ID,
                BookEntry.BOOK_NAME,
                BookEntry.BOOK_PRICE,
                BookEntry.BOOK_QUANTITY,
                BookEntry.SUPPLIER,
                BookEntry.SUPPLIER_CONTACT};

        return new CursorLoader(this, BookEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        invCursorAdapter.swapCursor(data);

        if (data.getCount() > 0) {
            emptyDb.setVisibility(View.GONE);
        } else {
            emptyDb.setVisibility(View.VISIBLE);
        }

        TextView inventorySum = findViewById(R.id.current_inventory);
        inventorySum.setText(getString(R.string.inventory_item_available_string, data.getCount()));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        invCursorAdapter.swapCursor(null);
    }

    //Method for adding items and dumping the inventory

    private void addToInventory(String bName, int bPrice, int bQuantity, String bSupplier, String bContact) {

        ContentValues values = new ContentValues();
        values.put(BookEntry.BOOK_NAME, bName);
        values.put(BookEntry.BOOK_PRICE, bPrice);
        values.put(BookEntry.BOOK_QUANTITY, bQuantity);
        values.put(BookEntry.SUPPLIER, bSupplier);
        values.put(BookEntry.SUPPLIER_CONTACT, bContact);

        getContentResolver().insert(BookEntry.CONTENT_URI, values);
    }

    private void dumpTheInventory() {
        getContentResolver().delete(BookEntry.CONTENT_URI, null, null);
    }
}
