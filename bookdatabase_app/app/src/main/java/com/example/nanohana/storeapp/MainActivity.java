package com.example.nanohana.storeapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nanohana.storeapp.data.BookDbHelper;
import com.example.nanohana.storeapp.data.ProductionContract;
import com.example.nanohana.storeapp.data.bookProvider;
import com.example.nanohana.storeapp.data.bookProvider.*;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,bookCursorAdapter.Callback {

    private static final int BOOK_LOADER = 0;
    bookCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.suspend_icon);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView bookListView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyView);

        mCursorAdapter = new bookCursorAdapter(this, null, this);
        bookListView.setAdapter(mCursorAdapter);

        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);

                Uri currentBookUri = ContentUris.withAppendedId(ProductionContract.ProductionEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentBookUri);

                // Launch the {@link EditorActivity} to display the data for the current pet.
                startActivity(intent);
            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(BOOK_LOADER, null, this);
    }

    private void insertabook() {
        ContentValues values = new ContentValues();
        values.put(ProductionContract.ProductionEntry.BOOKNAME, "Harry Potter");
        values.put(ProductionContract.ProductionEntry.COMPANY, "unknown");
        values.put(ProductionContract.ProductionEntry.COMPANY_PHONE, "13485387802");
        values.put(ProductionContract.ProductionEntry.COMPANY_EMAIL, "694644246@qq.com");
        values.put(ProductionContract.ProductionEntry.PD_PRICE, 25);
        values.put(ProductionContract.ProductionEntry.PD_number, 233);

        Uri newUri = getContentResolver().insert(ProductionContract.ProductionEntry.CONTENT_URI, values);
    }

    private void deleteAllBooks() {
        int rowsDeleted = getContentResolver().delete(ProductionContract.ProductionEntry.CONTENT_URI, null, null);
        Log.v("MainActivity", rowsDeleted + " rows deleted from book database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_book:
                insertabook();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllBooks();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ProductionContract.ProductionEntry._ID,
                ProductionContract.ProductionEntry.BOOKNAME,
                ProductionContract.ProductionEntry.COMPANY,
                ProductionContract.ProductionEntry.COMPANY_PHONE,
                ProductionContract.ProductionEntry.COMPANY_EMAIL,
                ProductionContract.ProductionEntry.PD_PRICE,
                ProductionContract.ProductionEntry.PD_number};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                ProductionContract.ProductionEntry.CONTENT_URI,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public void click(View v) {
        int id= (int) v.getTag();
        String[] projection = {
                ProductionContract.ProductionEntry._ID,
                ProductionContract.ProductionEntry.BOOKNAME,
                ProductionContract.ProductionEntry.COMPANY,
                ProductionContract.ProductionEntry.COMPANY_PHONE,
                ProductionContract.ProductionEntry.COMPANY_EMAIL,
                ProductionContract.ProductionEntry.PD_PRICE,
                ProductionContract.ProductionEntry.PD_number};
        Uri currentBookUri = ContentUris.withAppendedId(ProductionContract.ProductionEntry.CONTENT_URI,id);
        getLoaderManager().initLoader(BOOK_LOADER, null, this);
        Log.e("URI",""+currentBookUri);
        Cursor cursor=getContentResolver().query(currentBookUri,projection,null,null,null);
        int count=cursor.getCount();
        Log.e("count",""+count);
        if (count > 0 && cursor != null) {
            cursor.moveToFirst();
            int bnameColumnIndex = cursor.getColumnIndex(ProductionContract.ProductionEntry.BOOKNAME);
            int cnameColumnIndex = cursor.getColumnIndex(ProductionContract.ProductionEntry.COMPANY);
            int phoneColumnIndex = cursor.getColumnIndex(ProductionContract.ProductionEntry.COMPANY_PHONE);
            int emailColumnIndex = cursor.getColumnIndex(ProductionContract.ProductionEntry.COMPANY_EMAIL);
            int numberColumnIndex = cursor.getColumnIndex(ProductionContract.ProductionEntry.PD_number);
            int priceColumnIndex = cursor.getColumnIndex(ProductionContract.ProductionEntry.PD_PRICE);
            String bookName = cursor.getString(bnameColumnIndex);
            String company = cursor.getString(cnameColumnIndex);
            String phone = cursor.getString(phoneColumnIndex);
            String email = cursor.getString(emailColumnIndex);
            String number = cursor.getString(numberColumnIndex);
            String price = cursor.getString(priceColumnIndex);
            int real_price = Integer.parseInt(price);
            int real_number = Integer.parseInt(number);
            real_number = real_number - 1;
            Log.e("real_number", "" + real_number);
            ContentValues values = new ContentValues();
            values.put(ProductionContract.ProductionEntry._ID, id);
            values.put(ProductionContract.ProductionEntry.BOOKNAME, bookName);
            values.put(ProductionContract.ProductionEntry.COMPANY, company);
            values.put(ProductionContract.ProductionEntry.COMPANY_PHONE, phone);
            values.put(ProductionContract.ProductionEntry.COMPANY_EMAIL, email);
            values.put(ProductionContract.ProductionEntry.PD_number, real_number);
            values.put(ProductionContract.ProductionEntry.PD_PRICE, real_price);
            int rowsUpdated = getContentResolver().update(currentBookUri, values, null, null);
            cursor.close();
        }
    }
}
