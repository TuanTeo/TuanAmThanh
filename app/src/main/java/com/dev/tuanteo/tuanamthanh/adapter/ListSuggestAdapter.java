package com.dev.tuanteo.tuanamthanh.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dev.tuanteo.tuanamthanh.R;
import com.dev.tuanteo.tuanamthanh.api.FirebaseFireStoreAPI;
import com.dev.tuanteo.tuanamthanh.listener.IFirebaseListener;
import com.dev.tuanteo.tuanamthanh.listener.ILocalSongClickListener;
import com.dev.tuanteo.tuanamthanh.object.Artist;
import com.dev.tuanteo.tuanamthanh.object.MusicCategory;
import com.dev.tuanteo.tuanamthanh.object.Song;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

public class ListSuggestAdapter extends RecyclerView.Adapter<ListSuggestAdapter.ViewHolder>
    implements IFirebaseListener
{

    private Context mContext;
    private List<Song> mListSuggestSong = new ArrayList<>();
    private static ILocalSongClickListener mListener;

    public ListSuggestAdapter(Context context, ILocalSongClickListener listener) {
        mContext = context;
        mListener = listener;
        /*TuanTeo: Lay du lieu tren Firebase */
        FirebaseFireStoreAPI.getListSong(FirebaseFireStoreAPI.ALL_SONG_DB, this);
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
        Glide.with(mContext).load(mListSuggestSong.get(position).getImage()).into(holder.getSongImageview());
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mSongName = itemView.findViewById(R.id.local_song_name);
            mSingerName = itemView.findViewById(R.id.local_singer_name);
            mSongImageview = itemView.findViewById(R.id.local_song_image_view);
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

        @Override
        public void onClick(View v) {
            mListener.playSong(mListSuggestSong.get(getAdapterPosition()), true);
        }
    }
}
