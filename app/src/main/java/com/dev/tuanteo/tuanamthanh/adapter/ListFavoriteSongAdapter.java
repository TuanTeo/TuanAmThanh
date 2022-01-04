package com.dev.tuanteo.tuanamthanh.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dev.tuanteo.tuanamthanh.R;
import com.dev.tuanteo.tuanamthanh.database.DownloadSongDatabase;
import com.dev.tuanteo.tuanamthanh.database.DownloadSongProvider;
import com.dev.tuanteo.tuanamthanh.database.FavoriteSongDatabase;
import com.dev.tuanteo.tuanamthanh.database.FavoriteSongProvider;
import com.dev.tuanteo.tuanamthanh.listener.ILocalSongClickListener;
import com.dev.tuanteo.tuanamthanh.object.Song;
import com.dev.tuanteo.tuanamthanh.units.SongUtils;
import com.dev.tuanteo.tuanamthanh.units.Utils;

import java.util.List;

import javax.annotation.Nonnull;

public class ListFavoriteSongAdapter extends RecyclerView.Adapter<ListFavoriteSongAdapter.ViewHolder> {

    protected Context mContext;
    protected static List<Song> mListSong;
    private static ILocalSongClickListener mListener;

    public ListFavoriteSongAdapter(Context context) {
        mContext = context;
        mListSong = SongUtils.getListFavoriteSong(mContext);
//        mListener = listener;
    }

    @NonNull
    @Nonnull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @Nonnull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.local_song_item, parent, false);
        return new ListFavoriteSongAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @Nonnull ViewHolder holder, int position) {
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
                popup.inflate(R.menu.favorite_option_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.play_song_action:
                            mListener.playSong(mListSong.get(position), false, false);
                            return true;
                        case R.id.remove_favorite_song_action:
                            /*TuanTeo: Xoa khoi bai hat yeu thich */
                            mContext.getContentResolver().delete(FavoriteSongProvider.CONTENT_URI,
                                    FavoriteSongDatabase.COLUMN_SONG_ID + " =?",
                                    new String[] {mListSong.get(position).getId()});

                            Toast.makeText(mContext, mContext.getString(R.string.added_to_favorite),
                                    Toast.LENGTH_SHORT).show();

//                            mListener.updateListLocalSong();
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

    public void updateListLocalSong() {
        mListSong = SongUtils.getListFavoriteSong(mContext);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
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
//            itemView.setOnClickListener(this);
        }

//        @Override
//        public void onClick(View v) {
//            mListener.playSong(mListSong.get(getAdapterPosition()), false, false);
//        }

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
}
