package net.oschina.app.improve.widget.banner;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import net.oschina.app.improve.widget.indicator.CirclePagerIndicator;
import net.oschina.app.widget.SmoothScroller;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by haibin
 * on 2016/10/26.
 */

public abstract class HeaderView extends RelativeLayout implements ViewPager.OnPageChangeListener, Runnable {
    protected ViewPager mViewPager;
    protected CirclePagerIndicator mIndicator;
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
        mHandler = new Handler();
        mBanners = new ArrayList<>();
        List<Banner> banners = CacheManager.readFromJson(context, mBannerCache, Banner.class);
        if (banners != null){
            mBanners.addAll(banners);
            mHandler.postDelayed(this,5000);
        }
        LayoutInflater.from(context).inflate(getLayoutId(), this, true);
        mViewPager = (ViewPager) findViewById(R.id.vp_banner);
        mIndicator = (CirclePagerIndicator) findViewById(R.id.indicator);
        mAdapter = new BannerAdapter();
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setAdapter(mAdapter);

        mIndicator.bindViewPager(mViewPager);
        new SmoothScroller(getContext()).bingViewPager(mViewPager);
        mViewPager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        isScrolling = false;
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        isScrolling = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        isScrolling = true;
                        break;
                }
                return false;
            }
        });
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
        if (isScrolling)
            return;
        mCurrentItem = (mCurrentItem + 1) % mBanners.size();
        mViewPager.setCurrentItem(mCurrentItem, true);
        mHandler.postDelayed(this, 5000);
    }

    public void requestBanner() {
        OSChinaApi.getBanner(mUrl, mCallBack);
    }

    void setBanners(List<Banner> banners) {
        if (banners != null) {
            mHandler.removeCallbacks(this);
            mBanners.clear();
            mBanners.addAll(banners);
            mViewPager.getAdapter().notifyDataSetChanged();
            mIndicator.notifyDataSetChanged();
            mCurrentItem = 0;
            mViewPager.setCurrentItem(mCurrentItem, true);
            if (mBanners.size() > 1) {
                mHandler.postDelayed(this, 5000);
            }
        }
    }

    protected abstract int getLayoutId();

    protected abstract Object instantiateItem(ViewGroup container, int position);

    protected abstract void destroyItem(ViewGroup container, int position, Object object);

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        isScrolling = mCurrentItem != position;
    }

    @Override
    public void onPageSelected(int position) {
        isScrolling = false;
        mCurrentItem = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        isScrolling = state != ViewPager.SCROLL_STATE_IDLE;
    }

    private class BannerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mBanners.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return HeaderView.this.instantiateItem(container, position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            HeaderView.this.destroyItem(container, position, object);
        }
    }
}
