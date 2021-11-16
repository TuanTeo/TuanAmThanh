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
                MediaStore.Audio.AudioColumns.DATA,         //0
                MediaStore.Audio.AudioColumns.TITLE,        //1
                MediaStore.Audio.AudioColumns.ALBUM,        //2
                MediaStore.Audio.ArtistColumns.ARTIST,      //3
                MediaStore.Audio.Media.ALBUM_ID,            //4
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
                String path = c.getString(0);
                String name = c.getString(1);
                String album = c.getString(2);
                String artist = c.getString(3);
                long id = c.getLong(4);

                audioModel.setId(id);
                audioModel.setName(name);
                audioModel.setAlbum(album);
                audioModel.setArtist(artist);
                audioModel.setPath(path);

                Log.e("Name :" + name, " Album :" + album);
                Log.e("Path :" + path, " Artist :" + artist);

                tempAudioList.add(audioModel);
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
