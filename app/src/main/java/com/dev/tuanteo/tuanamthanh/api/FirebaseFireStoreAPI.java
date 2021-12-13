package com.dev.tuanteo.tuanamthanh.api;

import android.util.Log;

import com.dev.tuanteo.tuanamthanh.listener.IFirebaseListener;
import com.dev.tuanteo.tuanamthanh.object.Song;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class FirebaseFireStoreAPI {
    public static final String ALL_SONG_DB = "song";

    /**
     * TuanTeo: Get all song from firebase
     * @return
     */
    public static ArrayList<Song> getListSong(String Database, final IFirebaseListener listener) {
        ArrayList<Song> listSuggestSong = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(Database).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.d("FirebaseFirestore", document.getId() + " => " + document.getData());

                    Song song = new Song();
                    song.setName(document.getString("name"));
                    song.setArtist(document.getString("singer"));
                    song.setImage(document.getString("artist_image"));

                    listSuggestSong.add(song);
                }

                /*TuanTeo: notify about update complete */
                listener.updateComplete(listSuggestSong);
            } else {
                Log.d("FirebaseFirestore", "Error getting document ", task.getException());
            }
        });

        return listSuggestSong;
    }
}
