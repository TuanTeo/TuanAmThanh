package com.dev.tuanteo.tuanamthanh.api;

import android.util.Log;

import com.dev.tuanteo.tuanamthanh.listener.IFirebaseListener;
import com.dev.tuanteo.tuanamthanh.object.Artist;
import com.dev.tuanteo.tuanamthanh.object.MusicCategory;
import com.dev.tuanteo.tuanamthanh.object.Song;
import com.dev.tuanteo.tuanamthanh.units.Utils;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class FirebaseFireStoreAPI {
    public static final String ALL_SONG_DB = "song";
    public static final String ALL_ARTIST_DB = "artist";
    public static final String ALL_CATEGORY_DB = "category";

    /**
     * TuanTeo: Get all song from firebase
     * @return
     */
    public static void getListSong(String Database, final IFirebaseListener listener) {
        ArrayList<Song> listSuggestSong = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //db.collection(Database).whereEqualTo("name", "Thiên hạ hữu tình nhân").get().addOnCompleteListener(task -> {
        db.collection(ALL_SONG_DB).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.d("getListSong", document.getId() + " => " + document.getData());

                    Song song = new Song();
                    song.setId(document.getId());
                    song.setName(document.getString("name"));
                    song.setArtist(document.getString("singer"));
                    song.setImage(document.getString("artist_image"));
                    song.setPath(document.getString("path"));

                    listSuggestSong.add(song);
                }

                /*TuanTeo: notify about update complete */
                listener.getListSongComplete(listSuggestSong);
            } else {
                Log.d("FirebaseFirestore", "Error getting document ", task.getException());
            }
        });

    }

    public static void getListCategory(String query, IFirebaseListener listener) {
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

    public static void getListArtist(String query, IFirebaseListener listener) {
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
}
