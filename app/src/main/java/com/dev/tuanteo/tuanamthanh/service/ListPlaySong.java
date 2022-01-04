package com.dev.tuanteo.tuanamthanh.service;

import com.dev.tuanteo.tuanamthanh.object.Song;

import java.util.ArrayList;

/**
 * Lớp chứa danh sách bài hát muốn phát
 */
public class ListPlaySong {

    private ArrayList<Song> mPlayList;

    private static ListPlaySong sInstance;

    public static ListPlaySong getInstance() {
        if (sInstance != null) {
            return sInstance;
        } else {
            return sInstance = new ListPlaySong();
        }
    }

    public ArrayList<Song> getPlayList() {
        return mPlayList;
    }

    public void setPlayList(ArrayList<Song> playList) {
        this.mPlayList = playList;
    }
}
