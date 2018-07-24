package com.example.nanohana.storeapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.nanohana.storeapp.EditorActivity;
import com.example.nanohana.storeapp.MainActivity;

/**
 * Created by nanohana on 2018/5/31.
 */

public class bookProvider extends ContentProvider {
    public static final String LOG_TAG = bookProvider.class.getSimpleName();
    private static final int BOOKS = 100;
    private static final int BOOK_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(ProductionContract.CONTENT_AUTHORITY,ProductionContract.PATH_BOOKS, BOOKS);
        sUriMatcher.addURI(ProductionContract.CONTENT_AUTHORITY, ProductionContract.PATH_BOOKS+ "/#", BOOK_ID);
    }

    public  static BookDbHelper mDbHelper;
    @Override
    public boolean onCreate() {
        mDbHelper = new BookDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                cursor = database.query(ProductionContract.ProductionEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BOOK_ID:
                selection = ProductionContract.ProductionEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                cursor = database.query(ProductionContract.ProductionEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType( Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return ProductionContract.ProductionEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return ProductionContract.ProductionEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertbook(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertbook(Uri uri, ContentValues values) {
        // Check that the bookname and other string value is not null
        try{
            String bookname = values.getAsString(ProductionContract.ProductionEntry.BOOKNAME);
            if (bookname.length()==0) {
                throw new IllegalArgumentException("book requires a name");
            }
            String companyname = values.getAsString(ProductionContract.ProductionEntry.COMPANY);
            if (bookname.length()==0) {
                throw new IllegalArgumentException("book requires a company");
            }

            // check that price and number can not below 0 or be null
            Integer number = values.getAsInteger(ProductionContract.ProductionEntry.PD_number);
            if (number != null && number < 0) {
                throw new IllegalArgumentException("book requires valid number");
            }
            Integer price = values.getAsInteger(ProductionContract.ProductionEntry.PD_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("book requires valid price");
            }

            // Get writeable database
            SQLiteDatabase database = mDbHelper.getWritableDatabase();

            // Insert the new pet with the given values
            long id = database.insert(ProductionContract.ProductionEntry.TABLE_NAME, null, values);
            // If the ID is -1, then the insertion failed. Log an error and return null.
            if (id == -1) {
                Log.e(LOG_TAG, "Failed to insert row for " + uri);
                return null;
            }

            // Notify all listeners that the data has changed for the pet content URI
            getContext().getContentResolver().notifyChange(uri, null);

            // Return the new URI with the ID (of the newly inserted row) appended at the end
            return ContentUris.withAppendedId(uri, id);
        }catch (IllegalArgumentException e){

        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(ProductionContract.ProductionEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOK_ID:
                // Delete a single row given by the ID in the URI
                selection = ProductionContract.ProductionEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(ProductionContract.ProductionEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updatebook(uri, contentValues, selection, selectionArgs);
            case BOOK_ID:
                selection = ProductionContract.ProductionEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updatebook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updatebook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        try{
            if (values.containsKey(ProductionContract.ProductionEntry.BOOKNAME)) {
                String name = values.getAsString(ProductionContract.ProductionEntry.BOOKNAME);
                if (name.length()==0) {
                    throw new IllegalArgumentException("book requires a name");
                }
            }


            // If the {@link PetEntry#COLUMN_PET_WEIGHT} key is present,
            // check that the weight value is valid.
            if (values.containsKey(ProductionContract.ProductionEntry.PD_number)) {
                // Check that the weight is greater than or equal to 0 kg
                Integer number = values.getAsInteger(ProductionContract.ProductionEntry.PD_number);
                if (number != null && number < 0) {
                    throw new IllegalArgumentException("Pet requires valid number");
                }
            }

            // No need to check the breed, any value is valid (including null).

            // If there are no values to update, then don't try to update the database
            if (values.size() == 0) {
                return 0;
            }

            // Otherwise, get writeable database to update the data
            SQLiteDatabase database = mDbHelper.getWritableDatabase();

            // Perform the update on the database and get the number of rows affected
            int rowsUpdated = database.update(ProductionContract.ProductionEntry.TABLE_NAME, values, selection, selectionArgs);

            // If 1 or more rows were updated, then notify all listeners that the data at the
            // given URI has changed
            if (rowsUpdated != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }

            // Return the number of rows updated
            return rowsUpdated;
        }catch (IllegalArgumentException e){

        }
        return 0;
    }
}
