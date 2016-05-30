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
import net.oschina.app.adapter.general.EventAdapter;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.Banner;
import net.oschina.app.bean.base.PageBean;
import net.oschina.app.bean.base.ResultBean;
import net.oschina.app.bean.event.Event;
import net.oschina.app.cache.CacheManager;
import net.oschina.app.fragment.base.BaseListFragment;
import net.oschina.app.util.UIHelper;
import net.oschina.app.widget.SmoothScroller;
import net.oschina.app.widget.ViewEventBanner;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;

/**
 * 活动界面
 */
public class EventFragment extends BaseListFragment<Event> {

    private static final String EVENT_BANNER = "event_banner";
    private View mHeaderView;

    private ViewPager vp_event;

    private List<ViewEventBanner> banners = new ArrayList<>();

    private EventPagerAdapter pagerAdapter;

    private Handler handler;

    private int mCurrentItem = 0;

    private ScheduledExecutorService mSchedule;

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mHeaderView = LayoutInflater.from(getActivity()).inflate(R.layout.item_list_active_header, null);
        vp_event = (ViewPager) mHeaderView.findViewById(R.id.vp_news);
        pagerAdapter = new EventPagerAdapter();
        vp_event.setAdapter(pagerAdapter);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                vp_event.setCurrentItem(mCurrentItem);
            }
        };
        mExeService.submit(new Runnable() {
            @Override
            public void run() {
                final PageBean<Banner> pageBean = (PageBean<Banner>) CacheManager.readObject(getActivity(), EVENT_BANNER);
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
        OSChinaApi.getBannerList(OSChinaApi.CATALOG_BANNER_EVENT, new TextHttpResponseHandler() {
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
                                CacheManager.saveObject(getActivity(), resultBean.getResult(), EVENT_BANNER);
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
        new SmoothScroller(getActivity()).setViewPagerScroller(vp_event, getActivity());
        mListView.addHeaderView(mHeaderView);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void requestData() {
        super.requestData();
        OSChinaApi.getEventList(mIsRefresh ? mBeam.getPrevPageToken() : mBeam.getNextPageToken(), mHandler);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Event event = mAdapter.getItem(position);
        if (event != null)
            UIHelper.showEventDetail(view.getContext(), Integer.parseInt(String.valueOf(event.getId())));
    }

    @Override
    protected BaseListAdapter<Event> getListAdapter() {
        return new EventAdapter(this);
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<Event>>>() {
        }.getType();
    }

    private void initBanner(PageBean<Banner> result, boolean fromCache) {
        if (fromCache) banners.clear();
        for (Banner banner : result.getItems()) {
            ViewEventBanner viewNewsBanner = new ViewEventBanner(getActivity());
            viewNewsBanner.initData(getImgLoader(), banner);
            banners.add(viewNewsBanner);
        }
        pagerAdapter.notifyDataSetChanged();
    }

    private class EventPagerAdapter extends PagerAdapter {
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
