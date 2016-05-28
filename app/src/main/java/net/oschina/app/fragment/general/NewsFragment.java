package net.oschina.app.fragment.general;

import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.adapter.base.BaseListAdapter;
import net.oschina.app.adapter.general.NewsAdapter;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.Banner;
import net.oschina.app.bean.base.PageBean;
import net.oschina.app.bean.base.ResultBean;
import net.oschina.app.bean.news.News;
import net.oschina.app.cache.CacheManager;
import net.oschina.app.fragment.base.BaseListFragment;
import net.oschina.app.util.UIHelper;
import net.oschina.app.widget.SmoothScroller;
import net.oschina.app.widget.ViewNewsBanner;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;

/**
 * 资讯界面
 */
public class NewsFragment extends BaseListFragment<News> {

    private static final String NEWS_BANNER = "news_banner";

    private View mHeaderView;

    private ViewPager vp_news;

    private List<ViewNewsBanner> banners = new ArrayList<>();

    private NewsPagerAdapter mPagerAdapter;

    private Handler handler;

    private int mCurrentItem = 0;

    private ScheduledExecutorService mSchedule;

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mHeaderView = LayoutInflater.from(getActivity()).inflate(R.layout.item_list_news_header, null);
        vp_news = (ViewPager) mHeaderView.findViewById(R.id.vp_news);
        mPagerAdapter = new NewsPagerAdapter();
        vp_news.setAdapter(mPagerAdapter);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                vp_news.setCurrentItem(mCurrentItem);
            }
        };

        mExeService.submit(new Runnable() {
            @Override
            public void run() {
                final PageBean<Banner> pageBean = (PageBean<Banner>) CacheManager.readObject(getActivity(), NEWS_BANNER);
                if (pageBean != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            initBanner(pageBean, true);
                        }
                    });
                }
            }
        });

        OSChinaApi.getBannerList(OSChinaApi.CATALOG_BANNER_NEWS, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    final ResultBean<PageBean<Banner>> resultBean = AppContext.createGson().fromJson(responseString, new TypeToken<ResultBean<PageBean<Banner>>>() {
                    }.getType());
                    if (resultBean != null && resultBean.isSuccess()) {
                        mExeService.submit(new Runnable() {
                            @Override
                            public void run() {
                                CacheManager.saveObject(getActivity(), resultBean.getResult(), NEWS_BANNER);
                            }
                        });
                        initBanner(resultBean.getResult(), false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mSchedule = Executors.newSingleThreadScheduledExecutor();
        mSchedule.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                mCurrentItem = (mCurrentItem + 1) % banners.size();
                handler.obtainMessage().sendToTarget();
            }
        }, 2, 5, TimeUnit.SECONDS);

        new SmoothScroller(getActivity()).setViewPagerScroller(vp_news, getActivity());
        mListView.addHeaderView(mHeaderView);

    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void requestData() {
        super.requestData();
        OSChinaApi.getNewsList(mIsRefresh ? mBeam.getPrevPageToken() : mBeam.getNextPageToken(), mHandler);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        News news = mAdapter.getItem(position - 1);
        if (news != null) {
            UIHelper.showNewsDetail(getActivity(), news);
        }
    }

    @Override
    protected BaseListAdapter<News> getListAdapter() {
        return new NewsAdapter(this);
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<News>>>() {
        }.getType();
    }

    private void initBanner(PageBean<Banner> result, boolean fromCache) {
        if (fromCache) banners.clear();
        for (Banner banner : result.getItems()) {
            ViewNewsBanner viewNewsBanner = new ViewNewsBanner(getActivity());
            viewNewsBanner.initData(getImgLoader(), banner);
            banners.add(viewNewsBanner);
        }
        mPagerAdapter.notifyDataSetChanged();
    }

    private class NewsPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return banners.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(banners.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(banners.get(position));
            return banners.get(position);
        }
    }
}
