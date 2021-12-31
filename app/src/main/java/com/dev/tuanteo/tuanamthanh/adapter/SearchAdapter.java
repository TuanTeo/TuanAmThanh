package com.dev.tuanteo.tuanamthanh.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.dev.tuanteo.tuanamthanh.R;
import com.dev.tuanteo.tuanamthanh.listener.ILocalSongClickListener;
import com.dev.tuanteo.tuanamthanh.object.Song;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

public class SearchAdapter extends ListLocalSongAdapter {

    public SearchAdapter(Context context, List<Song> listSong, ILocalSongClickListener listener) {
        super(context, listSong, listener);
    }

    @NonNull
    @Nonnull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @Nonnull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.search_song_item, parent, false);
        return new ViewHolder(view);
    }

    public void setListSong(ArrayList<Song> listResult) {
        mListSong = listResult;
    }
}
