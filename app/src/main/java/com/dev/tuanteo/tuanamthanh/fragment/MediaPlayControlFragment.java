package com.dev.tuanteo.tuanamthanh.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dev.tuanteo.tuanamthanh.R;

public class MediaPlayControlFragment extends Fragment {

    private final Context mContext;

    public MediaPlayControlFragment(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return LayoutInflater.from(mContext)
                        .inflate(R.layout.media_player_view, container, false);
    }
}
