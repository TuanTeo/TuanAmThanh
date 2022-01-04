package com.dev.tuanteo.tuanamthanh.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dev.tuanteo.tuanamthanh.R;
import com.dev.tuanteo.tuanamthanh.database.FavoriteSongDatabase;
import com.dev.tuanteo.tuanamthanh.database.FavoriteSongProvider;
import com.dev.tuanteo.tuanamthanh.listener.MediaControlListener;
import com.dev.tuanteo.tuanamthanh.object.Song;
import com.dev.tuanteo.tuanamthanh.service.MediaPlayService;
import com.dev.tuanteo.tuanamthanh.units.LogUtils;
import com.dev.tuanteo.tuanamthanh.units.SongUtils;
import com.dev.tuanteo.tuanamthanh.view.CircularSeekBar;

public class MediaPlayControlFragment extends Fragment {

    private final Context mContext;
    private MediaPlayService mMediaPlayService;

    private MediaControlListener mListener;

    /*TuanTeo: Các phần tử của giao diện MediaPlayback */
    private TextView mSongNameTView;
    private TextView mSingerNameTView;
    private ImageView mSongImage;
    private ImageButton mPlayPauseButton;
    private ImageButton mPreviousButton;
    private ImageButton mNextButton;
    private CircularSeekBar mSeekBar;
    private ImageView mNavigateBackButton;
    private ImageView mFavoriteButton;

    /*TuanTeo: bien luu trang thai dang hien thi cua fragment */
    public boolean mIsDisplaying;

    /*TuanTeo: luu trang thai nhac yeu thich */
    private boolean mIsFavorite;

    /*TuanTeo: Dung de cap nhat lai tien trinh seekbar */
    private final Handler mSeekHandler = new Handler();
    private final Runnable mUpdateSeekBar = new Runnable() {
        @Override
        public void run() {
            // Updating progress bar
            mSeekBar.setProgress(mMediaPlayService.getMediaPlayer().getCurrentPosition());

            if (mIsDisplaying) {
                // Call this thread again after 15 milliseconds => ~ 1000/60fps
                mSeekHandler.postDelayed(this, 15);
            }
        }
    };

    public MediaPlayControlFragment(Context mContext, MediaPlayService mediaPlayService,
                                    MediaControlListener listener) {
        this.mContext = mContext;
        mMediaPlayService = mediaPlayService;
        mListener = listener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.media_player_layout, container, false);

        initComponent(view);
        updateUI();

        mIsDisplaying = true;

        return view;
    }

    private void initComponent(View view) {
        mSongNameTView = view.findViewById(R.id.play_song_name);
        mSingerNameTView = view.findViewById(R.id.play_singer_name);

        mSongImage = view.findViewById(R.id.play_album_image);

        mPlayPauseButton = view.findViewById(R.id.play_start_pause_button);
        mPlayPauseButton.setOnClickListener(v -> {
            if (mMediaPlayService.isPlayingMusic()) {
                mPlayPauseButton.setImageResource(R.drawable.ic_play_circle_control);
            } else {
                mPlayPauseButton.setImageResource(R.drawable.ic_pause_circle_control);
            }
            mMediaPlayService.pauseOrResumeMusic();
        });

        mPreviousButton = view.findViewById(R.id.play_previous_button);
        mPreviousButton.setOnClickListener(v -> {
            mMediaPlayService.previousMusic();
            updateUI();
        });

        mNextButton = view.findViewById(R.id.play_next_button);
        mNextButton.setOnClickListener(v -> {
            mMediaPlayService.nextMusic();
            updateUI();
        });

        mSeekBar = view.findViewById(R.id.play_seek_bar);

        /*TuanTeo: Cập nhật tiến trình seekbar */
        mSeekHandler.postDelayed(mUpdateSeekBar, 15);

        mSeekBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mMediaPlayService.getMediaPlayer().seekTo(progress);
                }
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {

            }
        });

        mNavigateBackButton = view.findViewById(R.id.navigation_back_button);
        mNavigateBackButton.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        mFavoriteButton = view.findViewById(R.id.favorite_button);
        mFavoriteButton.setOnClickListener(v -> {
            // TODO: 12/31/2021 Them logic danh sach yeu thich
            if (mIsFavorite) {
                mIsFavorite = false;
                mFavoriteButton.setImageResource(R.drawable.ic_favorite_border);

                /*TuanTeo: Xoa khoi bai hat yeu thich */
                mContext.getContentResolver().delete(FavoriteSongProvider.CONTENT_URI,
                        FavoriteSongDatabase.COLUMN_SONG_ID + " =?",
                        new String[] {mMediaPlayService.getCurrentPlaySong().getId()});
            } else {
                mIsFavorite = true;
                mFavoriteButton.setImageResource(R.drawable.ic_favorited);

                /*TuanTeo: Them vao bai hat yeu thich */
                mContext.getContentResolver().insert(FavoriteSongProvider.CONTENT_URI,
                        SongUtils.getContentDownloadSong(mMediaPlayService.getCurrentPlaySong()));
            }

            mListener.updateListFavoriteSong();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mIsDisplaying = false;
    }

    /**
     * Hàm cập nhật lại giao diện MediaPlayControl
     */
    public void updateUI() {
        Song song = mMediaPlayService.getCurrentPlaySong();

        mSongNameTView.setText(song.getName());
        mSingerNameTView.setText(song.getArtist());
        Glide.with(mContext)
                .load(song.getImage())
                .placeholder(R.drawable.music_note)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(mSongImage);
        
        if (mMediaPlayService.isPlayingMusic()) {
            mPlayPauseButton.setImageResource(R.drawable.ic_pause_circle_control);
        } else {
            mPlayPauseButton.setImageResource(R.drawable.ic_play_circle_control);
        }

        mSeekBar.setMax(mMediaPlayService.getCurrentPlaySong().getDuration());
        mSeekBar.setProgress(mMediaPlayService.getMediaPlayer().getCurrentPosition());

        mIsFavorite = checkIsFavoriteSong(song.getId());
        if (!mIsFavorite) {
            mFavoriteButton.setImageResource(R.drawable.ic_favorite_border);
        } else {
            mFavoriteButton.setImageResource(R.drawable.ic_favorited);
        }
    }

    private boolean checkIsFavoriteSong(String songId) {
        String[] BASE_PROJECTION = {FavoriteSongDatabase.COLUMN_SONG_ID};
        String condition = FavoriteSongDatabase.COLUMN_SONG_ID + " = '" + songId +"'";

        Cursor cursor = mContext.getContentResolver()
                .query(FavoriteSongProvider.CONTENT_URI, BASE_PROJECTION,
                        condition, null, null);

        if (cursor != null) {
            if (cursor.getCount()>0) {
                return true;
            }
            cursor.close();
        }

        return false;
    }

    public boolean isIsDisplaying() {
        return mIsDisplaying;
    }
}
