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
import com.dev.tuanteo.tuanamthanh.adapter.ListLocalSongAdapter;
import com.dev.tuanteo.tuanamthanh.listener.ILocalSongClickListener;
import com.dev.tuanteo.tuanamthanh.units.SongUtils;

public class ListAllSongFragment extends Fragment {


    private final Context mContext;
    private RecyclerView mListSongRecyclerView;
    private ILocalSongClickListener mItemClickListener;

    public ListAllSongFragment(Context context, ILocalSongClickListener listener) {
        this.mContext = context;
        mItemClickListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.list_all_song_fragment, container, false);

        initViewComponent(view);
        return view;
    }

    private void initViewComponent(View view) {
        mListSongRecyclerView = view.findViewById(R.id.list_all_song_recycler_view);
        ListLocalSongAdapter localSongAdapter = new ListLocalSongAdapter(mContext,
                    SongUtils.getListLocalSong(mContext), mItemClickListener);
        mListSongRecyclerView.setAdapter(localSongAdapter);
        mListSongRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mItemClickListener != null) {
            mItemClickListener = null;
        }
    }
}
