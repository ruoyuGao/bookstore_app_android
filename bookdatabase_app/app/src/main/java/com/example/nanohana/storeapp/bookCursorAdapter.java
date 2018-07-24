package com.example.nanohana.storeapp;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nanohana.storeapp.data.ProductionContract;

import java.util.List;

/**
 * Created by nanohana on 2018/5/31.
 */

public class bookCursorAdapter extends CursorAdapter {

    private static final String TAG = "ContentAdapter";
    public Callback mCallback;
    public static int id;

    public bookCursorAdapter(Context context, Cursor c, Callback callback) {
        super(context, c);
        mCallback = callback;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View ret = null;

        ret = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return ret;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.bookname);
        TextView summaryTextView = (TextView) view.findViewById(R.id.company);
        TextView numberTextView = (TextView) view.findViewById(R.id.number);

        // Find the columns of pet attributes that we're interested in
        int bnameColumnIndex = cursor.getColumnIndex(ProductionContract.ProductionEntry.BOOKNAME);
        int cnameColumnIndex = cursor.getColumnIndex(ProductionContract.ProductionEntry.COMPANY);
        int numberColumnIndex = cursor.getColumnIndex(ProductionContract.ProductionEntry.PD_number);

        String bookName = cursor.getString(bnameColumnIndex);
        String company = cursor.getString(cnameColumnIndex);
        String number = cursor.getString(numberColumnIndex);

        if (TextUtils.isEmpty(company)) {
            company = context.getString(R.string.unknown_type);
        }

        nameTextView.setText(bookName);
        summaryTextView.setText(company);
        numberTextView.setText(number);
        Button buildConn = (Button) view.findViewById(R.id.mBtn);
        final int id = cursor.getInt(cursor.getColumnIndex(ProductionContract.ProductionEntry._ID));
        buildConn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setTag(id);
                mCallback.click(v);
            }
        });
    }

    public interface Callback {
        public void click(View v);
    }
}

