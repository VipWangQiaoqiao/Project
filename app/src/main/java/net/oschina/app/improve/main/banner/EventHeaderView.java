package net.oschina.app.improve.main.banner;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;

import net.oschina.app.R;
import net.oschina.app.improve.widget.ViewEventBanner;

/**
 * Created by haibin
 * on 2016/10/26.
 */

public class EventHeaderView extends HeaderView {
    public EventHeaderView(Context context, RequestManager loader, String api, String bannerCache) {
        super(context, loader, api, bannerCache);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.banner_event;
    }

    @Override
    protected View instantiateItem(ViewGroup container, int position) {
        ViewEventBanner view = new ViewEventBanner(getContext());
        view.initData(mImageLoader, mBanners.get(position));
        container.addView(view);
        return view;
    }

    @Override
    public void onItemClick(int position) {

    }
}
