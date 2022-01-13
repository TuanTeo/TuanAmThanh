package com.dev.tuanteo.tuanamthanh.api;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.dev.tuanteo.tuanamthanh.listener.IFirebaseListener;
import com.dev.tuanteo.tuanamthanh.object.Song;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class FirebaseFireStoreAPITest {

    private Context mContext;

    @Mock
    IFirebaseListener mListener;

    ArrayList<Song> mListSong;


    @Before
    public void setup() {
        mListener = mock(IFirebaseListener.class);
        mContext = ApplicationProvider.getApplicationContext();
        mListSong = new ArrayList<>();
    }

    @Test
    public void getListSong_loadSuccessfully_shouldCalledListener() {
        FirebaseFireStoreAPI.getListSong(null, null, mListener);
        verify(mListener, times(1)).getListSongComplete(mListSong);
    }
}