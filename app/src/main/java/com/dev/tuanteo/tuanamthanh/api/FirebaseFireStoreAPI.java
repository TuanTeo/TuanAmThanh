package com.dev.tuanteo.tuanamthanh.api;

import android.content.ContentValues;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.dev.tuanteo.tuanamthanh.R;
import com.dev.tuanteo.tuanamthanh.database.DownloadSongDatabase;
import com.dev.tuanteo.tuanamthanh.database.DownloadSongProvider;
import com.dev.tuanteo.tuanamthanh.database.FavoriteSongProvider;
import com.dev.tuanteo.tuanamthanh.listener.IFirebaseListener;
import com.dev.tuanteo.tuanamthanh.object.Artist;
import com.dev.tuanteo.tuanamthanh.object.MusicCategory;
import com.dev.tuanteo.tuanamthanh.object.Song;
import com.dev.tuanteo.tuanamthanh.units.SongUtils;
import com.dev.tuanteo.tuanamthanh.units.Utils;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FirebaseFireStoreAPI {
    public static final String ALL_SONG_DB = "song";
    public static final String ALL_ARTIST_DB = "artist";
    public static final String ALL_CATEGORY_DB = "category";

    //allSong DB
    public static final String SONG_ARTIST_IMAGE = "artist_image";
    public static final String SONG_CATEGORY = "category";
    public static final String SONG_NAME = "name";
    public static final String SONG_PATH = "path";
    public static final String SONG_SINGER = "singer";
    public static final String SONG_SUGGEST = "suggest";

    //category Db
    public static final String CATEGORY_NAME = "name";
    public static final String CATEGORY_AVATAR = "avatar";

    //artist Db
    public static final String ARTIST_NAME = "name";
    public static final String ARTIST_AVATAR = "avatar";

    /*TuanTeo: Cache danh sách bài hát */
    private static ArrayList<Song> mListAllSong;
    private static ArrayList<Song> mListFindSong;
    private static ArrayList<Song> mListSuggestSong;

    /**
     * TuanTeo: Get all song from firebase
     * @return
     */
    public static void getListSong(String value, String condition, final IFirebaseListener listener) {
        ArrayList<Song> listSuggestSong = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference collection = db.collection(ALL_SONG_DB);
        Query query = collection;

        if (!TextUtils.isEmpty(condition)) {
            query = collection.whereEqualTo(value, condition);
        }

        //db.collection(ALL_SONG_DB).whereEqualTo("name", "Thiên hạ hữu tình nhân").get().addOnCompleteListener(task -> {
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.d("getListSong", document.getId() + " => " + document.getData());

                    Song song = new Song();
                    song.setId(document.getId());
                    song.setName(document.getString(SONG_NAME));
                    song.setArtist(document.getString(SONG_SINGER));
                    song.setImage(document.getString(SONG_ARTIST_IMAGE));
                    song.setPath(document.getString(SONG_PATH));

                    listSuggestSong.add(song);
                }

                /*TuanTeo: notify about update complete */
                listener.getListSongComplete(listSuggestSong);

                if (value.equals(SONG_SUGGEST)) {
                    mListSuggestSong = listSuggestSong;
                } else {
                    /*TuanTeo: Cache lại danh sách bài hát đang hiển thị */
                    mListFindSong = listSuggestSong;
                }
            } else {
                Log.d("FirebaseFirestore", "Error getting document ", task.getException());
            }
        });

    }

    public static void getListCategory(IFirebaseListener listener) {
        ArrayList<MusicCategory> listCategory = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(ALL_CATEGORY_DB).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.d("getListCategory", document.getId() + " => " + document.getData());

                    MusicCategory category = new MusicCategory();
                    category.setName(document.getString("name"));
                    category.setAvatar(document.getString("avatar"));

                    listCategory.add(category);
                }

                /*TuanTeo: notify about update complete */
                listener.getListCategoryComplete(listCategory);
            } else {
                Log.d("FirebaseFirestore", "Error getting document ", task.getException());
            }
        });
    }

    public static void getListArtist(IFirebaseListener listener) {
        ArrayList<Artist> listArtist = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(ALL_ARTIST_DB).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.d("getListArtist", document.getId() + " => " + document.getData());

                    Artist artist = new Artist();
                    artist.setName(document.getString("name"));
                    artist.setAvatar(document.getString("avatar"));

                    listArtist.add(artist);
                }

                /*TuanTeo: notify about update complete */
                listener.getListArtistComplete(listArtist);
            } else {
                Log.d("FirebaseFirestore", "Error getting document ", task.getException());
            }
        });
    }

    /**
     * TuanTeo: Get all song from firebase
     * @return
     */
    public static void loadListAllSong() {
        new Thread(() -> {
            ArrayList<Song> listSuggestSong = new ArrayList<>();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            CollectionReference collection = db.collection(ALL_SONG_DB);
            Query query = collection;

            //db.collection(ALL_SONG_DB).whereEqualTo("name", "Thiên hạ hữu tình nhân").get().addOnCompleteListener(task -> {
            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("getListSong", document.getId() + " => " + document.getData());

                        Song song = new Song();
                        song.setId(document.getId());
                        song.setName(document.getString(SONG_NAME));
                        song.setArtist(document.getString(SONG_SINGER));
                        song.setImage(document.getString(SONG_ARTIST_IMAGE));
                        song.setPath(document.getString(SONG_PATH));

                        listSuggestSong.add(song);
                    }

                    mListAllSong = listSuggestSong;

                    Log.d("FirebaseFirestore", "Load All Song Completed!", task.getException());
                } else {
                    Log.d("FirebaseFirestore", "Error getting document ", task.getException());
                }
            });
        }).start();
    }

    public static ArrayList<Song> getListSuggestSong() {
        return mListSuggestSong;
    }

    public static ArrayList<Song> getListFindSong() {
        if (mListFindSong == null) {
            return getListSuggestSong();
        }
        return mListFindSong;
    }

    public static void setListFindSong(List<Song> mListFindSong) {
        FirebaseFireStoreAPI.mListFindSong = (ArrayList<Song>) mListFindSong;
    }

    public static ArrayList<Song> getListAllSong() {
        return mListAllSong;
    }

    /**
     * TuanTeo: Tai bai hat online
     * @param mContext
     * @param song
     */
    public static void downloadSong(Context mContext, Song song) {
        mContext.getContentResolver().insert(DownloadSongProvider.CONTENT_URI,
                SongUtils.getContentDownloadSong(song));

        downloadMusicFile(mContext, song.getPath(), song.getId());
        downloadImageFile(mContext, song.getImage(), song.getId());

        Toast.makeText(mContext, mContext.getString(R.string.download_complete), Toast.LENGTH_SHORT).show();
    }

    public static void downloadMusicFile(Context context, String path, String id) {
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(path);

        File rootPath = new File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), "music");
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }

        final File localFile = new File(rootPath,id + ".mp3");

        mStorageRef.getFile(localFile).addOnSuccessListener(file -> {
            Log.d("downloadMusicFile", "downloadMusicFile: addOnSuccessListener");

            ContentValues values = new ContentValues();
            values.put(DownloadSongDatabase.COLUMN_SONG_PATH, localFile.getPath());

            String condition = DownloadSongDatabase.COLUMN_SONG_ID + "= '" + id +"'";

            context.getContentResolver().update(DownloadSongProvider.CONTENT_URI, values,
                    condition, null);

            context.getContentResolver().update(FavoriteSongProvider.CONTENT_URI, values,
                    condition, null);
        });
    }

    public static void downloadImageFile(Context context, String path, String id) {
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(path);

        File rootPath = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "image");
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }

        final File localFile = new File(rootPath,id + ".jpg");

        mStorageRef.getFile(localFile).addOnSuccessListener(file -> {
            Log.d("downloadImageFile", "downloadImageFile: addOnSuccessListener");
            ContentValues values = new ContentValues();
            values.put(DownloadSongDatabase.COLUMN_SONG_IMAGE, localFile.getPath());

            String condition = DownloadSongDatabase.COLUMN_SONG_ID + "= '" + id +"'";

            context.getContentResolver().update(DownloadSongProvider.CONTENT_URI, values,
                    condition, null);

            context.getContentResolver().update(FavoriteSongProvider.CONTENT_URI, values,
                    condition, null);
        });
    }
}
