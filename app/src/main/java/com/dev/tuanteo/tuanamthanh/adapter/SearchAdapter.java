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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dev.tuanteo.tuanamthanh.R;
import com.dev.tuanteo.tuanamthanh.listener.ILocalSongClickListener;
import com.dev.tuanteo.tuanamthanh.object.Song;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    protected Context mContext;
    protected static List<Song> mListResultSong;
    private static ILocalSongClickListener mListener;

    public SearchAdapter(Context context, List<Song> listSong, ILocalSongClickListener listener) {
        mContext = context;
        mListResultSong = listSong;
        mListener = listener;
    }

    @NonNull
    @Nonnull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @Nonnull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.search_song_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @Nonnull ViewHolder holder, int position) {
        holder.getSongName().setText(mListResultSong.get(position).getName());
        holder.getSingerName().setText(mListResultSong.get(position).getArtist());

        Glide.with(mContext)
                .load(mListResultSong.get(position).getImage())
                .placeholder(R.drawable.music_note)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(holder.getSongImageview());
    }

    @Override
    public int getItemCount() {
        return mListResultSong.size();
    }

    public void setListSong(ArrayList<Song> listResult) {
        mListResultSong = listResult;
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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

        @Override
        public void onClick(View v) {
            mListener.playSong(mListResultSong.get(getAdapterPosition()), false, false);
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
    }
}
