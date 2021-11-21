package com.dev.tuanteo.tuanamthanh.units;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.dev.tuanteo.tuanamthanh.object.Song;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class LocalSongUtils {

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
        return tempAudioList;
    }
}
