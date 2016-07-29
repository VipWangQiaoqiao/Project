package net.oschina.app.improve.impl;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

/**
 * Created by JuQiu
 * on 16/7/28.
 */
public abstract class GlideRequestFinishListener implements RequestListener<String, GlideDrawable> {
    @Override
    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
        onFinish();
        return false;
    }

    @Override
    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
        onFinish();
        return false;
    }

    public abstract void onFinish();
}
