package com.example.st_peter_hq.inventoryappp1;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.st_peter_hq.inventoryappp1.data.BookContract;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_ITEM_LOADER = 0;
    protected Uri currentItemUri;
    View.OnClickListener onClickListener;
    private TextView bookId;
    private TextView bookNameEText;
    private TextView bookPriceEText;
    private TextView bookQuantityEText;
    private TextView bookSupplierEText;
    private TextView bookSupplierContactEText;

    private int currentQuantity;
    private String currentContact;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_layout);

        Intent intent = getIntent();
        currentItemUri = intent.getData();

        setTitle(getString(R.string.item_details));
        getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);

        bookId = findViewById(R.id.details_book_id_value);
        bookNameEText = findViewById(R.id.details_book_name_value);
        bookPriceEText = findViewById(R.id.details_book_price_value);
        bookQuantityEText = findViewById(R.id.details_book_quantity_value);
        bookSupplierEText = findViewById(R.id.details_book_seller_value);
        bookSupplierContactEText = findViewById(R.id.details_book_contact_value);

        FloatingActionButton editFabButton = findViewById(R.id.edit_item_fab);

        Button sellItemBtn = findViewById(R.id.details_button_1);

        Button addItemBtn = findViewById(R.id.details_button_2);

        Button callItemBtn = findViewById(R.id.details_button_3);

        onClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.edit_item_fab:

                        Intent editIntent = new Intent(DetailActivity.this, EditActivity.class);

                        editIntent.setData(currentItemUri);
                        startActivity(editIntent);
                        break;

                    case R.id.details_button_1:
                    case R.id.details_button_2:
                        commerceBook(v, currentQuantity);
                        break;

                    case R.id.details_button_3:
                        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + currentContact));
                        startActivity(callIntent);
                        break;

                    case R.id.action_delete:
                        deleteItem();
                        break;
                }
            }
        };

        editFabButton.setOnClickListener(onClickListener);
        sellItemBtn.setOnClickListener(onClickListener);
        addItemBtn.setOnClickListener(onClickListener);
        callItemBtn.setOnClickListener(onClickListener);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem menuItem = menu.findItem(R.id.action_save);
        menuItem.setVisible(false);

        return true;
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_item_prompt);
        builder.setPositiveButton(R.string.delete_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.delete_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Assign option menu actions
        switch (item.getItemId()) {

            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                BookContract.BookEntry.BOOK_ID,
                BookContract.BookEntry.BOOK_NAME,
                BookContract.BookEntry.BOOK_PRICE,
                BookContract.BookEntry.BOOK_QUANTITY,
                BookContract.BookEntry.SUPPLIER,
                BookContract.BookEntry.SUPPLIER_CONTACT};

        return new CursorLoader(this, currentItemUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data == null || data.getCount() < 1) {
            return;
        }

        if (data.moveToFirst()) {

            data.moveToFirst();

            int bIdColumnIndex = data.getColumnIndex(BookContract.BookEntry._ID);
            int bNameColumnIndex = data.getColumnIndex(BookContract.BookEntry.BOOK_NAME);
            int bPriceColumnIndex = data.getColumnIndex(BookContract.BookEntry.BOOK_PRICE);
            int bQtyColumnIndex = data.getColumnIndex(BookContract.BookEntry.BOOK_QUANTITY);
            int bSupplierColumnIndex = data.getColumnIndex(BookContract.BookEntry.SUPPLIER);
            int bContactColumnIndex = data.getColumnIndex(BookContract.BookEntry.SUPPLIER_CONTACT);

            int id = data.getInt(bIdColumnIndex);
            String name = data.getString(bNameColumnIndex);
            int price = data.getInt(bPriceColumnIndex);
            int qty = currentQuantity = data.getInt(bQtyColumnIndex);
            String supplier = data.getString(bSupplierColumnIndex);
            String contact = currentContact = data.getString(bContactColumnIndex);

            bookId.setText(getString(R.string.details_id_parsable_string, Integer.toString(id)));
            bookNameEText.setText(name);
            bookPriceEText.setText(getString(R.string.item_price_parsable_string, Integer.toString(price)));
            bookQuantityEText.setText(Integer.toString(qty));
            bookSupplierEText.setText(supplier);
            bookSupplierContactEText.setText(contact);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        bookId.setText("");
        bookNameEText.setText("");
        bookPriceEText.setText("");
        bookQuantityEText.setText("");
        bookSupplierEText.setText("");
        bookSupplierContactEText.setText("");

    }

    private void commerceBook(View v, int currentQuantity) {

        ContentValues values = new ContentValues();

        int quantity = currentQuantity;

        switch (v.getId()) {

            case R.id.details_button_1:
                quantity -= 1;
                break;

            case R.id.details_button_2:
                quantity += 1;
                break;
        }

        if (quantity < 0) {

            Toast.makeText(v.getContext(), R.string.cant_sell_text, Toast.LENGTH_SHORT).show();

        } else {

            values.put(BookContract.BookEntry.BOOK_QUANTITY, quantity);

            int rowsAffected = v.getContext().getContentResolver().update(currentItemUri, values, null, null);

            //Show a toast message if an error occurred
            if (rowsAffected == 0) {
                Toast.makeText(v.getContext(), R.string.delete_error, Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void deleteItem() {

        if (currentItemUri != null) {
            int rowsDeleted = getContentResolver().delete(currentItemUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, R.string.delete_error, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.item_delete, Toast.LENGTH_SHORT).show();
            }
        }

        finish();

        Intent returnToMain = new Intent(DetailActivity.this, MainActivity.class);
        NavUtils.navigateUpTo(this, returnToMain);
    }
}
