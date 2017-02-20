package net.oschina.app.improve.widget.banner;

import android.content.Context;
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
        return R.layout.layout_event_header_view;
    }

    @Override
    protected Object instantiateItem(ViewGroup container, int position) {
        ViewEventBanner view = new ViewEventBanner(getContext());
        container.addView(view);
        if (mBanners.size() != 0) {
            int p = position % mBanners.size();
            if (p >= 0 && p < mBanners.size()) {
                view.initData(mImageLoader, mBanners.get(p));
            }
        }
        return view;
    }

    @Override
    protected void destroyItem(ViewGroup container, int position, Object object) {
        if (object instanceof ViewEventBanner) {
            container.removeView((ViewEventBanner) object);
        }
    }
}
