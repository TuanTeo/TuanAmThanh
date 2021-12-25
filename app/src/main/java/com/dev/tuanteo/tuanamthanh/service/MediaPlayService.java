package com.dev.tuanteo.tuanamthanh.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.dev.tuanteo.tuanamthanh.MainActivity;
import com.dev.tuanteo.tuanamthanh.R;
import com.dev.tuanteo.tuanamthanh.api.FirebaseFireStoreAPI;
import com.dev.tuanteo.tuanamthanh.listener.IFirebaseListener;
import com.dev.tuanteo.tuanamthanh.object.Artist;
import com.dev.tuanteo.tuanamthanh.object.MusicCategory;
import com.dev.tuanteo.tuanamthanh.object.Song;
import com.dev.tuanteo.tuanamthanh.receiver.NotificationReceiver;
import com.dev.tuanteo.tuanamthanh.units.Constant;
import com.dev.tuanteo.tuanamthanh.units.LocalSongUtils;
import com.dev.tuanteo.tuanamthanh.units.LogUtils;
import com.dev.tuanteo.tuanamthanh.units.Utils;

import java.io.IOException;
import java.util.ArrayList;

public class MediaPlayService extends Service {

    private static final String MEDIA_CHANNEL_ID = "media channel id";
    private static final int NOTIFICATION_ID = 333;

    private final IBinder mBinder = new BoundService();
    private MediaPlayer mMediaPlayer;

    /*TuanTeo: Thông tin danh sách bài hát */
    private ArrayList<Song> mListPlaySong;
    private int mPlayIndex;
    private String mPlayingSongID;
    private String mPlayingSongPath;
    private boolean mIsOnlineList;

    /*TuanTeo: BroadcastReceiver lang nghe su kien cua Notification */
    final private BroadcastReceiver mNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getStringExtra(Constant.NOTIFICATION_ACTION_NAME);

            if (Constant.NOTIFICATION_PREVIOUS_ACTION.equals(action)) {
                /*TuanTeo: Bam nut previous tren notification */
                previousMusic();
            } else if (Constant.NOTIFICATION_NEXT_ACTION.equals(action)) {
                /*TuanTeo: Bam nut next tren notification */
                nextMusic();
            } else if (Constant.NOTIFICATION_PLAY_PAUSE_ACTION.equals(action)) {
                /*TuanTeo: Bam nut play/pause tren notification */
                pauseOrResumeMusic();
            }

            /*TuanTeo: Cap nhat lai giao dien Notificaton */
            sendBroadcastUpdateUI();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.log("MediaPlayService onCreate");

        /*TuanTeo: Dang ky lang nghe su kien tu Notification */
        registerReceiver(mNotificationReceiver, new IntentFilter(Constant.NOTIFICATION_ACTION));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.log("MediaPlayService onStartCommand");

        mPlayingSongID = intent.getStringExtra(Constant.SONG_ID_TO_START_SERVICE);
        mPlayingSongPath = intent.getStringExtra(Constant.SONG_PATH_START_SERVICE);
        mIsOnlineList = intent.getBooleanExtra(Constant.IS_ONLINE_LIST, false);

        /*TuanTeo: Khởi tạo danh sách bài hát ban đầu */
        if (mIsOnlineList) {
            mListPlaySong = FirebaseFireStoreAPI.getListFindSong();
            Utils.updateDurationForListSong(mListPlaySong);
        } else {
            new Thread(() -> mListPlaySong = LocalSongUtils.getListLocalSong(getApplicationContext())).start();
        }

        initComponent();

        /*TuanTeo: Tao Notification Channel */
        createNotificationChannel();

        /*TuanTeo: Start foreground service */
        startForeground(NOTIFICATION_ID, getMediaNotification());

        /*TuanTeo: Choi nhac */
        playMusic();

        return START_NOT_STICKY;
    }

    private void initComponent() {
        mMediaPlayer = new MediaPlayer();
    }

    private void playMusic() {
        LogUtils.log("MediaPlayService playMusic " + mPlayingSongPath);

        try {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(getApplicationContext(), Uri.parse(mPlayingSongPath));
            mMediaPlayer.prepare();
            mMediaPlayer.start();

            mMediaPlayer.setOnCompletionListener(mp -> autoNextMedia());
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*TuanTeo: Update lại vị trí bài hát đang chơi trong danh sách */
        updatePlaySongIndex();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    MEDIA_CHANNEL_ID,
                    "Media Player Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.log("MediaPlayService onDestroy");

        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }

        /*TuanTeo: Huy dang ky lang nghe su kien */
        unregisterReceiver(mNotificationReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.log("MediaPlayService onBind");

        return mBinder;
    }

    /**
     * Lay trang thai service dang phat nhac hay khong
     * @return boolean trang thai dang phát nhac
     */
    public boolean isPlayingMusic() {
        return mMediaPlayer.isPlaying();
    }

    /**
     * Class to bind MediaPlayService
     */
    public class BoundService extends Binder {
        public MediaPlayService getService() {
            return MediaPlayService.this;
        }
    }

    /**
     * Hàm phát 1 bài hát chỉ định
     * @param song bài hát chỉ định
     */
    public void playSong(Song song, boolean isOnline, boolean isNeedUpdate, boolean isSuggestList) {
        /*TuanTeo: Cập nhật lại danh sách bài hát nếu cần */
        if (isNeedUpdate) {
            mIsOnlineList = isOnline;
            updateListPlaySong(isSuggestList);
        }
        mPlayingSongID = song.getId();
        mPlayingSongPath = song.getPath();
        stopMusic();
        playMusic();
    }

    /**
     * Cập nhật lại danh sách bài hát tương ứng
     * @param isSuggestList     kiểm tra có phải danh sách bài hát gợi ý không
     */
    private void updateListPlaySong(boolean isSuggestList) {
        if (mIsOnlineList) {
            if (isSuggestList) {
                mListPlaySong = FirebaseFireStoreAPI.getListSuggestSong();
            } else {
                mListPlaySong = FirebaseFireStoreAPI.getListFindSong();
            }
            Utils.updateDurationForListSong(mListPlaySong);
        } else {
            new Thread(() -> mListPlaySong = LocalSongUtils.getListLocalSong(getApplicationContext())).start();
        }
    }

    /**
     * Tam dung phat nhac
     */
    private void stopMusic() {
        LogUtils.log("MediaPlayService stopMusic");
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        mMediaPlayer.reset();
    }

    /**
     * Ham goi khi bấm nút play/pause
     */
    public void pauseOrResumeMusic() {
        if (mMediaPlayer.isPlaying()) {
            pauseMusic();
        } else {
            resumeMusic();
        }
        sendBroadcastUpdateUI();
    }

    /**
     * Ham pause music
     */
    private void pauseMusic() {
        LogUtils.log("MediaPlayService pauseMusic");
        mMediaPlayer.pause();
    }

    /**
     * Ham resume music
     */
    private void resumeMusic() {
        LogUtils.log("MediaPlayService resumeMusic");
        mMediaPlayer.start();
    }

    /**
     * Next bài hát
     */
    public void nextMusic() {
        if (mPlayIndex == mListPlaySong.size() - 1) {
            mPlayIndex = 0;
        } else {
            ++mPlayIndex;
        }
        playSong(mListPlaySong.get(mPlayIndex), mIsOnlineList,false, false);
        LogUtils.log("nextMusic playSong index " + mPlayIndex);

        sendBroadcastUpdateUI();
    }

    /**
     * Lùi bài hát
     */
    public void previousMusic() {
        if (mPlayIndex == 0) {
            mPlayIndex = mListPlaySong.size() - 1;
        } else {
            --mPlayIndex;
        }
        playSong(mListPlaySong.get(mPlayIndex), mIsOnlineList, false, false);
        LogUtils.log("nextMusic playSong index " + mPlayIndex);

        sendBroadcastUpdateUI();
    }

    /**
     * Luồng update lại vị trí bài hát đang chơi trong danh sách
     */
    private void updatePlaySongIndex() {
        new Thread(()-> {
            for (int i=0; i< mListPlaySong.size(); i++) {
                if (mPlayingSongID.equals(mListPlaySong.get(i).getId())) {
                    mPlayIndex = i;
                    LogUtils.log("updatePlaySongIndex mPlayIndex = " + i);
                    break;
                }
            }

            /*TuanTeo: Cap nhat lai giao dien Notification */
            updateNotificationUI();
        }).start();
    }

    /**
     * Hàm trả về bài hát đang chơi (dùng cho việc update UI)
     * @return current play song
     */
    public Song getCurrentPlaySong() {
        return mListPlaySong.get(mPlayIndex);
    }

    private void autoNextMedia() {
        // TODO: 11/21/2021 them logic kiem tra co phat lai hay khong o day

        /*TuanTeo: Logic phát bài hát tiếp theo */
        nextMusic();
    }

    /**
     * Hàm gửi broadcast cập nhật UI trên MainActivity
     */
    private void sendBroadcastUpdateUI() {
        Song currentSong = mListPlaySong.get(mPlayIndex);

        Intent intent = new Intent(Constant.ACTION_UPDATE_UI);
        intent.putExtra(Constant.SONG_NAME_TO_START_SERVICE, currentSong.getName());
        intent.putExtra(Constant.SINGER_NAME_TO_START_SERVICE, currentSong.getArtist());
        intent.putExtra(Constant.SONG_IMAGE_TO_START_SERVICE, currentSong.getImage());

        sendBroadcast(intent);

        updateNotificationUI();
    }

    /**
     * Cap nhat giao dien tren notification
     */
    private void updateNotificationUI() {
        Notification notification = getMediaNotification();

        NotificationManager notificationManager =
                (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private synchronized Notification getMediaNotification() {
        /*TuanTeo: Cập nhật thông tin bài hát, trạng thái phát nhạc hiện tại */
        int playPauseImageId;
        String songName = "";
        String singerName = "";
        if (isPlayingMusic()) {
            playPauseImageId = R.drawable.ic_notification_pause;
        } else {
            playPauseImageId = R.drawable.ic_notification_play;
        }

        try {
            songName = getCurrentPlaySong().getName();
            singerName = getCurrentPlaySong().getArtist();
        } catch (NullPointerException exception) {
            exception.printStackTrace();
        }

        /*TuanTeo: Su kien khi click vào Notification */
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        /*TuanTeo: Su kien cho nút previous trên Notification */
        Intent previousIntent = new Intent(getApplicationContext(), NotificationReceiver.class)
                .setAction(Constant.NOTIFICATION_PREVIOUS_ACTION);
        PendingIntent previousPending = PendingIntent.getBroadcast(getApplicationContext(), 0,
                previousIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        /*TuanTeo: Su kien cho nút next trên Notification */
        Intent nextIntent = new Intent(getApplicationContext(), NotificationReceiver.class)
                .setAction(Constant.NOTIFICATION_NEXT_ACTION);
        PendingIntent nextPending = PendingIntent.getBroadcast(getApplicationContext(), 0,
                nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        /*TuanTeo: Su kien cho nút pause/play trên Notification */
        Intent playIntent = new Intent(getApplicationContext(), NotificationReceiver.class)
                .setAction(Constant.NOTIFICATION_PLAY_PAUSE_ACTION);
        PendingIntent playPending = PendingIntent.getBroadcast(getApplicationContext(), 0,
                playIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // TODO: 11/28/2021 Cap nhat hinh dai dien cua bai hat
        return new NotificationCompat.Builder(getApplicationContext(), MEDIA_CHANNEL_ID)
                .setContentTitle(songName)
                .setContentText(singerName)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.music_note))
                .addAction(R.drawable.ic_notification_skip_previous, "Previous", previousPending) // #0
                .addAction(playPauseImageId, "Pause", playPending) // #1
                .addAction(R.drawable.ic_notification_skip_next, "Next", nextPending) // #2
                .setContentIntent(pendingIntent)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2))
                .build();
    }


    /*TuanTeo: Lấy biến MediaPlayer */
    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }
}
