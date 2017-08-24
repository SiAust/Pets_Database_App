/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.android.pets.data.PetContract.PetEntry;
import com.example.android.pets.data.PetDbHelper;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = EditorActivity.class.getSimpleName();

    private static final int EDIT_PET_LOADER = 1;

    /** EditText field to enter the pet's name */
    private EditText mNameEditText;

    /** EditText field to enter the pet's breed */
    private EditText mBreedEditText;

    /** EditText field to enter the pet's weight */
    private EditText mWeightEditText;

    /** EditText field to enter the pet's gender */
    private Spinner mGenderSpinner;
    /**
     * Gender of the pet. The possible values are:
     * 0 for unknown gender, 1 for male, 2 for female.
     */
    private int mGender = 0;
    // A global Uri object so we can pass the Uri from the intent to the loader.
    private Uri mUri;
    // A global Cursor object to pass data from the Loader
    private Cursor mCursor;

    private Long mRowId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        //Get the Uri from the OnItemClicked intent object passed from CatalogActivity
        mUri = getIntent().getData();
        // If mUri isn't null log a statement containing it's value in a String.
        if (mUri != null) {
            Log.i(LOG_TAG, "Uri passed from CatalogActivity: " + Uri.parse(mUri.toString()));
        }
        //Get an instance of the ActionBar so we can change the title of the activity
        ActionBar actionBar = getSupportActionBar();

        if (mUri != null) {
            //If uri is not null, user clicked on existing pet item and we set title to "Edit a pet"
            actionBar.setTitle(getString(R.string.editor_activity_edit_pet));
            // Initialise the Loader to get a single pet Cursor
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(EDIT_PET_LOADER, null, this);
        } else {
            //If uri is null this is a new pet so we set title to "Add a pet"
            actionBar.setTitle(getString(R.string.editor_activity_title_new_pet));
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        setupSpinner();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = PetEntry.GENDER_MALE; // Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = PetEntry.GENDER_FEMALE; // Female
                    } else {
                        mGender = PetEntry.GENDER_UNKNOWN; // Unknown
                    }
                    Log.i(LOG_TAG, "genderSpinner value: " + mGender);
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = 0; // Unknown
            }
        });
    }

    private void savePet(){
        PetDbHelper dbHelper = new PetDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String nameString = mNameEditText.getText().toString().trim();
        String breedString = mBreedEditText.getText().toString().trim();
        String weightString = mWeightEditText.getText().toString().trim();
        int weight = Integer.parseInt(weightString);

        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME, nameString);
        values.put(PetEntry.COLUMN_PET_BREED, breedString);
        values.put(PetEntry.COLUMN_PET_GENDER, mGender);
        values.put(PetEntry.COLUMN_PET_WEIGHT, weight);

       if (mUri == null) {
           //mUri is null meaning this is a new pet so we insert a new row.
           getContentResolver().insert(PetEntry.CONTENT_URI, values);
       } else {
           // mUri is not null so this is an existing pet and we update it using mUri to find its
           // location in the table.
           int rowsAffected = getContentResolver().update(mUri, values, null, null);
       }

//        mRowId = db.insert(PetEntry.TABLE_NAME, null, values);
    }

    /* Moved toast messages to PetProvider temporarily
     private void makeToast() {
        if (mRowId == -1){
            Toast.makeText(this, "Error saving pet", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Pet saved with id: " + mRowId, Toast.LENGTH_SHORT).show();
        }
    } */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                savePet();
                finish();
                //makeToast();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUri == null) {
            return null;
        }

        String[] projection = {
                PetEntry._ID,
                PetEntry.COLUMN_PET_WEIGHT,
                PetEntry.COLUMN_PET_GENDER,
                PetEntry.COLUMN_PET_BREED,
                PetEntry.COLUMN_PET_NAME
        };

        return new CursorLoader(this, mUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        boolean cursorEmpty;
        if (data != null) {
            cursorEmpty = false;
        } else {
            cursorEmpty = true;
        }
        Log.i(LOG_TAG, "onLoadFinished cursor null? " + cursorEmpty);
        // Move to the first row of the returned cursor object
        data.moveToFirst();
        // Get values from appropriate columns
        String name = data.getString(data.getColumnIndex(PetEntry.COLUMN_PET_NAME));
        String breed = data.getString(data.getColumnIndex(PetEntry.COLUMN_PET_BREED));
        int gender = data.getInt(data.getColumnIndex(PetEntry.COLUMN_PET_GENDER));
        String weight = data.getString(data.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT));
        // Set values on our activity fields
        mNameEditText.setText(name);
        mBreedEditText.setText(breed);
//        mGenderSpinner.setSelection(gender);
        switch (gender) {
            case PetEntry.GENDER_MALE:
                mGenderSpinner.setSelection(1);
                break;
            case PetEntry.GENDER_FEMALE:
                mGenderSpinner.setSelection(2);
                break;
            case PetEntry.GENDER_UNKNOWN:
                mGenderSpinner.setSelection(0);
                break;
        }
        mWeightEditText.setText(weight);
        Log.i(LOG_TAG, "Loader data name: " + name);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.clearComposingText();
        mBreedEditText.clearComposingText();
        mGenderSpinner.setSelection(PetEntry.GENDER_UNKNOWN);
        mWeightEditText.clearComposingText();
    }

}