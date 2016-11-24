package net.oschina.app.improve.main.banner;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;

import net.oschina.app.R;
import net.oschina.app.bean.Banner;
import net.oschina.app.improve.widget.ViewNewsBanner;
import net.oschina.app.util.UIHelper;

import java.util.List;

/**
 * Created by haibin
 * on 2016/10/26.
 */

public class NewsHeaderView extends HeaderView {
    private TextView mTitleTextView;

    public NewsHeaderView(Context context, RequestManager loader, String api, String bannerCache) {
        super(context, loader, api, bannerCache);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        mTitleTextView = (TextView) findViewById(R.id.tv_title);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.banner_news;
    }

    @Override
    public void onViewSelected(int position) {
        super.onViewSelected(position);
        mTitleTextView.setText(mBanners.get(position).getName());
    }

    @Override
    void setBanners(List<Banner> banners) {
        if (banners.size() > 0 && mBanners.size() == 0) {
            mTitleTextView.setText(banners.get(0).getName());
        }
        super.setBanners(banners);
    }

    @Override
    protected View instantiateItem(ViewGroup container, int position) {
        ViewNewsBanner view = new ViewNewsBanner(getContext());
        view.initData(mImageLoader, mBanners.get(position));
        container.addView(view);
        return view;
    }

    @Override
    public void onItemClick(int position) {
        Banner banner = mBanners.get(position);
        if (banner != null) {
            int type = banner.getType();
            long id = banner.getId();
            UIHelper.showDetail(getContext(), type, id, banner.getHref());
        }
    }
}
