package com.example.st_peter_hq.inventoryappp1;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.st_peter_hq.inventoryappp1.data.BookContract.BookEntry;

public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_ITEM_LOADER = 0;
    protected Uri currentItemUri;
    private EditText bookNameEText;
    private EditText bookPriceEText;
    private EditText bookQuantityEText;
    private EditText bookSupplierEText;
    private EditText bookSupplierContactEText;

    private boolean editOccurred = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            editOccurred = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_layout);

        Intent intent = getIntent();
        currentItemUri = intent.getData();

        if (currentItemUri == null) {
            setTitle(getString(R.string.edit_activity_add));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.edit_activity_edit));
            getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
        }

        //Get information from the views and adjust it accordingly
        bookNameEText = findViewById(R.id.new_book_name);
        bookPriceEText = findViewById(R.id.new_book_price);
        bookQuantityEText = findViewById(R.id.new_book_quantity);
        bookSupplierEText = findViewById(R.id.new_book_supplier);
        bookSupplierContactEText = findViewById(R.id.new_book_contact);

        bookNameEText.setOnTouchListener(mTouchListener);
        bookPriceEText.setOnTouchListener(mTouchListener);
        bookQuantityEText.setOnTouchListener(mTouchListener);
        bookSupplierEText.setOnTouchListener(mTouchListener);
        bookSupplierContactEText.setOnTouchListener(mTouchListener);

    }

    private boolean saveItem() {

        String nameString = bookNameEText.getText().toString().trim();
        String priceInt = bookPriceEText.getText().toString();
        String quantityInt = bookQuantityEText.getText().toString();
        String supplierString = bookSupplierEText.getText().toString().trim();
        String supplierContactString = bookSupplierContactEText.getText().toString();

        if (currentItemUri == null &&
                TextUtils.isEmpty(nameString) ||
                TextUtils.isEmpty(supplierContactString)) {
            Toast.makeText(getApplicationContext(), "Please enter the values", Toast.LENGTH_SHORT).show();
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(BookEntry.BOOK_NAME, nameString);
        if(TextUtils.isEmpty(priceInt)){
            priceInt = "0";
        }
        values.put(BookEntry.BOOK_PRICE, priceInt);
        values.put(BookEntry.SUPPLIER_CONTACT, supplierContactString);
        if (TextUtils.isEmpty(quantityInt)){
            quantityInt = "0";
        }
        values.put(BookEntry.BOOK_QUANTITY, quantityInt);

        String supplier;
        if (TextUtils.isEmpty(supplierString)) {
            supplier = "Anonymous";
        } else {
            supplier = supplierString;
        }
        values.put(BookEntry.SUPPLIER, supplier);

        if (currentItemUri == null) {

            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

            //Implement the toast according to conditions
            if (newUri == null) {
                //If the uri is empty or and error occurred
                Toast.makeText(this, getString(R.string.edit_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.edit_success),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(currentItemUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.edit_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.update_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (currentItemUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Assign option menu actions
        switch (item.getItemId()) {

            case R.id.action_save:
                if (saveItem())
                    finish();
                return true;

            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;

            case android.R.id.home:
                //If there was no change move on
                if (!editOccurred) {
                    finish();
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        };

                //If there is a change prompt the user
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (!editOccurred) {
            super.onBackPressed();
            return;
        }

        //Setup a prompt screen and assign onClickListener
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        //Prompt the user of unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
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

        return new CursorLoader(this, currentItemUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data == null || data.getCount() < 1) {
            return;
        }

        if (data.moveToFirst()) {

            int bNameColumnIndex = data.getColumnIndex(BookEntry.BOOK_NAME);
            int bPriceColumnIndex = data.getColumnIndex(BookEntry.BOOK_PRICE);
            int bQtyColumnIndex = data.getColumnIndex(BookEntry.BOOK_QUANTITY);
            int bSupplierColumnIndex = data.getColumnIndex(BookEntry.SUPPLIER);
            int bContactColumnIndex = data.getColumnIndex(BookEntry.SUPPLIER_CONTACT);

            String name = data.getString(bNameColumnIndex);
            int price = data.getInt(bPriceColumnIndex);
            int qty = data.getInt(bQtyColumnIndex);
            String supplier = data.getString(bSupplierColumnIndex);
            String contact = data.getString(bContactColumnIndex);

            bookNameEText.setText(name);
            bookPriceEText.setText(Integer.toString(price));
            bookQuantityEText.setText(Integer.toString(qty));
            bookSupplierEText.setText(supplier);
            bookSupplierContactEText.setText(contact);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        bookNameEText.setText("");
        bookPriceEText.setText("");
        bookQuantityEText.setText("");
        bookSupplierEText.setText("");
        bookSupplierContactEText.setText("");
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.edit_add_unsaved_items);
        builder.setPositiveButton(R.string.edit_ok, discardButtonClickListener);
        builder.setNegativeButton(R.string.edit_keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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

        Intent returnToMain = new Intent(this, MainActivity.class);
        startActivity(returnToMain);
    }
}
