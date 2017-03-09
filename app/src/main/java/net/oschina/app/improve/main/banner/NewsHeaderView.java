package net.oschina.app.improve.main.banner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
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

@SuppressLint("ViewConstructor")
public class NewsHeaderView extends HeaderView {
    private TextView mTitleTextView;

    public NewsHeaderView(Context context, RequestManager loader, String api, String bannerCache) {
        super(context, loader, api, bannerCache);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        //mBannerView.setTransformer(new ScaleTransform());
        mTitleTextView = (TextView) findViewById(R.id.tv_title);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_news_banner;
    }

    @Override
    public void onViewSelected(int position) {
        super.onViewSelected(position);
        if (mBanners.size() != 0)
            mTitleTextView.setText(mBanners.get(position % mBanners.size()).getName());
    }

    @Override
    void setBanners(List<Banner> banners) {
        super.setBanners(banners);
        if (banners.size() > 0 && mCurrentItem == 0) {
            mTitleTextView.setText(banners.get(0).getName());
        }
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

    @Override
    protected View instantiateItem(int position) {
        ViewNewsBanner view = new ViewNewsBanner(getContext());
        if (mBanners.size() != 0) {
            int p = position % mBanners.size();
            if (p >= 0 && p < mBanners.size()) {
                view.initData(mImageLoader, mBanners.get(p));
            }
        }
        return view;
    }
}
