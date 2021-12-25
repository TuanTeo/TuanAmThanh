package com.dev.tuanteo.tuanamthanh.adapter;

import android.content.Context;

import com.dev.tuanteo.tuanamthanh.api.FirebaseFireStoreAPI;
import com.dev.tuanteo.tuanamthanh.listener.ILocalSongClickListener;

public class ListDetailSingerAdapter extends ListSuggestAdapter {

    public ListDetailSingerAdapter(Context context, ILocalSongClickListener listener, String singer) {
        super(context, listener, singer);
    }

    @Override
    protected void getAndUpdateListSong(String condition) {
        FirebaseFireStoreAPI.getListSong(FirebaseFireStoreAPI.SONG_SINGER, condition, this);
    }
}
