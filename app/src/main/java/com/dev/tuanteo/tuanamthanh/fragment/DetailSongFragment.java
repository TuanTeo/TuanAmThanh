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
import com.dev.tuanteo.tuanamthanh.adapter.ListDetailCategoryAdapter;
import com.dev.tuanteo.tuanamthanh.adapter.ListDetailSingerAdapter;
import com.dev.tuanteo.tuanamthanh.listener.ILocalSongClickListener;

import javax.annotation.Nonnull;

public class DetailSongFragment extends Fragment {

    public static int DETAIL_CATEGORY_TYPE = 1;
    public static int DETAIL_SINGER_TYPE = 2;

    private Context mContext;
    private String mCondition;
    private String mAvatar;
    private ILocalSongClickListener mListener;
    private int mDetailType = 0;
    private RecyclerView mDetailRecyclerView;

    public DetailSongFragment(Context context, String condition, String avatar, ILocalSongClickListener listener, int type) {
        mContext = context;
        mCondition = condition;
        mAvatar = avatar;
        mListener = listener;
        mDetailType = type;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull @Nonnull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_song_detail, container, false);

        LinearLayoutManager suggestLayoutManager
                = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mDetailRecyclerView = view.findViewById(R.id.song_detail_recycler_view);

        /*TuanTeo: Hiển thị adapter phù hợp với kiểu truyền vào */
        if (DETAIL_CATEGORY_TYPE == mDetailType) {
            mDetailRecyclerView.setAdapter(new ListDetailCategoryAdapter(mContext,
                    (MainActivity) getActivity(), mCondition));
        } else if (DETAIL_SINGER_TYPE == mDetailType) {
            mDetailRecyclerView.setAdapter(new ListDetailSingerAdapter(mContext,
                    (MainActivity) getActivity(), mCondition));
        }
        mDetailRecyclerView.setLayoutManager(suggestLayoutManager);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mListener.distroyDetailFragment();
    }
}
