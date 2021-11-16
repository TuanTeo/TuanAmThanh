package com.dev.tuanteo.tuanamthanh.adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dev.tuanteo.tuanamthanh.object.Song;

import java.util.List;

public class ListSongAdapter<V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter {

    protected final Context mContext;
    protected List<Song> mListSong;

    public ListSongAdapter(Context context, List<Song> listSong) {
        mContext = context;
        mListSong = listSong;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        if (mListSong != null) {
            return mListSong.size();
        } else {
            return 15;
        }
    }
}
