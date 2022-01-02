package com.dev.tuanteo.tuanamthanh.adapter;

import android.content.Context;

import com.dev.tuanteo.tuanamthanh.api.FirebaseFireStoreAPI;
import com.dev.tuanteo.tuanamthanh.listener.ILocalSongClickListener;
import com.dev.tuanteo.tuanamthanh.units.SongUtils;

public class ListDetailSingerAdapter extends ListSuggestAdapter {

    public ListDetailSingerAdapter(Context context, ILocalSongClickListener listener, String singer) {
        super(context, listener, singer);
    }

    @Override
    protected void getAndUpdateListSong(String condition) {
        mListSuggestSong = SongUtils.getListArtistSong(condition);
        FirebaseFireStoreAPI.setListFindSong(mListSuggestSong);
        notifyDataSetChanged();
    }
}
