package com.dev.tuanteo.tuanamthanh.fragment;

import android.annotation.SuppressLint;
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
import com.dev.tuanteo.tuanamthanh.adapter.ListSongAdapter;

public class ListAllSongFragment extends Fragment {


    private final Context mContext;
    private RecyclerView mListSongRecyclerView;

    public ListAllSongFragment(Context context) {
        this.mContext = context;
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

    // TODO: 11/8/2021 Dung Adapter xịn
    @SuppressLint("VisibleForTests")
    private void initViewComponent(View view) {
        mListSongRecyclerView = view.findViewById(R.id.list_all_song_recycler_view);
        mListSongRecyclerView.setAdapter(new ListLocalSongAdapter(mContext));
        mListSongRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    }
}
