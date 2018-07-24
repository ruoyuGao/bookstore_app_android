package com.example.nanohana.storeapp;


import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import com.example.nanohana.storeapp.data.BookDbHelper;
import com.example.nanohana.storeapp.data.ProductionContract;

public class EditorActivity  extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final int EXISTING_BOOK_LOADER = 0;
    private Uri mCurrentBookUri;
    private boolean mBookHasChanged = false;

    private EditText bNameEditText;
    private EditText cNameEditText;
    private EditText PhoneNumberEditText;
    private EditText EMADEditText;
    private EditText BpriceEditText;
    private EditText BnumberEditText;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        if (mCurrentBookUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_pet));
            invalidateOptionsMenu();
        } else {
            setTitle("change a book");
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        bNameEditText = (EditText) findViewById(R.id.edit_production_name);
        cNameEditText = (EditText) findViewById(R.id.edit_production_company);
        PhoneNumberEditText = (EditText) findViewById(R.id.edit_phone_number);
        EMADEditText = (EditText) findViewById(R.id.edit_email_address);
        BpriceEditText = (EditText) findViewById(R.id.edit_production_price);
        BnumberEditText = (EditText) findViewById(R.id.edit_production_number);

        bNameEditText.setOnTouchListener(mTouchListener);
        cNameEditText.setOnTouchListener(mTouchListener);
        PhoneNumberEditText.setOnTouchListener(mTouchListener);
        EMADEditText.setOnTouchListener(mTouchListener);
        BpriceEditText.setOnTouchListener(mTouchListener);
        BnumberEditText.setOnTouchListener(mTouchListener);
    }

    public void savedata(){
        String bnameString = bNameEditText.getText().toString().trim();
        String cnamestring=cNameEditText.getText().toString().trim();
        String phonenumber=PhoneNumberEditText.getText().toString().trim();
        String email=EMADEditText.getText().toString().trim();
        String price=BpriceEditText.getText().toString().trim();
        String number=BnumberEditText.getText().toString().trim();
        int real_price=0;
        int real_number=0;

        if (mCurrentBookUri == null &&
                TextUtils.isEmpty(bnameString) && TextUtils.isEmpty(cnamestring) &&
                TextUtils.isEmpty(phonenumber) && TextUtils.isEmpty(email)) {
            // Since no fields were modified, we can return early without creating a new pet.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            Toast.makeText(this,"no data",Toast.LENGTH_LONG).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ProductionContract.ProductionEntry.BOOKNAME,bnameString);
        values.put(ProductionContract.ProductionEntry.COMPANY,cnamestring);
        values.put(ProductionContract.ProductionEntry.COMPANY_PHONE,phonenumber);
        values.put(ProductionContract.ProductionEntry.COMPANY_EMAIL,email);
        if (!TextUtils.isEmpty(price)) {
             real_price = Integer.parseInt(price);
        }
        values.put(ProductionContract.ProductionEntry.PD_PRICE,real_price);
        if (!TextUtils.isEmpty(number)) {
            real_number = Integer.parseInt(number);
        }
        values.put(ProductionContract.ProductionEntry.PD_number,real_number);

        if (mCurrentBookUri == null) {
            // This is a NEW pet, so insert a new pet into the provider,
            // returning the content URI for the new pet.
            Uri newUri = getContentResolver().insert(ProductionContract.ProductionEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING pet, so update the pet with content URI: mCurrentPetUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentPetUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                savedata();
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.unsaved_changes_dialog_msg);
            builder.setPositiveButton(R.string.discard, discardButtonClickListener);
            builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked the "Keep editing" button, so dismiss the dialog
                    // and continue editing the pet.
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
    }
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("delete this book?");
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteBook() {
        if (mCurrentBookUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all pet attributes, define a projection that contains
        // all columns from the pet table
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
                mCurrentBookUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int bnameColumnIndex = cursor.getColumnIndex(ProductionContract.ProductionEntry.BOOKNAME);
            int cnameColumnIndex = cursor.getColumnIndex(ProductionContract.ProductionEntry.COMPANY);
            int phoneColumnIndex = cursor.getColumnIndex(ProductionContract.ProductionEntry.COMPANY_PHONE);
            int emailColumnIndex = cursor.getColumnIndex(ProductionContract.ProductionEntry.COMPANY_EMAIL);
            int priceColumnIndex=cursor.getColumnIndex(ProductionContract.ProductionEntry.PD_PRICE);
            int numberColumnIndex=cursor.getColumnIndex(ProductionContract.ProductionEntry.PD_number);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(bnameColumnIndex);
            String company = cursor.getString(cnameColumnIndex);
            String phnoe=cursor.getString(phoneColumnIndex);
            String email=cursor.getString(emailColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int number = cursor.getInt(numberColumnIndex);

            // Update the views on the screen with the values from the database
            bNameEditText.setText(name);
            cNameEditText.setText(company);
            PhoneNumberEditText.setText(phnoe);
            EMADEditText.setText(email);
            BpriceEditText.setText(Integer.toString(price));
            BnumberEditText.setText(Integer.toString(number));

        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        bNameEditText.setText("");
        cNameEditText.setText("");
        PhoneNumberEditText.setText("");
        EMADEditText.setText("");
        BpriceEditText.setText("");
        BnumberEditText.setText("");
    }

   public  void add(View view){
       EditText textView=(EditText)findViewById(R.id.edit_production_number);
       String s=textView.getText().toString();
       if(s.length()!=0){
       int i=Integer.parseInt(s);
       String s1=String.valueOf(i+1);
       if(i>=0) {
           textView.setText(s1);
           savedata();
       }else{
        Toast.makeText(this,"error number under 0",Toast.LENGTH_SHORT).show();
       }
   }else{
           Toast.makeText(this,"no number here",Toast.LENGTH_SHORT).show();
       }
    }
   public  void book(View view){
       Intent intent =new Intent();
       intent.setAction(Intent.ACTION_DIAL);
       EditText textView=(EditText)findViewById(R.id.edit_phone_number);
       String s=textView.getText().toString();
       Uri number = Uri.parse("tel:"+s);
       Intent i = new Intent(Intent.ACTION_DIAL, number);
       startActivity(i);
   }
   public  void minus(View view){
       EditText textView=(EditText)findViewById(R.id.edit_production_number);
       String s=textView.getText().toString();
       if(s.length()!=0){
       int i=Integer.parseInt(s);
       String s1=String.valueOf(i-1);
       if(i>=1) {
           textView.setText(s1);
           savedata();
       }else{
           Toast.makeText(this,"error number under 0",Toast.LENGTH_SHORT).show();
       }
   }else{
           Toast.makeText(this,"no number here",Toast.LENGTH_SHORT).show();
       }
   }

}
