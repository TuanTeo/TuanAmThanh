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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

    /*TuanTeo: View cho controller */
    private View mMainPlayerController;
    private TextView mSongNameController;
    private TextView mSingerNameController;
    private ImageButton mPlayPauseButtonController;
    private ImageButton mPreviousButtonController;
    private ImageButton mNextButtonController;

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

        /*TuanTeo: Khởi tạo giá trị cho thanh điều khiển nhạc */
        initMainControllerComponent();
    }

    /**
     * Các thành phần của MainPlayerController
     */
    private void initMainControllerComponent() {
        mSongNameController = findViewById(R.id.main_player_control_song_name);
        mSongNameController.setSelected(true);

        mSingerNameController = findViewById(R.id.main_player_control_singer_name);

        /*TuanTeo: Nut play/pause giao diện controller */
        mPlayPauseButtonController = findViewById(R.id.main_player_control_pause_button);
        mPlayPauseButtonController.setOnClickListener(view -> {
            if (mMediaService != null) {
                if (mMediaService.isPlayingMusic()) {
                    mPlayPauseButtonController.setImageResource(R.drawable.ic_play_circle_controler);
                } else {
                    mPlayPauseButtonController.setImageResource(R.drawable.ic_pause_circle_controler);
                }
                mMediaService.pauseOrResumeMusic();
            }
        });

        // TODO: 11/20/2021 Set sự kiện cho nút next prev
        mPreviousButtonController = findViewById(R.id.main_player_control_previous_button);
        mPreviousButtonController.setOnClickListener(view -> {
            mMediaService.previousMusic();
            updateUIMainPlayerController(mMediaService.getCurrentPlaySong());
        });
        mNextButtonController = findViewById(R.id.main_player_control_next_button);
        mNextButtonController.setOnClickListener(view -> {
            mMediaService.nextMusic();
            updateUIMainPlayerController(mMediaService.getCurrentPlaySong());
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        //TuanTeo: Bind to MediaPlayService if Service is Running
        if (isServiceRunning(MediaPlayService.class)) {
            bindMediaPlayService();
        }
    }

    /**
     * Danh sách các Fragment hiển thị trên view chính
     * @return
     */
    private List<Fragment> initListFragments() {
        List<Fragment> listFragment = new ArrayList<>();
        listFragment.add(new HomeFragment(getApplicationContext()));
        listFragment.add(new ListAllSongFragment(getApplicationContext(), this));
        return listFragment;
    }

    /**
     * Hiển thị giao diện MainPlayerController
     */
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
        /*TuanTeo: Cập nhật UI trên giao diên điều khiển nhạc */
        updateUIMainPlayerController(song);

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
     * Cập nhật giao diện điều khiển nhạc mini
     * @param song bài hát đang phát
     */
    private void updateUIMainPlayerController(Song song) {
        LogUtils.log("updateUIMainPlayerController " + song.getName());
        mSongNameController.setText(song.getName());
        mSingerNameController.setText(song.getArtist());
        mPlayPauseButtonController.setImageResource(R.drawable.ic_pause_circle_controler);
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
    private void startMediaPlayService(String songId, String songPath) {
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