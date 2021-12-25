package com.dev.tuanteo.tuanamthanh.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.dev.tuanteo.tuanamthanh.listener.RecyclerAdapterListener;
import com.dev.tuanteo.tuanamthanh.listener.HomeFragmentListener;

import javax.annotation.Nonnull;

public class HomeFragment extends Fragment implements RecyclerAdapterListener {

    private Context mContext;
    private RecyclerView mCategoryRecyclerView;
    private RecyclerView mSingerRecyclerView;
    private RecyclerView mSuggestRecyclerView;

    private HomeFragmentListener mListener;

    public HomeFragment(Context context, HomeFragmentListener listener) {
        this.mContext = context;
        mListener = listener;
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
        mCategoryRecyclerView.setAdapter(new ListCategoryAdapter(mContext, this));
        mCategoryRecyclerView.setLayoutManager(categoryLayoutManager);
        mCategoryRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                int action = e.getAction();
                switch (action) {
                    case MotionEvent.ACTION_MOVE:
                        rv.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull @Nonnull RecyclerView rv, @NonNull @Nonnull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        /*TuanTeo: Singer RecyclerView */
        LinearLayoutManager singerLayoutManager
                = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mSingerRecyclerView = view.findViewById(R.id.singer_recycler_view);
        mSingerRecyclerView.setAdapter(new ListSingerAdapter(mContext, this));
        mSingerRecyclerView.setLayoutManager(singerLayoutManager);
        mSingerRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                int action = e.getAction();
                switch (action) {
                    case MotionEvent.ACTION_MOVE:
                        rv.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull @Nonnull RecyclerView rv, @NonNull @Nonnull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        LinearLayoutManager suggestLayoutManager
                = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mSuggestRecyclerView = view.findViewById(R.id.suggestion_recycler_view);
        mSuggestRecyclerView.setAdapter(new ListSuggestAdapter(mContext, (MainActivity) getActivity()));
        mSuggestRecyclerView.setLayoutManager(suggestLayoutManager);
        mSuggestRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                int action = e.getAction();
                switch (action) {
                    case MotionEvent.ACTION_MOVE:
                        rv.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull @Nonnull RecyclerView rv, @NonNull @Nonnull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    @Override
    public void openListCategorySong(String category, String avatar) {
        mListener.openCategoryFragmentDetail(category, avatar);
    }

    @Override
    public void openListArtistSong(String singer, String avatar) {
        mListener.openArtistFragmentDetail(singer, avatar);
    }
}
