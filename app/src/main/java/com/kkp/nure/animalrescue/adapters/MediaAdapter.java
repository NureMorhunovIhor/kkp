package com.kkp.nure.animalrescue.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kkp.nure.animalrescue.R;
import com.kkp.nure.animalrescue.entities.Media;
import com.kkp.nure.animalrescue.utils.GlideMediaUrl;

import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaViewHolder> {

    private final List<Media> medias;
    private final Context context;

    public MediaAdapter(List<Media> medias, Context context) {
        this.medias = medias;
        this.context = context;
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.report_media_item, parent, false);
        return new MediaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {
        Glide.with(context)
                .load(new GlideMediaUrl(medias.get(position)))
                .placeholder(R.drawable.baseline_hide_image_24)
                .into(holder.imageView);
        holder.removeButton.setOnClickListener(view -> {
            int itemPos = holder.getAdapterPosition();
            medias.remove(itemPos);
            notifyItemRemoved(itemPos);
        });
    }

    @Override
    public int getItemCount() {
        return medias.size();
    }

    public static class MediaViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton removeButton;

        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.mediaImageView);
            removeButton = itemView.findViewById(R.id.removeMediaButton);
        }
    }
}


