package com.dev.tuanteo.tuanamthanh.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dev.tuanteo.tuanamthanh.R;
import com.dev.tuanteo.tuanamthanh.adapter.ListFavoriteSongAdapter;
import com.dev.tuanteo.tuanamthanh.listener.IFavoriteFragmentListener;
import com.dev.tuanteo.tuanamthanh.object.Song;
import com.dev.tuanteo.tuanamthanh.units.SongUtils;

import java.util.ArrayList;

import javax.annotation.Nonnull;


public class FavoriteSongFragment extends Fragment {

    private Context mContext;
    private ArrayList<Song> mListFavoriteSong;
    private IFavoriteFragmentListener mListener;

    private RecyclerView mListSongRecyclerView;
    private ListFavoriteSongAdapter mFavoriteSongAdapter;

    public FavoriteSongFragment(Context context, IFavoriteFragmentListener listener) {
        mContext = context;
        mListener = listener;
        mListFavoriteSong = SongUtils.getListFavoriteSong(mContext);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull @Nonnull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.list_all_song_fragment, container, false);

        initViewComponent(view);
        return view;
    }

    private void initViewComponent(View view) {
        mListSongRecyclerView = view.findViewById(R.id.list_all_song_recycler_view);
        mFavoriteSongAdapter = new ListFavoriteSongAdapter(mContext);
        mListSongRecyclerView.setAdapter(mFavoriteSongAdapter);
        mListSongRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mListener != null) {
            mListener = null;
        }
    }

    public void updateUI() {
        if (mFavoriteSongAdapter != null) {
            mFavoriteSongAdapter.updateListLocalSong();
        }
    }
}
