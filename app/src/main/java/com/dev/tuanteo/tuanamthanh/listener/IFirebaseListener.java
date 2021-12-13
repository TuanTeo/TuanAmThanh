package com.dev.tuanteo.tuanamthanh.listener;

import com.dev.tuanteo.tuanamthanh.object.Artist;
import com.dev.tuanteo.tuanamthanh.object.MusicCategory;
import com.dev.tuanteo.tuanamthanh.object.Song;

import java.util.ArrayList;

public interface IFirebaseListener {
    void getListSongComplete(ArrayList<Song> listSong);
    void getListCategoryComplete(ArrayList<MusicCategory> listCategory);
    void getListArtistComplete(ArrayList<Artist> listArtist);
}
