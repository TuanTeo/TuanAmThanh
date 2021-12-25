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
import com.dev.tuanteo.tuanamthanh.api.FirebaseFireStoreAPI;
import com.dev.tuanteo.tuanamthanh.listener.IFirebaseListener;
import com.dev.tuanteo.tuanamthanh.listener.RecyclerAdapterListener;
import com.dev.tuanteo.tuanamthanh.object.Artist;
import com.dev.tuanteo.tuanamthanh.object.MusicCategory;
import com.dev.tuanteo.tuanamthanh.object.Song;

import java.util.ArrayList;

public class ListSingerAdapter extends  RecyclerView.Adapter<ListSingerAdapter.ViewHolder> implements IFirebaseListener {

    private final Context mContext;
    private ArrayList<Artist> mListArtist = new ArrayList<>();
    private RecyclerAdapterListener mListener;

    public ListSingerAdapter(Context context, RecyclerAdapterListener listener) {
        mContext = context;
        mListener = listener;
        FirebaseFireStoreAPI.getListArtist(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.music_singer_item, parent, false);
        return new ListSingerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListSingerAdapter.ViewHolder holder, int position) {
        holder.getArtistName().setText(mListArtist.get(position).getName());
        Glide.with(mContext)
                .load(mListArtist.get(position).getAvatar())
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(holder.getArtistAvatar());

        holder.getArtistAvatar().setOnClickListener(view -> {
            mListener.openListArtistSong(mListArtist.get(position).getName(),
                    mListArtist.get(position).getAvatar());
        });
    }

    @Override
    public int getItemCount() {
        return mListArtist.size();
    }

    @Override
    public void getListSongComplete(ArrayList<Song> listSong) {

    }

    @Override
    public void getListCategoryComplete(ArrayList<MusicCategory> listCategory) {

    }

    @Override
    public void getListArtistComplete(ArrayList<Artist> listArtist) {
        mListArtist = listArtist;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        private final ImageView mArtistAvatar;
        private final TextView mArtistName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mArtistAvatar = itemView.findViewById(R.id.singer_music_background);
            mArtistName = itemView.findViewById(R.id.music_singer_name);
        }

        public ImageView getArtistAvatar() {
            return mArtistAvatar;
        }

        public TextView getArtistName() {
            return mArtistName;
        }
    }
}
