package com.dev.tuanteo.tuanamthanh.listener;

import com.dev.tuanteo.tuanamthanh.object.Song;

public interface ILocalSongClickListener {
    void playSong(Song song, boolean isOnline);
}
