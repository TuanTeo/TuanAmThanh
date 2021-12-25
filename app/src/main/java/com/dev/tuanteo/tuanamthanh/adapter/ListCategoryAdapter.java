package com.dev.tuanteo.tuanamthanh.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dev.tuanteo.tuanamthanh.R;
import com.dev.tuanteo.tuanamthanh.api.FirebaseFireStoreAPI;
import com.dev.tuanteo.tuanamthanh.listener.RecyclerAdapterListener;
import com.dev.tuanteo.tuanamthanh.listener.IFirebaseListener;
import com.dev.tuanteo.tuanamthanh.object.Artist;
import com.dev.tuanteo.tuanamthanh.object.MusicCategory;
import com.dev.tuanteo.tuanamthanh.object.Song;

import java.util.ArrayList;

public class ListCategoryAdapter extends  RecyclerView.Adapter<ListCategoryAdapter.ViewHolder>
        implements IFirebaseListener {

    private final Context mContext;
    private ArrayList<MusicCategory> mListCategory = new ArrayList<>();
    private final RecyclerAdapterListener mListener;

    public ListCategoryAdapter(Context context, RecyclerAdapterListener listener) {
        mContext = context;
        mListener = listener;

        /*TuanTeo: Get all music category */
        FirebaseFireStoreAPI.getListCategory(FirebaseFireStoreAPI.ALL_CATEGORY_DB, this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.category_music_item, parent, false);
        return new ListCategoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListCategoryAdapter.ViewHolder holder, int position) {
        holder.getNameCategory().setText(mListCategory.get(position).getName());
        Glide.with(mContext)
                .load(mListCategory.get(position).getAvatar())
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(holder.getAvatarCategory());

        holder.getAvatarCategory().setOnClickListener(view -> {
            mListener.openListCategorySong(mListCategory.get(position).getName(), mListCategory.get(position).getAvatar());
        });
    }

    @Override
    public int getItemCount() {
        return mListCategory.size();
    }

    @Override
    public void getListSongComplete(ArrayList<Song> listSong) {

    }

    @Override
    public void getListCategoryComplete(ArrayList<MusicCategory> listCategory) {
        mListCategory = listCategory;
        notifyDataSetChanged();
    }

    @Override
    public void getListArtistComplete(ArrayList<Artist> listArtist) {

    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mAvatarCategory;
        private final TextView mNameCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mAvatarCategory = itemView.findViewById(R.id.category_music_background);
            mNameCategory = itemView.findViewById(R.id.category_music_name);
        }

        public ImageView getAvatarCategory() {
            return mAvatarCategory;
        }

        public TextView getNameCategory() {
            return mNameCategory;
        }
    }
}
