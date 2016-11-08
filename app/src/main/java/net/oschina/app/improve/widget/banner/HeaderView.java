package net.oschina.app.improve.widget.banner;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.RequestManager;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.Banner;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.widget.indicator.CirclePagerIndicator;

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

    public HeaderView(Context context, RequestManager loader, String api) {
        super(context);
        mImageLoader = loader;
        this.mUrl = api;
        init(context);
    }

    protected void init(Context context) {
        mHandler = new Handler();
        mBanners = new ArrayList<>();
        LayoutInflater.from(context).inflate(getLayoutId(), this, true);
        mViewPager = (ViewPager) findViewById(R.id.vp_banner);
        mIndicator = (CirclePagerIndicator) findViewById(R.id.indicator);
        mAdapter = new BannerAdapter();
        mViewPager.setAdapter(mAdapter);
        mCallBack = new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultBean<List<Banner>> result = AppOperator.createGson().fromJson(responseString,
                            new TypeToken<List<PageBean<Banner>>>() {
                            }.getType());
                    if (result != null && result.isSuccess()) {
                        setBanners(result.getResult());
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
        mCurrentItem = (mCurrentItem + 1) % mBanners.size();
        mViewPager.setCurrentItem(mCurrentItem);
        mHandler.postDelayed(this, 5000);
    }

    public void requestBanner() {
        OSChinaApi.getBanner(mUrl, mCallBack);
    }

    private void setBanners(List<Banner> banners) {
        if (banners != null) {
            mHandler.removeCallbacks(this);
            mBanners.clear();
            mBanners.addAll(banners);
            mCurrentItem = 0;
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

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

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
