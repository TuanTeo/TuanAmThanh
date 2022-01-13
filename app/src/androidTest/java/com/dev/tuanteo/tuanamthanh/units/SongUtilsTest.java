package com.dev.tuanteo.tuanamthanh.units;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.dev.tuanteo.tuanamthanh.object.Song;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class SongUtilsTest {

    @Test
    public void getListLocalSong_shouldNotNull() {
        Context context = ApplicationProvider.getApplicationContext();
        ArrayList<Song> list = SongUtils.getListLocalSong(context);
        assertNotNull(list);
    }
}