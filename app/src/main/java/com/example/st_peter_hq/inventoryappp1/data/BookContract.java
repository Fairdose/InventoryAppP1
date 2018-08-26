package com.example.st_peter_hq.inventoryappp1.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import android.view.View;

public class BookContract {

    public static final String CONTENT_AUTHORITY = "com.example.st_peter_hq.inventoryappp1";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String INVENTORY_PATH = "books";

    public BookContract() { }

    public static final class BookEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, INVENTORY_PATH);
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + INVENTORY_PATH;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + INVENTORY_PATH;
        public final static String TABLE_NAME = "books";
        public final static String BOOK_ID = BaseColumns._ID;
        public final static String BOOK_NAME = "product_name";
        public final static String BOOK_PRICE = "price";
        public final static String BOOK_QUANTITY = "quantity";
        public final static String SUPPLIER = "supplier";
        public final static String SUPPLIER_CONTACT = "contact";
    }



    public static boolean askTheOwner(int price){
        return price == 0;
    }
}
