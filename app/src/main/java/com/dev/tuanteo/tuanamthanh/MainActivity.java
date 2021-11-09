package com.dev.tuanteo.tuanamthanh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dev.tuanteo.tuanamthanh.adapter.MainPagerAdapter;
import com.dev.tuanteo.tuanamthanh.fragment.HomeFragment;
import com.dev.tuanteo.tuanamthanh.fragment.ListAllSongFragment;
import com.dev.tuanteo.tuanamthanh.fragment.MediaPlayControlFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 mMainViewPager;
    private TabLayout mMainTabView;
    private View mMainPlayerController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*TuanTeo: Show toolbar */
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        /*TuanTeo: Khoi tao cac thanh phan cua view chinh */
        initComponent();
    }

    private void initComponent() {
        /*TuanTeo: Danh sach cac pager tren view chinh */
        List<Fragment> listFragments = initListFragments();
        mMainViewPager = findViewById(R.id.view_pager);
        mMainViewPager.setAdapter(new MainPagerAdapter(this, listFragments));
        /*TuanTeo: Khong cho vuot de chuyen pager */
        mMainViewPager.setUserInputEnabled(false);

        mMainTabView = findViewById(R.id.tab_layout);
        new TabLayoutMediator(mMainTabView, mMainViewPager, (tab, position) -> {
            String tabName;
            int icon;
            if (position == 0) {
                tabName = "Home";
                icon = R.drawable.ic_home;
            } else {
                tabName = "Library";
                icon = R.drawable.ic_library_music;
            }
//            tab.setText(tabName);
            tab.setIcon(icon);
        }).attach();

        // TODO: 11/6/2021 tạo fragment player controler
        mMainPlayerController = findViewById(R.id.main_player_control);
        mMainPlayerController.setOnClickListener(v ->
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
                .replace(R.id.main_frame_layout,
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
            if (mMainPlayerController.getVisibility() == View.GONE) {
                mMainPlayerController.setVisibility(View.VISIBLE);
            } else {
                mMainPlayerController.setVisibility(View.GONE);
            }
        }

        return super.onOptionsItemSelected(item);
    }
}