package com.dev.tuanteo.tuanamthanh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dev.tuanteo.tuanamthanh.adapter.MainPagerAdapter;
import com.dev.tuanteo.tuanamthanh.databinding.ActivityMainBinding;
import com.dev.tuanteo.tuanamthanh.databinding.MediaPlayerViewBinding;
import com.dev.tuanteo.tuanamthanh.fragment.HomeFragment;
import com.dev.tuanteo.tuanamthanh.fragment.ListAllSongFragment;
import com.dev.tuanteo.tuanamthanh.fragment.MediaPlayControlFragment;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mBinding;
    private MediaPlayerViewBinding mMediaPlayerViewBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*TuanTeo: Get ActivityMainBinding */
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        /*TuanTeo: Show toolbar */
        setSupportActionBar(mBinding.toolBar);

        initComponent();

        View view = mBinding.getRoot();
        setContentView(view);
    }

    private void initComponent() {

        List<Fragment> listFragments = initListFragments();
        mBinding.viewPager.setAdapter(new MainPagerAdapter(this, listFragments));

         new TabLayoutMediator(mBinding.tabLayout, mBinding.viewPager, (tab, position) -> {
            String tabName;
            int icon;
            if (position == 0) {
                tabName = "Home";
                icon = R.drawable.ic_home;
            } else {
                tabName = "Active";
                icon = R.drawable.ic_library_music;
            }
//            tab.setText(tabName);
            tab.setIcon(icon);
        }).attach();

        // TODO: 11/6/2021 tạo fragment player controler
        mBinding.mainPlayerControl.setOnClickListener(v ->
                showMediaPlayerControlFragment());
    }

    private List<Fragment> initListFragments() {
        List<Fragment> listFragment = new ArrayList<>();
        listFragment.add(new HomeFragment(getApplicationContext()));
        listFragment.add(new ListAllSongFragment(getApplicationContext()));
        return listFragment;
    }

    private void showMediaPlayerControlFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(mBinding.mainFrameLayout.getId(),
                        new MediaPlayControlFragment(getApplicationContext()))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.settings) {
            if (mBinding.mainPlayerControl.getVisibility() == View.GONE) {
                mBinding.mainPlayerControl.setVisibility(View.VISIBLE);
            } else {
                mBinding.mainPlayerControl.setVisibility(View.GONE);
            }
        }

        return super.onOptionsItemSelected(item);
    }
}