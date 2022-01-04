package com.dev.tuanteo.tuanamthanh.units;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.dev.tuanteo.tuanamthanh.api.FirebaseFireStoreAPI;
import com.dev.tuanteo.tuanamthanh.database.DownloadSongDatabase;
import com.dev.tuanteo.tuanamthanh.database.DownloadSongProvider;
import com.dev.tuanteo.tuanamthanh.database.FavoriteSongProvider;
import com.dev.tuanteo.tuanamthanh.object.Song;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SongUtils {

    public static final ArrayList<Song> getListLocalSong(Context context) {
        WeakReference<Context> contextWeakReference = new WeakReference<>(context);
        final ArrayList<Song> tempAudioList = new ArrayList<>();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Audio.AudioColumns.TITLE,// 0
                MediaStore.Audio.AudioColumns.TRACK,// 1
                MediaStore.Audio.AudioColumns.YEAR,// 2
                MediaStore.Audio.AudioColumns.DURATION,// 3
                MediaStore.Audio.AudioColumns.DATA,// 4
                MediaStore.Audio.AudioColumns.ALBUM,// 5
                MediaStore.Audio.AudioColumns.ARTIST_ID,// 6
                MediaStore.Audio.AudioColumns.ARTIST,// 7
                MediaStore.Audio.Media.ALBUM_ID, //8
                MediaStore.Audio.Albums._ID // 9
        };

        // if want fetch all files
        Cursor c = contextWeakReference.get().getContentResolver().query(uri,
                projection,
                null,
                null,
                null);

        if (c != null) {
            while (c.moveToNext()) {
                Song audioModel = new Song();
                String path = c.getString(4);
                String name = c.getString(0);
                String album = c.getString(5);
                String artist = c.getString(7);
                String id = c.getString(9);
                int duration = c.getInt(3);

                audioModel.setId(id);
                audioModel.setName(name);
                audioModel.setAlbum(album);
                audioModel.setArtist(artist);
                audioModel.setPath(path);
                audioModel.setDuration(duration);

                Log.d("Name :" + name, " Album :" + album);
                Log.d("Path :" + path, " Artist :" + artist);
                Log.d("id:", path);

                /*TuanTeo: Chi lay file am thanh tren 5s */
                if (duration >= 5000) {
                    tempAudioList.add(audioModel);
                }
            }
            c.close();
        }

        /*TuanTeo: Them danh sach nhac tai ve */
        tempAudioList.addAll(getListDownLoadSong(context));

        Collections.sort(tempAudioList);
        return tempAudioList;
    }

    public static final ArrayList<Song> getListAllSong(Context context) {
        WeakReference<Context> contextWeakReference = new WeakReference<>(context);

        ArrayList<Song> allSongList = new ArrayList<>();
        ArrayList<Song> localSongList = getListLocalSong(contextWeakReference.get());
        ArrayList<Song> onlineSongList = FirebaseFireStoreAPI.getListAllSong();

        allSongList.addAll(localSongList);
        allSongList.addAll(onlineSongList);

        return allSongList;
    }

    /**
     * Get list bài hát theo tên ca sĩ
     * @param singer
     * @return
     */
    public static List<Song> getListArtistSong(String singer) {
        ArrayList<Song> artistSongList = new ArrayList<>();

        for (Song song : FirebaseFireStoreAPI.getListAllSong()) {
            if (song.getArtist().contains(singer)) {
                artistSongList.add(song);
            }
        }

        return artistSongList;
    }

    public static ContentValues getContentDownloadSong(Song song) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(DownloadSongDatabase.COLUMN_SONG_ID, song.getId());
        contentValues.put(DownloadSongDatabase.COLUMN_SONG_NAME, song.getName());
        contentValues.put(DownloadSongDatabase.COLUMN_SONG_ARTIST, song.getArtist());
        contentValues.put(DownloadSongDatabase.COLUMN_SONG_PATH, song.getPath());
        contentValues.put(DownloadSongDatabase.COLUMN_SONG_IMAGE, song.getImage());
        contentValues.put(DownloadSongDatabase.COLUMN_SONG_ALBUM, song.getAlbum());

        return contentValues;
    }

    /**
     * Danh sách bài hát tải về
     * @param context
     * @return
     */
    public static ArrayList<Song> getListDownLoadSong(Context context) {
        ArrayList<Song> listDownloadSong = new ArrayList<>();

        Cursor cursor = context.getContentResolver().query(DownloadSongProvider.CONTENT_URI, DownloadSongDatabase.BASE_COLUMN,
                null, null, null);

        if (cursor!=null && cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndexOrThrow(DownloadSongDatabase.COLUMN_SONG_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DownloadSongDatabase.COLUMN_SONG_NAME));
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(DownloadSongDatabase.COLUMN_SONG_ARTIST));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(DownloadSongDatabase.COLUMN_SONG_PATH));
                String image = cursor.getString(cursor.getColumnIndexOrThrow(DownloadSongDatabase.COLUMN_SONG_IMAGE));
                String album = cursor.getString(cursor.getColumnIndexOrThrow(DownloadSongDatabase.COLUMN_SONG_ALBUM));

                Song song = new Song(id, name, artist, path, image, album);
                listDownloadSong.add(song);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }

        return listDownloadSong;
    }

    /**
     * Danh sách bài hát yêu thích
     * @param context
     * @return
     */
    public static ArrayList<Song> getListFavoriteSong(Context context) {
        ArrayList<Song> listFavoriteSong = new ArrayList<>();

        Cursor cursor = context.getContentResolver().query(FavoriteSongProvider.CONTENT_URI, DownloadSongDatabase.BASE_COLUMN,
                null, null, null);

        if (cursor!=null && cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndexOrThrow(DownloadSongDatabase.COLUMN_SONG_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DownloadSongDatabase.COLUMN_SONG_NAME));
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(DownloadSongDatabase.COLUMN_SONG_ARTIST));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(DownloadSongDatabase.COLUMN_SONG_PATH));
                String image = cursor.getString(cursor.getColumnIndexOrThrow(DownloadSongDatabase.COLUMN_SONG_IMAGE));
                String album = cursor.getString(cursor.getColumnIndexOrThrow(DownloadSongDatabase.COLUMN_SONG_ALBUM));

                Song song = new Song(id, name, artist, path, image, album);
                listFavoriteSong.add(song);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }

        Collections.sort(listFavoriteSong);

        return listFavoriteSong;
    }
}
