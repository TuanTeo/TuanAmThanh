package com.dev.tuanteo.tuanamthanh.listener;

import com.dev.tuanteo.tuanamthanh.object.Song;

import java.util.ArrayList;

public interface IFirebaseListener {
    void updateComplete(ArrayList<Song> listSong);
}
