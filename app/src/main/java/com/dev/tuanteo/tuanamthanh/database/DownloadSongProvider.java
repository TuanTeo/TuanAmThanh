package com.dev.tuanteo.tuanamthanh.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DownloadSongProvider extends ContentProvider {

    //Authority: Thẩm quyền
    private static final String AUTHORITY = "com.dev.tuanteo.tuanamthanh.download";
    private static final String DOWNLOAD_SONG_BASE_PATH = "download";

    //Uri of FavoriteSong Database
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + DOWNLOAD_SONG_BASE_PATH);

    public static final int TUTORIALS = 100;
    public static final int TUTORIAL_ID = 110;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(AUTHORITY, DOWNLOAD_SONG_BASE_PATH, TUTORIALS);
        sUriMatcher.addURI(AUTHORITY, DOWNLOAD_SONG_BASE_PATH + "/#", TUTORIAL_ID);
    }

    private SQLiteDatabase mObjWriteDB;
    private DownloadSongDatabase mDownloadSongDB;

    @Override
    public boolean onCreate() {
        mDownloadSongDB = new DownloadSongDatabase(getContext());
        mObjWriteDB = mDownloadSongDB.getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(DownloadSongDatabase.TABLE_NAME);
        Cursor cursor = queryBuilder.query(mDownloadSongDB.getReadableDatabase(),projection
                , selection, selectionArgs,null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        //Kiểm tra xem ID cua Song đã có trong Database chưa
        assert values != null;
        Cursor cursor = query(CONTENT_URI,null,
                DownloadSongDatabase.COLUMN_SONG_ID + " = ?",
                new String[]{values.getAsString(DownloadSongDatabase.COLUMN_SONG_ID)},
                null);

        //Kiem tra trong cursor co phan tu nao khong
        if(cursor != null && cursor.moveToFirst()){   //Co phan tu ( da co trong database ) => khong them
            cursor.close();
            return null;
        } else {                    //Chua co phan tu => them
            long rowID = mObjWriteDB.insert(DownloadSongDatabase.TABLE_NAME, null, values);

            if (rowID > 0) {
                Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
                getContext().getContentResolver().notifyChange(_uri, null);
                if (cursor != null) {
                    cursor.close();
                }
                return _uri;
            }

            values.get(DownloadSongDatabase.COLUMN_SONG_ID);

            throw new SQLException("Fail to add a record into " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int uriType = sUriMatcher.match(uri);
        SQLiteDatabase sqlDB = mDownloadSongDB.getWritableDatabase();
        int rowsDeleted;
        switch (uriType) {
            case TUTORIALS:
                rowsDeleted = sqlDB.delete(DownloadSongDatabase.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case TUTORIAL_ID:
                String id = uri.getPathSegments().get(0);
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(DownloadSongDatabase.TABLE_NAME,
                            DownloadSongDatabase.COLUMN_SONG_ID + "=" + id,null);
                } else {
                    rowsDeleted = sqlDB.delete(DownloadSongDatabase.TABLE_NAME,
                            DownloadSongDatabase.COLUMN_SONG_ID + "=" + id + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: Lam gi co " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int uriType = sUriMatcher.match(uri);
        SQLiteDatabase sqlDB = mDownloadSongDB.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType){
            case TUTORIALS:
                rowsUpdated = sqlDB.update(DownloadSongDatabase.TABLE_NAME,
                        values, selection, selectionArgs);
                break;
            case TUTORIAL_ID:
//                String id = uri.getLastPathSegment();
//                if(TextUtils.isEmpty(selection)){
//                    /*TuanTeo: Code mau (giu lai tham khao)*/
//                    rowsUpdated = sqlDB.update(FavoriteSongDataBase.TABLE_SONG, values,
//                            FavoriteSongDataBase.COLUMN_PATH + "=" +id + "and" + selection,
//                            selectionArgs);
//                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
