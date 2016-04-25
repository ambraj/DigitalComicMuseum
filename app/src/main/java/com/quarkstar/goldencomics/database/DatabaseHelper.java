package com.quarkstar.goldencomics.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.*;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String TABLE_COMIC = "comic";
    public static final String TABLE_SERIES = "series";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_SERIES_ID = "series_id";
    public static final String COLUMN_SERIES_URL = "url";
    public static final String COLUMN_PAGE_COUNT = "pages_count";

    public static final String CONDITION_TRUE = "1=1";

    private static final String DATABASE_NAME = "goldencomics.db";
    private static final int DATABASE_VERSION = 13;
    private static final String SP_KEY_DB_VER = "db_ver";
    public static String ERROR_TAG = "ERROR:  GoldenComics ";
    private final Context mContext;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
        initialize();
    }

    /**
     * Initializes database. Creates database if doesn't exist.
     */
    private void initialize() {

        if (databaseExists()) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            int dbVersion = prefs.getInt(SP_KEY_DB_VER, 1);
            Log.e(ERROR_TAG, "DATABASE_VERSION="+DATABASE_VERSION+" || dbVersion="+dbVersion);
            if (DATABASE_VERSION != dbVersion) {
                File dbFile = mContext.getDatabasePath(DATABASE_NAME);
                if (!dbFile.delete()) {
                    Log.w(ERROR_TAG, "Unable to update database");
                }
            }
        }
        if (!databaseExists()) {
            createDatabase();
        }
    }

    /**
     * Returns true if database file exists, false otherwise.
     *
     * @return
     */
    private boolean databaseExists() {
        File dbFile = mContext.getDatabasePath(DATABASE_NAME);
        return dbFile.exists();
    }

    /**
     * Creates database by copying it from assets directory.
     */
    private void createDatabase() {
        String parentPath = mContext.getDatabasePath(DATABASE_NAME).getParent();
        String path = mContext.getDatabasePath(DATABASE_NAME).getPath();

        File file = new File(parentPath);
        if (!file.exists()) {
            if (!file.mkdir()) {
                Log.w(ERROR_TAG, "Unable to create database directory");
                return;
            }
        }

        InputStream is = null;
        OutputStream os = null;
        try {
            is = mContext.getAssets().open(DATABASE_NAME);
            os = new FileOutputStream(path);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.flush();
            SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(mContext);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(SP_KEY_DB_VER, DATABASE_VERSION);
            editor.commit();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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

    public int addComicToLibrary(String comicId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("favorite", status);

        return db.update(TABLE_COMIC, values, COLUMN_ID + "=" + comicId, null);
    }

    public int updateLastReadPageNo(String comicId, int pageNo) {
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
