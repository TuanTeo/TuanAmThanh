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

import com.dev.tuanteo.tuanamthanh.MainActivity;
import com.dev.tuanteo.tuanamthanh.R;
import com.dev.tuanteo.tuanamthanh.adapter.ListCategoryAdapter;
import com.dev.tuanteo.tuanamthanh.adapter.ListSingerAdapter;
import com.dev.tuanteo.tuanamthanh.adapter.ListSuggestAdapter;

public class HomeFragment extends Fragment {

    private Context mContext;
    private RecyclerView mCategoryRecyclerView;
    private RecyclerView mSingerRecyclerView;
    private RecyclerView mSuggestRecyclerView;

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

    private void initComponent(View view) {
        /*TuanTeo: Category RecyclerView */
        LinearLayoutManager categoryLayoutManager
                = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mCategoryRecyclerView = view.findViewById(R.id.category_recycler_view);
        mCategoryRecyclerView.setAdapter(new ListCategoryAdapter(mContext));
        mCategoryRecyclerView.setLayoutManager(categoryLayoutManager);

        /*TuanTeo: Singer RecyclerView */
        LinearLayoutManager singerLayoutManager
                = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mSingerRecyclerView = view.findViewById(R.id.singer_recycler_view);
        mSingerRecyclerView.setAdapter(new ListSingerAdapter(mContext));
        mSingerRecyclerView.setLayoutManager(singerLayoutManager);

        LinearLayoutManager suggestLayoutManager
                = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mSuggestRecyclerView = view.findViewById(R.id.suggestion_recycler_view);
        mSuggestRecyclerView.setAdapter(new ListSuggestAdapter(mContext, (MainActivity) getActivity()));
        mSuggestRecyclerView.setLayoutManager(suggestLayoutManager);
    }
}
