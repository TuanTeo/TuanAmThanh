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

import com.dev.tuanteo.tuanamthanh.MainActivity;
import com.dev.tuanteo.tuanamthanh.R;
import com.dev.tuanteo.tuanamthanh.adapter.ListDetailAdapter;
import com.dev.tuanteo.tuanamthanh.listener.ILocalSongClickListener;

import javax.annotation.Nonnull;

public class DetailSongFragment extends Fragment {

    private Context mContext;
    private String mCategory;
    private String mAvatar;
    private ILocalSongClickListener mListener;

    private RecyclerView mDetailRecyclerView;

    public DetailSongFragment(Context context, String category, String avatar, ILocalSongClickListener listener) {
        mContext = context;
        mCategory = category;
        mAvatar = avatar;
        mListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull @Nonnull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_song_detail, container, false);

        LinearLayoutManager suggestLayoutManager
                = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mDetailRecyclerView = view.findViewById(R.id.song_detail_recycler_view);
        mDetailRecyclerView.setAdapter(new ListDetailAdapter(mContext,
                (MainActivity) getActivity(), mCategory));
        mDetailRecyclerView.setLayoutManager(suggestLayoutManager);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mListener.distroyDetailFragment();
    }
}
