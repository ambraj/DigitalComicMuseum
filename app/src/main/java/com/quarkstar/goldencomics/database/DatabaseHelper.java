package com.quarkstar.goldencomics.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String TABLE_COMIC = "comic";
    public static final String TABLE_SERIES = "series";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_SERIES_ID = "series_id";
    public static final String COLUMN_SERIES_URL = "url";
    public static final String COLUMN_PAGE_COUNT = "pages_count";

    public static final String CONDITION_TRUE = "1=1";

    public static final String DATABASE_NAME = "goldencomics.db";

    public static String ERROR_TAG = "ERROR: GoldenComics ";

    private static String DB_PATH = "/data/com.quarkstar.goldencomics/databases/";

    private final Context myContext;

    /**
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.myContext = context;
        DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     */
    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();

        if (dbExist) {
            //do nothing - database already exist
        } else {
            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getWritableDatabase();

            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }

    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     *
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;

        try {
            String myPath = DB_PATH + DATABASE_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            Log.e(ERROR_TAG, "checkDataBase: can't open database" + DB_PATH + DATABASE_NAME);
        }

        if (checkDB != null) {
            checkDB.close();
        }

        return checkDB != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     */
    private void copyDataBase() throws IOException {

        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DATABASE_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DATABASE_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public Cursor fetchComicData(String tableName, String condition) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + tableName + " where " + condition, null);

        return cursor;
    }

    public Cursor fetchComicDataUsingQuery(String query) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    public int addComicToLibrary(String comicId, String status){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("favorite", status);

        return db.update(TABLE_COMIC, values, COLUMN_ID + "=" + comicId, null);
    }

    public int updateLastReadPageNo(String comicId, int pageNo){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("last_read_page", pageNo);

        return db.update(TABLE_COMIC, values, COLUMN_ID + "=" + comicId, null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
