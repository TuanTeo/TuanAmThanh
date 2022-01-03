package com.dev.tuanteo.tuanamthanh.adapter;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.VisibleForTesting;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dev.tuanteo.tuanamthanh.R;
import com.dev.tuanteo.tuanamthanh.database.FavoriteSongDatabase;
import com.dev.tuanteo.tuanamthanh.database.FavoriteSongProvider;
import com.dev.tuanteo.tuanamthanh.listener.ILocalSongClickListener;
import com.dev.tuanteo.tuanamthanh.object.Song;
import com.dev.tuanteo.tuanamthanh.units.SongUtils;
import com.dev.tuanteo.tuanamthanh.units.Utils;

import java.util.Collections;
import java.util.List;

public class ListLocalSongAdapter extends RecyclerView.Adapter<ListLocalSongAdapter.ViewHolder> {

    protected Context mContext;
    protected static List<Song> mListSong;
    private static ILocalSongClickListener mListener;

    public ListLocalSongAdapter(Context context, List<Song> listSong, ILocalSongClickListener listener) {
        mContext = context;
        mListSong = listSong;
        /*TuanTeo: Sap xep theo A-Z */
        Collections.sort(mListSong);
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.local_song_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getSongName().setText(mListSong.get(position).getName());
        holder.getSingerName().setText(mListSong.get(position).getArtist());

        Glide.with(mContext)
                .load(mListSong.get(position).getImage())
                .placeholder(R.drawable.music_note)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(holder.getSongImageview());

        if (holder.getMenuImageView() != null) {
            holder.getMenuImageView().setOnClickListener(v -> {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(mContext, holder.mMenuImageView);
                //inflating menu from xml resource
                popup.inflate(R.menu.song_options_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.play_song_action:
                            mListener.playSong(mListSong.get(position), false, false);
                            return true;
                        case R.id.delete_song_action:
                            // TODO: 12/31/2021 them logic xoa bai hat
                            /*TuanTeo: Xoa bai hat Offline */
                            boolean result = Utils.deleteFileUsingDisplayName(mContext, Utils.getNameFileByPath(mListSong.get(position).getPath()));

                            if (result) {
                                Toast.makeText(mContext, mContext.getString(R.string.deleted_complete),
                                        Toast.LENGTH_SHORT).show();

                                /*TuanTeo: Xoa khoi bai hat yeu thich */
                                mContext.getContentResolver().delete(FavoriteSongProvider.CONTENT_URI,
                                        FavoriteSongDatabase.COLUMN_SONG_ID + " =?",
                                        new String[] {mListSong.get(position).getId()});

                                mListSong = SongUtils.getListLocalSong(mContext);

                                /*TuanTeo: Sap xep theo A-Z */
                                Collections.sort(mListSong);

                                notifyDataSetChanged();
                            }

                            return true;
                        case R.id.favorite_song_action:
                            /*TuanTeo: Them vao bai hat yeu thich */
                            mContext.getContentResolver().insert(FavoriteSongProvider.CONTENT_URI,
                                    SongUtils.getContentDownloadSong(mListSong.get(position)));

                            Toast.makeText(mContext, mContext.getString(R.string.added_to_favorite),
                                    Toast.LENGTH_SHORT).show();
                            return true;
                        default:
                            return false;
                    }
                });
                //displaying the popup
                popup.show();
            });
        }
    }

    @Override
    public int getItemCount() {
        return mListSong.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mSongName;
        private TextView mSingerName;
        private ImageView mSongImageview;
        private ImageView mMenuImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mSongName = itemView.findViewById(R.id.local_song_name);
            mSingerName = itemView.findViewById(R.id.local_singer_name);
            mSongImageview = itemView.findViewById(R.id.local_song_image_view);
            mMenuImageView = itemView.findViewById(R.id.local_song_more_menu);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.playSong(mListSong.get(getAdapterPosition()), false, false);
        }

        public ImageView getSongImageview() {
            return mSongImageview;
        }
        public TextView getSongName() {
            return mSongName;
        }
        public TextView getSingerName() {
            return mSingerName;
        }

        public ImageView getMenuImageView() {
            return mMenuImageView;
        }
    }

    /**
     * Tuantqd
     * Function to get Image Uri by AlbumID
     * @param imgUri
     * @return
     */
    public static Uri queryAlbumUri(String imgUri) {   //dung album de load anh
        final Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
        return ContentUris.withAppendedId(artworkUri, Long.parseLong(imgUri));  //noi them imgUri vao artworkUri
    }
}
