package com.dev.tuanteo.tuanamthanh.adapter;

import android.content.Context;

import com.dev.tuanteo.tuanamthanh.api.FirebaseFireStoreAPI;
import com.dev.tuanteo.tuanamthanh.listener.ILocalSongClickListener;

public class ListDetailCategoryAdapter extends ListSuggestAdapter {

    public ListDetailCategoryAdapter(Context context, ILocalSongClickListener listener, String category) {
        super(context, listener, category);
    }

    @Override
    protected void getAndUpdateListSong(String condition) {
        FirebaseFireStoreAPI.getListSong(FirebaseFireStoreAPI.SONG_CATEGORY, condition, this);
    }
}
