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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.VisibleForTesting;
import androidx.recyclerview.widget.RecyclerView;

import com.dev.tuanteo.tuanamthanh.R;
import com.dev.tuanteo.tuanamthanh.listener.ILocalSongClickListener;
import com.dev.tuanteo.tuanamthanh.object.Song;

import java.io.IOException;
import java.util.List;

public class ListLocalSongAdapter extends RecyclerView.Adapter<ListLocalSongAdapter.ViewHolder> {

    private Context mContext;
    protected static List<Song> mListSong;
    private static ILocalSongClickListener mListener;

    public ListLocalSongAdapter(Context context, List<Song> listSong, ILocalSongClickListener listener) {
        mContext = context;
        mListSong = listSong;
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            Uri imageUri = queryAlbumUri(mListSong.get(position).getId());
//            if (imageUri != null) {
//                holder.getSongImageview().setImageURI(imageUri);
//            } else {
//                holder.getSongImageview().setImageResource(R.drawable.song_image);
//            }
        }

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
                        return true;
                    case R.id.favorite_song_action:
                        // TODO: 12/31/2021 them logic bai hat yeu thich
                        return true;
                    default:
                        return false;
                }
            });
            //displaying the popup
            popup.show();
        });
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
