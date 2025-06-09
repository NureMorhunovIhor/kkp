package com.kkp.nure.animalrescue.utils;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.kkp.nure.animalrescue.R;
import com.kkp.nure.animalrescue.entities.Media;

public class GlideUtils {
    public static void loadMediaToImage(ImageView imageView, Media media) {
        if (media != null) {
            Glide.with(imageView.getContext())
                    .load(new GlideMediaUrl(media))
                    .placeholder(R.drawable.baseline_no_accounts_24)
                    .circleCrop()
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.baseline_no_accounts_24);
        }
    }
}
