package com.dev.tuanteo.tuanamthanh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dev.tuanteo.tuanamthanh.adapter.MainPagerAdapter;
import com.dev.tuanteo.tuanamthanh.fragment.HomeFragment;
import com.dev.tuanteo.tuanamthanh.fragment.ListAllSongFragment;
import com.dev.tuanteo.tuanamthanh.fragment.MediaPlayControlFragment;
import com.dev.tuanteo.tuanamthanh.listener.ILocalSongClickListener;
import com.dev.tuanteo.tuanamthanh.object.Song;
import com.dev.tuanteo.tuanamthanh.service.MediaPlayService;
import com.dev.tuanteo.tuanamthanh.units.Constant;
import com.dev.tuanteo.tuanamthanh.units.LogUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements ILocalSongClickListener {

    private ViewPager2 mMainViewPager;
    private TabLayout mMainTabView;
    private View mMainPlayerController;

    private MediaPlayService mMediaService;
    private boolean mIsBoundMediaService;

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mMediaServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayService.BoundService binder = (MediaPlayService.BoundService) service;
            mMediaService = binder.getService();
            mIsBoundMediaService = true;
            LogUtils.log("mMediaServiceConnection onServiceConnected: ");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mIsBoundMediaService = false;
            LogUtils.log("mMediaServiceConnection onServiceDisconnected: ");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*TuanTeo: Show toolbar */
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        /*TuanTeo: Khoi tao cac thanh phan cua view chinh */
        initComponent();

        // TODO: 11/16/2021 Xu ly logic xin quyen hẳn hoi
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
        }
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

        mMainPlayerController = findViewById(R.id.main_player_control);
        mMainPlayerController.setOnClickListener(v ->
                showMediaPlayerControlFragment());
    }

    @Override
    protected void onStart() {
        super.onStart();

        //TuanTeo: Bind to MediaPlayService if Service is Running
        if (isServiceRunning(MediaPlayService.class)) {
            bindMediaPlayService();
        }
    }

    private List<Fragment> initListFragments() {
        List<Fragment> listFragment = new ArrayList<>();
        listFragment.add(new HomeFragment(getApplicationContext()));
        listFragment.add(new ListAllSongFragment(getApplicationContext(), this));
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
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();

        /*TuanTeo: Unbind MediaPlayService */
        if (mIsBoundMediaService) {
            unbindService(mMediaServiceConnection);
            mIsBoundMediaService = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        /*TuanTeo: stop Service */
        stopMediaPlayService();
    }

    @Override
    public void playSong(Song song) {
        if (!mIsBoundMediaService && mMediaService == null) {
            if (mMainPlayerController.getVisibility() == View.GONE) {
                mMainPlayerController.setVisibility(View.VISIBLE);
            }

            // TODO: 11/16/2021 Chạy foreground Servive choi nhac
            startMediaPlayService(song.getId(), song.getPath());
            bindMediaPlayService();
        } else {
            /*TuanTeo: Neu bind service rồi thì chạy bài hát được chọn */
            mMediaService.playSong(song);
        }
    }

    /**
     * Bind MediaPlayService
     */
    private void bindMediaPlayService() {
        Intent intent = new Intent(getApplicationContext(), MediaPlayService.class);
        bindService(intent, mMediaServiceConnection, BIND_AUTO_CREATE);
    }

    /**
     * Start MediaPlayService
     * @param songId        song id to start the first time
     * @param songPath      song path to start the first time
     */
    private void startMediaPlayService(long songId, String songPath) {
        Intent serviceIntent = new Intent(this, MediaPlayService.class);
        serviceIntent.putExtra(Constant.SONG_ID_TO_START_SERVICE, songId);
        serviceIntent.putExtra(Constant.SONG_PATH_START_SERVICE, songPath);
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    /**
     * Stop MediaPlayService
     */
    public void stopMediaPlayService() {
        Intent serviceIntent = new Intent(getApplicationContext(), MediaPlayService.class);
        stopService(serviceIntent);
    }

    /**
     * Ham check Service có đang hoạt động hay không
     * @param serviceClass  service muốn kiểm tra
     * @return  boolean-true nếu đang hoạt động, false thì ngược lại
     */
    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (Objects.equals(serviceClass.getName(), service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}