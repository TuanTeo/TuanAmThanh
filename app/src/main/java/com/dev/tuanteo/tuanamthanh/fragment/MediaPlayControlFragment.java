package com.dev.tuanteo.tuanamthanh.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dev.tuanteo.tuanamthanh.R;
import com.dev.tuanteo.tuanamthanh.object.Song;
import com.dev.tuanteo.tuanamthanh.service.MediaPlayService;

public class MediaPlayControlFragment extends Fragment {

    private final Context mContext;
    private MediaPlayService mMediaPlayService;

    /*TuanTeo: Các phần tử của giao diện MediaPlayback */
    private TextView mSongNameTView;
    private TextView mSingerNameTView;
    private ImageButton mPlayPauseButton;
    private ImageButton mPreviousButton;
    private ImageButton mNextButton;
    private SeekBar mSeekBar;

    public MediaPlayControlFragment(Context mContext, MediaPlayService mediaPlayService) {
        this.mContext = mContext;
        mMediaPlayService = mediaPlayService;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.media_player_layout, container, false);

        initComponent(view);
        updateUI();

        return view;
    }

    private void initComponent(View view) {
        mSongNameTView = view.findViewById(R.id.play_song_name);
        mSingerNameTView = view.findViewById(R.id.play_singer_name);

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
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /**
     * Hàm cập nhật lại giao diện MediaPlayControl
     */
    public void updateUI() {
        Song song = mMediaPlayService.getCurrentPlaySong();

        mSongNameTView.setText(song.getName());
        mSingerNameTView.setText(song.getArtist());
        if (mMediaPlayService.isPlayingMusic()) {
            mPlayPauseButton.setImageResource(R.drawable.ic_pause_circle_control);
        } else {
            mPlayPauseButton.setImageResource(R.drawable.ic_play_circle_control);
        }
    }
}
