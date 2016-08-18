package net.oschina.app.improve.base.adapter;

import android.content.Context;

import com.bumptech.glide.RequestManager;

import java.util.Date;

/**
 * Created by huanghaibin_dev
 * on 2016/8/18.
 */

public abstract class BaseGeneralRecyclerAdapter<T> extends BaseRecyclerAdapter<T> {
    public BaseGeneralRecyclerAdapter(Callback callback, int mode) {
        super(callback.getContext(), mode);
    }

    public interface Callback {
        RequestManager getImgLoader();

        Context getContext();

        Date getSystemTime();
    }
}
