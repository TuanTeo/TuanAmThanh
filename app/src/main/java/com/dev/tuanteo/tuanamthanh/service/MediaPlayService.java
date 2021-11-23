package com.dev.tuanteo.tuanamthanh.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
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
import com.dev.tuanteo.tuanamthanh.object.Song;
import com.dev.tuanteo.tuanamthanh.units.Constant;
import com.dev.tuanteo.tuanamthanh.units.LocalSongUtils;
import com.dev.tuanteo.tuanamthanh.units.LogUtils;

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

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.log("MediaPlayService onCreate");
        new Thread(() -> mListPlaySong = LocalSongUtils.getListLocalSong(getApplicationContext())).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.log("MediaPlayService onStartCommand");

        mPlayingSongID = intent.getStringExtra(Constant.SONG_ID_TO_START_SERVICE);
        mPlayingSongPath = intent.getStringExtra(Constant.SONG_PATH_START_SERVICE);

        initComponent();

        /*TuanTeo: Tao Notification Channel */
        createNotificationChannel();

        // TODO: 11/16/2021 Hien thi giao dien choi nhac khi click vao notification
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), MEDIA_CHANNEL_ID)
                .setContentTitle("Song Path "  + mPlayingSongPath)
                .setContentText("Play song id " + mPlayingSongID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();

        /*TuanTeo: Start foreground service */
        startForeground(NOTIFICATION_ID, notification);

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
    public void playSong(Song song) {
        mPlayingSongID = song.getId();
        mPlayingSongPath = song.getPath();
        stopMusic();
        playMusic();
    }

    /**
     * Tam dung phat nhac
     */
    private void stopMusic() {
        // TODO: 11/18/2021 Stop bằng cách hay hơn
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
        playSong(mListPlaySong.get(mPlayIndex));
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
        playSong(mListPlaySong.get(mPlayIndex));
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

        sendBroadcast(intent);
    }
}
