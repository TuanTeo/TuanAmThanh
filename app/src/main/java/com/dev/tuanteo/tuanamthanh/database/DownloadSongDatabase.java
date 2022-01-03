package com.dev.tuanteo.tuanamthanh.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DownloadSongDatabase extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "download.db";

    public static final String TABLE_NAME = "download";
    public static final String COLUMN_SONG_ID = "_id";
    public static final String COLUMN_SONG_NAME = "name";
    public static final String COLUMN_SONG_PATH = "path";
    public static final String COLUMN_SONG_ARTIST = "artist";
    public static final String COLUMN_SONG_IMAGE = "image";
    public static final String COLUMN_SONG_ALBUM = "album";

    public static final String[] BASE_COLUMN = {
            COLUMN_SONG_ID,
            COLUMN_SONG_NAME,
            COLUMN_SONG_PATH,
            COLUMN_SONG_ARTIST,
            COLUMN_SONG_IMAGE,
            COLUMN_SONG_ALBUM
    };

    private String SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_SONG_ID + " TEXT," +
            COLUMN_SONG_NAME + " TEXT," +
            COLUMN_SONG_PATH + " TEXT," +
            COLUMN_SONG_ARTIST + " TEXT," +
            COLUMN_SONG_IMAGE + " TEXT," +
            COLUMN_SONG_ALBUM + " TEXT)";

    public DownloadSongDatabase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop older table if existed
        String sqlDropTableQuery = "DROP TABLE IF EXISTS";
        db.execSQL(sqlDropTableQuery + TABLE_NAME);
        onCreate(db);
    }
}
