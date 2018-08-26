package com.example.st_peter_hq.inventoryappp1;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.st_peter_hq.inventoryappp1.data.BookContract.BookEntry;

public class InvCursorAdapter extends CursorAdapter {

    private int currentQuantity;

    public InvCursorAdapter(Context context, Cursor c) {

        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor data, ViewGroup parent) {
        //Inflate the list
        return LayoutInflater.from(context).inflate(R.layout.inventory_item_wrapper, parent, false);
    }

    @Override
    public void bindView(final View view, Context context, final Cursor data) {

        Button sellBook = view.findViewById(R.id.sell_item_button);
        TextView bookName = view.findViewById(R.id.inventory_item_name);
        TextView bookPrice = view.findViewById(R.id.inventory_item_price);
        TextView bookQuantity = view.findViewById(R.id.inventory_item_quantity);
        TextView bookQuantityHead = view.findViewById(R.id.inventory_item_quantity_header);
        TextView bookSupplier = view.findViewById(R.id.inventory_item_supplier_name);
        TextView bookSupContact = view.findViewById(R.id.inventory_item_supplier_contact);

        //Find the columns
        int idColumnIndex = data.getColumnIndex(BookEntry.BOOK_ID);
        int nameColumnIndex = data.getColumnIndex(BookEntry.BOOK_NAME);
        int priceColumnIndex = data.getColumnIndex(BookEntry.BOOK_PRICE);
        int qtyColumnIndex = data.getColumnIndex(BookEntry.BOOK_QUANTITY);
        int suppColumnIndex = data.getColumnIndex(BookEntry.SUPPLIER);
        int cntctColumnIndex = data.getColumnIndex(BookEntry.SUPPLIER_CONTACT);

        //Read data from the data
        final int sbookId = data.getInt(idColumnIndex);
        String sbookName = data.getString(nameColumnIndex);
        int sbookPrice = data.getInt(priceColumnIndex);
        int sbookQuantity = currentQuantity = data.getInt(qtyColumnIndex);
        String sbookSupplier = data.getString(suppColumnIndex);
        String sbookSupContact = data.getString(cntctColumnIndex);

        //Update the TextViews
        bookName.setText(sbookName);

        if (sbookPrice == 0) {
            bookPrice.setText(context.getString(R.string.ask_the_owner));
        } else {
            bookPrice.setText(context.getString(R.string.item_price_parsable_string, String.valueOf(sbookPrice)));
        }

        if (sbookQuantity == 0) {
            bookQuantityHead.setVisibility(View.GONE);
            bookQuantity.setText(context.getString(R.string.sold_out_text));
        } else {
            bookQuantityHead.setVisibility(View.VISIBLE);
            bookQuantity.setText(String.valueOf(sbookQuantity));
        }

        if (sbookSupplier.isEmpty()) {
            sbookSupplier = context.getString(R.string.anonymous_text);
        }
        bookSupplier.setText(sbookSupplier);
        bookSupContact.setText(sbookSupContact);

        sellBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sellBook(v, data.getPosition(), sbookId);
            }
        });
    }

    private void sellBook(View v, int p, int id){

        Uri currentUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);

        ContentValues values = new ContentValues();

        int quantity = currentQuantity - 1;

        if (quantity < 0) {

            Toast.makeText(v.getContext(), R.string.cant_sell_text, Toast.LENGTH_SHORT).show();

        } else {

            values.put(BookEntry.BOOK_QUANTITY, quantity);

            int rowsAffected = v.getContext().getContentResolver().update(currentUri, values, null, null);

            if (rowsAffected == 0) { Toast.makeText(v.getContext(), R.string.delete_error, Toast.LENGTH_SHORT).show(); }
        }
    }
}
