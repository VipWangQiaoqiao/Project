package net.oschina.app.fragment.general;

import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.R;
import net.oschina.app.adapter.base.BaseListAdapter;
import net.oschina.app.adapter.general.EventAdapter;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.base.PageBean;
import net.oschina.app.bean.base.ResultBean;
import net.oschina.app.bean.event.Event;
import net.oschina.app.fragment.base.BaseListFragment;

import java.lang.reflect.Type;

/**
 * 活动界面
 */
public class EventFragment extends BaseListFragment<Event> {

    private View mHeaderView;

    private ViewPager vp_news;

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mHeaderView = LayoutInflater.from(getActivity()).inflate(R.layout.item_list_active_header, null);
        vp_news = (ViewPager) mHeaderView.findViewById(R.id.vp_news);
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
}
