package net.oschina.app.improve.main.banner;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
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

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by haibin
 * on 2016/10/26.
 */

public abstract class HeaderView extends RelativeLayout implements BannerView.OnBannerChangeListener,
        Runnable,BannerView.OnBannerItemClicklistener {
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

    public HeaderView(Context context, RequestManager loader, String api, String bannerCache) {
        super(context);
        mImageLoader = loader;
        this.mUrl = api;
        this.mBannerCache = bannerCache;
        init(context);
    }

    protected void init(Context context) {
        mBanners = new ArrayList<>();
        List<Banner> banners = CacheManager.readListJson(context, mBannerCache, Banner.class);
        if (banners != null) {
            mBanners.addAll(banners);
            if (mHandler == null)
                mHandler = new Handler();
            mHandler.postDelayed(this, 5000);
        }
        LayoutInflater.from(context).inflate(getLayoutId(), this, true);
        mBannerView = (BannerView) findViewById(R.id.bannerView);
        mBannerView.setBannerOnItemClickListener(this);
        mIndicator = (CircleBannerIndicator) findViewById(R.id.indicator);
        mAdapter = new BannerAdapter();
        mBannerView.addOnBannerChangeListener(this);
        mBannerView.setAdapter(mAdapter);
        mIndicator.bindBannerView(mBannerView);
        mCallBack = new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    final ResultBean<PageBean<Banner>> result = AppOperator.createGson().fromJson(responseString,
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
        mCurrentItem = mCurrentItem + 1;
        mBannerView.scrollToNext();
        // mBannerView.setCurrentItem(mCurrentItem);
    }

    public void requestBanner() {
        if (mHandler == null)
            mHandler = new Handler();
        mHandler.removeCallbacks(this);
        OSChinaApi.getBanner(mUrl, mCallBack);
    }

    void setBanners(List<Banner> banners) {
        if (banners != null) {
            mHandler.removeCallbacks(this);
            mBanners.clear();
            mBanners.addAll(banners);
            mBannerView.getAdapter().notifyDataSetChanged();
            //mIndicator.setCount(mBanners.size());
            mIndicator.notifyDataSetChange();
            if (mBanners.size() > 1) {
                mHandler.postDelayed(this, 5000);
            }
        }
    }

    protected abstract int getLayoutId();

    protected abstract View instantiateItem(int position);

    @Override
    public void onViewScrolled(int position, float positionOffset) {
        isScrolling = mCurrentItem != position;
    }

    @Override
    public void onViewSelected(int position) {
        isScrolling = false;
        mCurrentItem = position;
    }

    @Override
    public void onViewStateChanged(int state) {
        isScrolling = state != BannerView.OnBannerChangeListener.STATE_IDLE;
    }

    private class BannerAdapter extends BaseBannerAdapter {

        @Override
        public View instantiateItem(int position) {
            return HeaderView.this.instantiateItem(position);
        }

        @Override
        public int getCount() {
            return mBanners.size();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mHandler == null)
            mHandler = new Handler();
        mHandler.postDelayed(this, 5000);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mHandler == null)
            return;
        mHandler.removeCallbacks(this);
        mHandler = null;
    }
}
