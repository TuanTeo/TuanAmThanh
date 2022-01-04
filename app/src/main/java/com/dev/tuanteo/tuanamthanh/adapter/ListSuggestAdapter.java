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
import com.dev.tuanteo.tuanamthanh.api.FirebaseFireStoreAPI;
import com.dev.tuanteo.tuanamthanh.database.FavoriteSongProvider;
import com.dev.tuanteo.tuanamthanh.listener.IFirebaseListener;
import com.dev.tuanteo.tuanamthanh.listener.ILocalSongClickListener;
import com.dev.tuanteo.tuanamthanh.object.Artist;
import com.dev.tuanteo.tuanamthanh.object.MusicCategory;
import com.dev.tuanteo.tuanamthanh.object.Song;
import com.dev.tuanteo.tuanamthanh.units.SongUtils;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

public class ListSuggestAdapter extends RecyclerView.Adapter<ListSuggestAdapter.ViewHolder>
    implements IFirebaseListener {

    private Context mContext;
    protected List<Song> mListSuggestSong = new ArrayList<>();
    private static ILocalSongClickListener mListener;

    private boolean mIsSuggest;

    public ListSuggestAdapter(Context context, ILocalSongClickListener listener) {
        mContext = context;
        mListener = listener;
        getAndUpdateListSong(null);
        mIsSuggest = true;
    }

    public ListSuggestAdapter(Context context, ILocalSongClickListener listener, String condition) {
        mContext = context;
        mListener = listener;
        getAndUpdateListSong(condition);
    }

    protected void getAndUpdateListSong(String condition) {
        /*TuanTeo: Lay du lieu tren Firebase */
        FirebaseFireStoreAPI.getListSong(FirebaseFireStoreAPI.SONG_SUGGEST, "true", this);
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull @Nonnull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.local_song_item, parent, false);
        return new ListSuggestAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @Nonnull ViewHolder holder, int position) {
        holder.getSongName().setText(mListSuggestSong.get(position).getName());
        holder.getSingerName().setText(mListSuggestSong.get(position).getArtist());
        Glide.with(mContext)
                .load(mListSuggestSong.get(position).getImage())
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(holder.getSongImageview());

        if (holder.getMenuImageView() != null) {
            holder.getMenuImageView().setOnClickListener(v -> {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(mContext, holder.mMenuImageView);
                //inflating menu from xml resource
                popup.inflate(R.menu.song_online_options_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.play_song_action:
                            mListener.playSong(mListSuggestSong.get(position), true, true);
                            return true;
                        case R.id.download_song_action:
                            FirebaseFireStoreAPI.downloadSong(mContext, mListSuggestSong.get(position));

                            mListener.updateListLocalSong();
                            return true;
                        case R.id.favorite_song_action:
                            /*TuanTeo: Them vao bai hat yeu thich */
                            mContext.getContentResolver().insert(FavoriteSongProvider.CONTENT_URI,
                                    SongUtils.getContentDownloadSong(mListSuggestSong.get(position)));

                            Toast.makeText(mContext, mContext.getString(R.string.added_to_favorite), Toast.LENGTH_SHORT).show();

                            mListener.updateListLocalSong();
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
        return mListSuggestSong.size();
    }

    @Override
    public void getListSongComplete(ArrayList<Song> listSong) {
        mListSuggestSong.clear();
        mListSuggestSong = listSong;
        notifyDataSetChanged();
    }

    @Override
    public void getListCategoryComplete(ArrayList<MusicCategory> listCategory) {

    }

    @Override
    public void getListArtistComplete(ArrayList<Artist> listArtist) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

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

        @Override
        public void onClick(View v) {
            mListener.playSong(mListSuggestSong.get(getAdapterPosition()), true, mIsSuggest);
        }
    }
}
