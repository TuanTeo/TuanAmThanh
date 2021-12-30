package com.dev.tuanteo.tuanamthanh.adapter;

import android.content.Context;

import com.dev.tuanteo.tuanamthanh.listener.ILocalSongClickListener;
import com.dev.tuanteo.tuanamthanh.object.Song;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends ListLocalSongAdapter {

    public SearchAdapter(Context context, List<Song> listSong, ILocalSongClickListener listener) {
        super(context, listSong, listener);
    }

    public void setListSong(ArrayList<Song> listResult) {
        mListSong = listResult;
    }
}
