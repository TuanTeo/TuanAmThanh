package com.dev.tuanteo.tuanamthanh.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class MainPagerAdapter extends FragmentStateAdapter {

    private List<Fragment> mListFragments;

    public MainPagerAdapter(@NonNull FragmentActivity fragmentActivity,
                            List<Fragment> listFragments) {
        super(fragmentActivity);
        mListFragments = listFragments;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return mListFragments.get(position);
    }

    @Override
    public int getItemCount() {
        return mListFragments.size();
    }
}
