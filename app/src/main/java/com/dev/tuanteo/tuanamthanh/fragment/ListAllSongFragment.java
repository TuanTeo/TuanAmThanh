package com.dev.tuanteo.tuanamthanh.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dev.tuanteo.tuanamthanh.R;
import com.dev.tuanteo.tuanamthanh.adapter.ListLocalSongAdapter;
import com.dev.tuanteo.tuanamthanh.listener.ILocalSongClickListener;
import com.dev.tuanteo.tuanamthanh.object.Song;

import java.util.ArrayList;
import java.util.List;

public class ListAllSongFragment extends Fragment {


    private final Context mContext;
    private RecyclerView mListSongRecyclerView;
    private ILocalSongClickListener mItemClickListener;

    public ListAllSongFragment(Context context, ILocalSongClickListener listener) {
        this.mContext = context;
        mItemClickListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.list_all_song_fragment, container, false);

        initViewComponent(view);
        return view;
    }

    // TODO: 11/8/2021 Dung Adapter xịn
    @SuppressLint("VisibleForTests")
    private void initViewComponent(View view) {
        mListSongRecyclerView = view.findViewById(R.id.list_all_song_recycler_view);
        mListSongRecyclerView.setAdapter(new ListLocalSongAdapter(mContext, getAllAudioFromDevice(mContext), mItemClickListener));
        mListSongRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    }

    public List<Song> getAllAudioFromDevice(final Context context) {

        final List<Song> tempAudioList = new ArrayList<>();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Audio.AudioColumns.TITLE,// 0
                MediaStore.Audio.AudioColumns.TRACK,// 1
                MediaStore.Audio.AudioColumns.YEAR,// 2
                MediaStore.Audio.AudioColumns.DURATION,// 3
                MediaStore.Audio.AudioColumns.DATA,// 4
                MediaStore.Audio.AudioColumns.ALBUM,// 5
                MediaStore.Audio.AudioColumns.ARTIST_ID,// 6
                MediaStore.Audio.AudioColumns.ARTIST,// 7
                MediaStore.Audio.Media.ALBUM_ID, //8
                MediaStore.Audio.Albums._ID // 9
        };

        // if want fetch all files
        Cursor c = context.getContentResolver().query(uri,
                projection,
                null,
                null,
                null);

        if (c != null) {
            while (c.moveToNext()) {
                Song audioModel = new Song();
                String path = c.getString(4);
                String name = c.getString(0);
                String album = c.getString(5);
                String artist = c.getString(7);
                String id = c.getString(8);
                int duration = c.getInt(3);

                audioModel.setId(id);
                audioModel.setName(name);
                audioModel.setAlbum(album);
                audioModel.setArtist(artist);
                audioModel.setPath(path);

                Log.d("Name :" + name, " Album :" + album);
                Log.d("Path :" + path, " Artist :" + artist);
                Log.d("id:", path);

                /*TuanTeo: Chi lay file am thanh tren 5s */
                if (duration >= 5000) {
                    tempAudioList.add(audioModel);
                }
            }
            c.close();
        }
        return tempAudioList;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mItemClickListener != null) {
            mItemClickListener = null;
        }
    }
}
