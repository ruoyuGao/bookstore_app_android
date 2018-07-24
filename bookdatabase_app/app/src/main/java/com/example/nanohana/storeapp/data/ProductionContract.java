package com.example.nanohana.storeapp.data;

/**
 * Created by nanohana on 2018/5/29.
 */

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
public final class ProductionContract {
    public static final String CONTENT_AUTHORITY = "com.example.nanohana.storeapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_BOOKS = "storeapp";
    private ProductionContract(){}

    public static final class ProductionEntry implements BaseColumns{
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        public final static String _ID = BaseColumns._ID;
        public final  static String TABLE_NAME="storeapp";
        public final  static String COMPANY="company";
        public final static String BOOKNAME="bookname";
        public final static String COMPANY_PHONE="company_phone_number";
        public final static String COMPANY_EMAIL="company_email_address";
        public final static String PD_PRICE="price";
        public final static String PD_number="number";

    }
}
