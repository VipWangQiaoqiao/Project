package net.oschina.app.fragment.general;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import net.oschina.app.fragment.base.BaseListFragment;
import net.oschina.app.widget.ViewEventBanner;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 活动界面
 */
public class EventFragment extends BaseListFragment<Event> {

    private View mHeaderView;

    private ViewPager vp_news;

    private List<ViewEventBanner> banners = new ArrayList<>();

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mHeaderView = LayoutInflater.from(getActivity()).inflate(R.layout.item_list_active_header, null);
        vp_news = (ViewPager) mHeaderView.findViewById(R.id.vp_news);
        OSChinaApi.getBannerList(OSChinaApi.CATALOG_BANNER_EVENT, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultBean<PageBean<Banner>> resultBean = AppContext.createGson().fromJson(responseString, new TypeToken<ResultBean<PageBean<Banner>>>() {
                    }.getType());
                    if (resultBean != null) {
                        for (Banner banner : resultBean.getResult().getItems()) {
                            ViewEventBanner viewNewsBanner = new ViewEventBanner(getActivity());
                            viewNewsBanner.initData(getImgLoader(), banner);
                            banners.add(viewNewsBanner);
                        }
                        vp_news.setAdapter(new EventPagerAdapter());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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
    protected BaseListAdapter<Event> getListAdapter() {
        return new EventAdapter(this);
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<Event>>>() {
        }.getType();
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
