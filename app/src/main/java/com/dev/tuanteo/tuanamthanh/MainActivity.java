package com.dev.tuanteo.tuanamthanh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dev.tuanteo.tuanamthanh.adapter.MainPagerAdapter;
import com.dev.tuanteo.tuanamthanh.adapter.SearchAdapter;
import com.dev.tuanteo.tuanamthanh.api.FirebaseFireStoreAPI;
import com.dev.tuanteo.tuanamthanh.fragment.DetailSongFragment;
import com.dev.tuanteo.tuanamthanh.fragment.FavoriteSongFragment;
import com.dev.tuanteo.tuanamthanh.fragment.HomeFragment;
import com.dev.tuanteo.tuanamthanh.fragment.ListAllSongFragment;
import com.dev.tuanteo.tuanamthanh.fragment.MediaPlayControlFragment;
import com.dev.tuanteo.tuanamthanh.listener.HomeFragmentListener;
import com.dev.tuanteo.tuanamthanh.listener.IFavoriteFragmentListener;
import com.dev.tuanteo.tuanamthanh.listener.ILocalSongClickListener;
import com.dev.tuanteo.tuanamthanh.listener.MediaControlListener;
import com.dev.tuanteo.tuanamthanh.object.Song;
import com.dev.tuanteo.tuanamthanh.service.MediaPlayService;
import com.dev.tuanteo.tuanamthanh.units.Constant;
import com.dev.tuanteo.tuanamthanh.units.LogUtils;
import com.dev.tuanteo.tuanamthanh.units.SongUtils;
import com.dev.tuanteo.tuanamthanh.units.Utils;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements ILocalSongClickListener,
        HomeFragmentListener, IFavoriteFragmentListener, MediaControlListener {

    private ViewPager2 mMainViewPager;
    private TabLayout mMainTabView;

    /*TuanTeo: View cho controller */
    private View mMainPlayerController;
    private TextView mSongNameController;
    private TextView mSingerNameController;
    private ImageView mSongImageController;
    private ImageButton mPlayPauseButtonController;
    private ImageButton mPreviousButtonController;
    private ImageButton mNextButtonController;

    private MediaPlayService mMediaService;
    private boolean mIsBoundMediaService;

    private MediaPlayControlFragment mMediaPlayControlFragment;

    /** Defines callbacks for service binding, passed to bindService() */
    private final ServiceConnection mMediaServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayService.BoundService binder = (MediaPlayService.BoundService) service;
            mMediaService = binder.getService();
            mIsBoundMediaService = true;
            LogUtils.log("mMediaServiceConnection onServiceConnected: ");

            /*TuanTeo: Cap nhat lai UI khi bind service luc mo lai app */
            if (mMediaService.isPlayingMusic()) {
                updateUIMainPlayerController(mMediaService.getCurrentPlaySong());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mIsBoundMediaService = false;
            LogUtils.log("mMediaServiceConnection onServiceDisconnected: ");
        }
    };

    /**
     * BroadcastReceiver for update UI from service
     */
    private final BroadcastReceiver mUpdateUIReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.log("mUpdateUIReceive updateUI");

            String songName = intent.getStringExtra(Constant.SONG_NAME_TO_UPDATE_UI);
            String singerName = intent.getStringExtra(Constant.SINGER_NAME_TO_UPDATE_UI);
            String songImage = intent.getStringExtra(Constant.SONG_IMAGE_TO_UPDATE_UI);

            mSongNameController.setText(songName);
            mSingerNameController.setText(singerName);
            if (mMediaService.isPlayingMusic()) {
                mPlayPauseButtonController.setImageResource(R.drawable.ic_pause_circle_controler);
            } else {
                mPlayPauseButtonController.setImageResource(R.drawable.ic_play_circle_controler);
            }
            Glide.with(getApplicationContext())
                    .load(songImage)
                    .placeholder(R.drawable.music_note)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .into(mSongImageController);

            /*TuanTeo: Cập nhật cả UI trên MediaPlayControlFragment */
            if (mMediaPlayControlFragment != null) {
                mMediaPlayControlFragment.updateUI();
            }
        }
    };

    /*TuanTeo: Search view */
    private SearchView mSearchView;
    private RecyclerView mSearchRV;

    private ListAllSongFragment mListLocalSongFragment;
    private HomeFragment mHomeFragment;
    private FavoriteSongFragment mFavoriteSongFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*TuanTeo: Show toolbar */
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        /*TuanTeo: Khoi tao cac thanh phan cua view chinh */
        initComponent();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
        }

        /*TuanTeo: Load danh sách tất cả bài hát online */
        FirebaseFireStoreAPI.loadListAllSong();
    }

    private void initComponent() {
        /*TuanTeo: Danh sach cac pager tren view chinh */
        List<Fragment> listFragments = initListFragments();
        mMainViewPager = findViewById(R.id.view_pager);
        mMainViewPager.setAdapter(new MainPagerAdapter(this, listFragments));
        /*TuanTeo: Khong cho vuot de chuyen pager */
        mMainViewPager.setUserInputEnabled(true);

        mMainTabView = findViewById(R.id.tab_layout);
        new TabLayoutMediator(mMainTabView, mMainViewPager, (tab, position) -> {
            String tabName;
            int icon;
            if (position == 0) {
                tabName = "Home";
                icon = R.drawable.ic_home;
            } else if (position == 1) {
                tabName = "Library";
                icon = R.drawable.ic_library_music;
            } else {
                icon = R.drawable.ic_favorite_tab;
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

        mSongImageController = findViewById(R.id.main_player_control_song_image);

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

        /*TuanTeo: Dang ky lang nghe broadcast */
        registerReceiver(mUpdateUIReceive, new IntentFilter(Constant.ACTION_UPDATE_UI));
    }

    /**
     * Danh sách các Fragment hiển thị trên view chính
     * @return
     */
    private List<Fragment> initListFragments() {
        mHomeFragment = new HomeFragment(getApplicationContext(), this);
        mListLocalSongFragment = new ListAllSongFragment(getApplicationContext(), this);
        mFavoriteSongFragment = new FavoriteSongFragment(getApplicationContext(), this);

        List<Fragment> listFragment = new ArrayList<>();
        listFragment.add(mHomeFragment);
        listFragment.add(mListLocalSongFragment);
        listFragment.add(mFavoriteSongFragment);
        return listFragment;
    }

    /**
     * Hiển thị giao diện MainPlayerController
     */
    private void showMediaPlayerControlFragment() {
        mMediaPlayControlFragment = new MediaPlayControlFragment(getApplicationContext(), mMediaService, this);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_frame_layout, mMediaPlayControlFragment)
                .addToBackStack(null)
                .commit();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem myActionMenuItem = menu.findItem( R.id.app_bar_search);
        mSearchView = (SearchView) myActionMenuItem.getActionView();
        mSearchRV = findViewById(R.id.search_result_recycler_view);

        SearchAdapter searchAdapter = new SearchAdapter(getApplicationContext(), new ArrayList<>(), this);
        mSearchRV.setAdapter(searchAdapter);
        mSearchRV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        mSearchView.setOnSearchClickListener(v -> {
            mSearchRV.setVisibility(View.VISIBLE);

            ArrayList<Song> resultList = new ArrayList<>();
            searchAdapter.setListSong(resultList);
            searchAdapter.notifyDataSetChanged();

            /*TuanTeo: Ẩn controller */
            mMainPlayerController.setVisibility(View.GONE);
            mMainTabView.setVisibility(View.GONE);
        });

        mSearchView.setOnCloseListener(() -> {
            mSearchRV.setVisibility(View.GONE);

            /*TuanTeo: Hiển thị lại controller */
            mMainPlayerController.setVisibility(View.VISIBLE);
            mMainTabView.setVisibility(View.VISIBLE);
            return false;
        });

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                myActionMenuItem.collapseActionView();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                newText = newText.toLowerCase();

                ArrayList<Song> resultList = new ArrayList<>();

                if (!TextUtils.isEmpty(newText)) {
                    for (Song song : SongUtils.getListAllSong(getApplicationContext())) {
                        String title = song.getName().toLowerCase();
                        if (title.contains(newText)) {
                            resultList.add(song);
                        }
                    }
                }

                searchAdapter.setListSong(resultList);
                searchAdapter.notifyDataSetChanged();

                return false;
            }
        });
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

        /*TuanTeo: Huy lắng nghe su kien UpdateUI tu Service */
        unregisterReceiver(mUpdateUIReceive);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        /*TuanTeo: stop Service */
        stopMediaPlayService();
    }

    @Override
    public void playSong(Song song, boolean isOnline, boolean isSuggestList) {
        /*TuanTeo: Cập nhật UI trên giao diên điều khiển nhạc */
        updateUIMainPlayerController(song);

        /*TuanTeo: Nghe nhạc online cần kiểm tra trạng thái mạng */
        if (isOnline && !Utils.isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "Kết nối mạng và thử lại", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!mIsBoundMediaService && mMediaService == null) {
            if (mMainPlayerController.getVisibility() == View.GONE) {
                mMainPlayerController.setVisibility(View.VISIBLE);
            }

            startMediaPlayService(isOnline, song.getId(), song.getPath(), song.getImage());
            bindMediaPlayService();
        } else {
            /*TuanTeo: Neu bind service rồi thì chạy bài hát được chọn */
            mMediaService.playSong(song, true);
        }
    }

    @Override
    public void distroyDetailFragment() {
        findViewById(R.id.main_frame_container).setVisibility(View.GONE);
    }

    @Override
    public void updateListLocalSong() {
        mListLocalSongFragment.updateUI();
        mFavoriteSongFragment.updateUI();
    }

    /**
     * Cập nhật giao diện điều khiển nhạc mini
     * @param song bài hát đang phát
     */
    private void updateUIMainPlayerController(Song song) {
        LogUtils.log("updateUIMainPlayerController " + song.getName());
        mSongNameController.setText(song.getName());
        mSingerNameController.setText(song.getArtist());
        Glide.with(getApplicationContext())
                .load(song.getImage())
                .placeholder(R.drawable.music_note)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(mSongImageController);
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
    private void startMediaPlayService(boolean isOnline, String songId, String songPath, String songImage) {
        Intent serviceIntent = new Intent(this, MediaPlayService.class);
        serviceIntent.putExtra(Constant.SONG_ID_TO_START_SERVICE, songId);
        serviceIntent.putExtra(Constant.SONG_PATH_START_SERVICE, songPath);
        serviceIntent.putExtra(Constant.SONG_IMAGE_START_SERVICE, songImage);
        serviceIntent.putExtra(Constant.IS_ONLINE_LIST, isOnline);
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

    @Override
    public void openCategoryFragmentDetail(String category, String avatar) {

        findViewById(R.id.main_frame_container).setVisibility(View.VISIBLE);

        DetailSongFragment detailSongFragment = new DetailSongFragment(getApplicationContext(),
                category, avatar, this, DetailSongFragment.DETAIL_CATEGORY_TYPE);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_frame_container, detailSongFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void openArtistFragmentDetail(String singer, String avatar) {
        findViewById(R.id.main_frame_container).setVisibility(View.VISIBLE);

        DetailSongFragment detailSongFragment = new DetailSongFragment(getApplicationContext(),
                singer, avatar, this, DetailSongFragment.DETAIL_SINGER_TYPE);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_frame_container, detailSongFragment)
                .addToBackStack(null)
                .commit();
    }

    /*TuanTeo: Bien dung cho back 2 lan moi thoat app */
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (findViewById(R.id.main_frame_container).getVisibility() == View.VISIBLE) {
            super.onBackPressed();
            return;
        }

        if (!mSearchView.isIconified()) {
            mSearchView.setIconified(true);
            mSearchView.onActionViewCollapsed();
            mSearchRV.setVisibility(View.GONE);

            /*TuanTeo: Hiển thị lại controller */
            mMainPlayerController.setVisibility(View.VISIBLE);
            mMainTabView.setVisibility(View.VISIBLE);
            return;
        }

        if (mMediaPlayControlFragment != null && mMediaPlayControlFragment.isIsDisplaying()) {
            super.onBackPressed();
            return;
        }

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getResources().getString(R.string.confirm_exit), Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    public void updateListFavoriteSong() {
        mFavoriteSongFragment.updateUI();
    }
}