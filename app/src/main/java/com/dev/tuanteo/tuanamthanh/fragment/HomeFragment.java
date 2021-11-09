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
import com.dev.tuanteo.tuanamthanh.adapter.ListCategoryAdapter;

public class HomeFragment extends Fragment {

    private Context mContext;
    private RecyclerView mCategoryRecyclerView;

    public HomeFragment(Context context) {
        this.mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.home_pager, container, false);

        initComponent(view);

        return view;
    }

    // TODO: 11/8/2021 Dang fake du lieu
    @SuppressLint("VisibleForTests")
    private void initComponent(View view) {
        mCategoryRecyclerView = view.findViewById(R.id.category_recycler_view);
        mCategoryRecyclerView.setAdapter(new ListCategoryAdapter(mContext));

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mCategoryRecyclerView.setLayoutManager(layoutManager);
    }
}
