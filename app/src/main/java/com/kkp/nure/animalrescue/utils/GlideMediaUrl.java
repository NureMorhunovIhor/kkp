package com.kkp.nure.animalrescue.utils;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;
import com.kkp.nure.animalrescue.entities.Media;

import java.net.URL;

public class GlideMediaUrl extends GlideUrl {
    public Long mediaId = null;

    public GlideMediaUrl(URL url) {
        super(url);
    }

    public GlideMediaUrl(String url) {
        super(url);
    }

    public GlideMediaUrl(URL url, Headers headers) {
        super(url, headers);
    }

    public GlideMediaUrl(String url, Headers headers) {
        super(url, headers);
    }

    public GlideMediaUrl(Media media) {
        this(media.getUrl());
        mediaId = media.getId();
    }

    @Override
    public String getCacheKey() {
        if(mediaId == null)
            return super.getCacheKey();

        return "cached-media://"+mediaId;
    }
}
