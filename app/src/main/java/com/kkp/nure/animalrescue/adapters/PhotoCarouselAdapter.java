package com.kkp.nure.animalrescue.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kkp.nure.animalrescue.R;
import com.kkp.nure.animalrescue.entities.Media;
import com.kkp.nure.animalrescue.utils.GlideMediaUrl;

import java.util.List;

public class PhotoCarouselAdapter extends RecyclerView.Adapter<PhotoCarouselAdapter.PhotoViewHolder> {

    private final List<Media> photos;
    private final Context context;

    public PhotoCarouselAdapter(List<Media> photos, Context context) {
        this.photos = photos;
        this.context = context;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView image = new ImageView(context);
        image.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new PhotoViewHolder(image);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        Glide.with(context)
                .load(new GlideMediaUrl(photos.get(position)))
                .placeholder(R.drawable.baseline_hide_image_24)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView;
        }
    }
}

