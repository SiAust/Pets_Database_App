package com.example.android.pets.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Simon Aust on 08/08/2017.
 */

public final class PetContract  {

    public static final String CONTENT_AUTHORITY = "com.example.android.pets";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PETS = "pets";


    //Prevent accidental instantiating of class with private default constructor.
    private PetContract(){
    }

    public static class PetEntry implements BaseColumns {
        //Constant Uri for our ContentProvider class, points to the database.
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS);
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                        + CONTENT_AUTHORITY + "/" + PATH_PETS;
        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_PETS;
        //Constant value for id column.
        public static final String TABLE_NAME = "pets";
        public static final String _ID = BaseColumns._ID;
        // Constant value for table name.
        public static final String COLUMN_PET_NAME = "name";
        //Constant value for name column.
        public static final String COLUMN_PET_BREED = "breed";
        //Constant value for gender column.
        public static final String COLUMN_PET_GENDER = "gender";
        //Constant value for weight column.
        public static final String COLUMN_PET_WEIGHT = "weight";

        //Values for gender
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;
        public static final int GENDER_UNKNOWN = 0;

        public static boolean isValidGender(int gender){
            if (gender == GENDER_FEMALE||gender == GENDER_MALE||gender == GENDER_UNKNOWN) {
                return true;
            }
            return false;
        }

    }

}
