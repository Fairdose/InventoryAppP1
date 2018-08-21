package com.example.st_peter_hq.inventoryappp1.data;

import android.provider.BaseColumns;

public class BookContract {

    public BookContract() { }

    public static final class BookEntry implements BaseColumns {
        public final static String TABLE_NAME = "books";
        public final static String BOOK_ID = BaseColumns._ID;
        public final static String BOOK_NAME = "product_name";
        public final static String BOOK_PRICE = "price";
        public final static String BOOK_QUANTITY = "quantity";
        public final static String SUPPLIER = "supplier";
        public final static String SUPPLIER_CONTACT = "contact";
    }
}
