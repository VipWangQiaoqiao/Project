package net.oschina.app.improve.general.fragments;

import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.Banner;
import net.oschina.app.cache.CacheManager;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.base.adapter.BaseListAdapter;
import net.oschina.app.improve.base.fragments.BaseGeneralListFragment;
import net.oschina.app.improve.bean.Event;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.detail.activities.EventDetailActivity;
import net.oschina.app.improve.general.adapter.EventAdapter;
import net.oschina.app.improve.widget.ViewEventHeader;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

/**
 * 活动界面
 */
public class EventFragment extends BaseGeneralListFragment<Event> {

    private boolean isFirst = true;
    private static final String EVENT_BANNER = "event_banner";
    private ViewEventHeader mHeaderView;
    public static final String HISTORY_EVENT = "history_event";
    private Handler handler = new Handler();

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mHeaderView = new ViewEventHeader(getActivity());

        AppOperator.runOnThread(new Runnable() {
            @SuppressWarnings("unchecked")
            @Override
            public void run() {
                final PageBean<Banner> pageBean = (PageBean<Banner>) CacheManager.readObject(getActivity(), EVENT_BANNER);
                if (pageBean != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mHeaderView.initData(getImgLoader(), pageBean.getItems());
                        }
                    });
                }
            }
        });


        mHeaderView.setRefreshLayout(mRefreshLayout);
        mListView.addHeaderView(mHeaderView);

        getBannerList();
    }

    @Override
    public void onRefreshing() {
        super.onRefreshing();
        if (!isFirst)
            getBannerList();
    }

    @Override
    protected void requestData() {
        super.requestData();
        OSChinaApi.getEventList(mIsRefresh ? mBean.getPrevPageToken() : mBean.getNextPageToken(), mHandler);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Event event = mAdapter.getItem(position - 1);
        if (event != null) {
            EventDetailActivity.show(getActivity(), event.getId());
            TextView title = (TextView) view.findViewById(R.id.tv_event_title);
            updateTextColor(title, null);
            saveToReadedList(HISTORY_EVENT, String.valueOf(event.getId()));
        }
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

    @Override
    protected void onRequestFinish() {
        super.onRequestFinish();
        isFirst = false;
    }

    private void getBannerList() {
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
                        AppOperator.runOnThread(new Runnable() {
                            @Override
                            public void run() {
                                CacheManager.saveObject(getActivity(), resultBean.getResult(), EVENT_BANNER);
                            }
                        });
                        mHeaderView.initData(getImgLoader(), resultBean.getResult().getItems());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
