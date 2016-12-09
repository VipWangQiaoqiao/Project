package net.oschina.app.improve.main.banner;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.RequestManager;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.Banner;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.utils.CacheManager;
import net.oschina.app.improve.widget.indicator.BannerView;
import net.oschina.app.improve.widget.indicator.CircleBannerIndicator;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by haibin
 * on 2016/10/26.
 */

public abstract class HeaderView extends RelativeLayout implements BannerView.OnViewChangeListener, BannerView.OnItemClickListener, Runnable {
    protected BannerView mBannerView;
    protected CircleBannerIndicator mIndicator;
    protected List<Banner> mBanners;
    protected BannerAdapter mAdapter;
    protected Handler mHandler;
    protected int mCurrentItem;
    protected RequestManager mImageLoader;
    protected TextHttpResponseHandler mCallBack;
    protected String mUrl;
    private boolean isScrolling;
    protected String mBannerCache;
    private boolean isStop;

    public HeaderView(Context context, RequestManager loader, String api, String bannerCache) {
        super(context);
        mImageLoader = loader;
        this.mUrl = api;
        this.mBannerCache = bannerCache;
        init(context);
    }

    protected void init(Context context) {
        mHandler = new Handler();
        mBanners = new ArrayList<>();

        List<Banner> banners = CacheManager.readListJson(context, mBannerCache, Banner.class);
        if (banners != null) {
            mBanners.addAll(banners);
            mHandler.postDelayed(this, 5000);
        }
        LayoutInflater.from(context).inflate(getLayoutId(), this, true);
        mBannerView = (BannerView) findViewById(R.id.banner);
        mIndicator = (CircleBannerIndicator) findViewById(R.id.indicator);
        mAdapter = new BannerAdapter();
        mBannerView.addOnViewChangeListener(this);
        mBannerView.setAdapter(mAdapter);
        mBannerView.setOnItemClickListener(this);
        mIndicator.bindBannerView(mBannerView);
        mCallBack = new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultBean<PageBean<Banner>> result = AppOperator.createGson().fromJson(responseString,
                            new TypeToken<ResultBean<PageBean<Banner>>>() {
                            }.getType());
                    if (result != null && result.isSuccess()) {
                        CacheManager.saveToJson(getContext(), mBannerCache, result.getResult().getItems());
                        setBanners(result.getResult().getItems());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        requestBanner();
    }

    @Override
    public void run() {
        mHandler.postDelayed(this, 5000);
        if (isScrolling)
            return;
        mCurrentItem = (mCurrentItem + 1) % mBanners.size();
        mBannerView.setCurrentItem(mCurrentItem);
    }

    public void requestBanner() {
        OSChinaApi.getBanner(mUrl, mCallBack);
    }

    void setBanners(List<Banner> banners) {
        if (banners != null) {
            mHandler.removeCallbacks(this);
            mBanners.clear();
            mBanners.addAll(banners);
            mBannerView.getAdapter().notifyDataSetChange();
            mIndicator.notifyDataSetChange();
            if (mBanners.size() > 1) {
                mHandler.postDelayed(this, 5000);
            }
        }
    }

    protected abstract int getLayoutId();

    protected abstract View instantiateItem(ViewGroup container, int position);


    @Override
    public void onViewScrolled(int position, float positionOffset) {
        isScrolling = mCurrentItem != position;
        isStop = true;
        mHandler.removeCallbacks(this);
    }

    @Override
    public void onViewSelected(int position) {
        isScrolling = false;
        mCurrentItem = position;
        if (isStop) {
            mHandler.postDelayed(HeaderView.this, 5000);
        }
        isStop = false;
    }

    @Override
    public void onViewStateChanged(int state) {
        isScrolling = state == BannerView.OnViewChangeListener.STATE_DRAGGING;
    }

    private class BannerAdapter extends BannerView.ViewAdapter {
        @Override
        public int getCount() {
            return mBanners.size();
        }

        @Override
        public View instantiateItem(ViewGroup parent, int position) {
            return HeaderView.this.instantiateItem(parent, position);
        }
    }
}
