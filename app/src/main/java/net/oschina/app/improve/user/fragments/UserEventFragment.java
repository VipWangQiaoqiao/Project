package net.oschina.app.improve.user.fragments;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.AppConfig;
import net.oschina.app.OSCApplication;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.base.fragments.BaseGeneralRecyclerFragment;
import net.oschina.app.improve.bean.News;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.bean.User;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.detail.activities.EventDetailActivity;
import net.oschina.app.improve.user.adapter.UserEventAdapter;
import net.oschina.app.util.UIHelper;

import java.lang.reflect.Type;


/**
 * Created by fei
 * on 2016/12/2.
 * desc:
 */
public class UserEventFragment extends BaseGeneralRecyclerFragment<SubBean> {

    private static final String TAG = "UserEventFragment";

    private OSCApplication.ReadState mReadState;

    /**
     * init fragment
     *
     * @return fragment
     */
    public static UserEventFragment newInstance() {
        return new UserEventFragment();
        //return (UserEventFragment) Fragment.instantiate(context, UserEventFragment.class.getClass().getName(), bundle);
    }


    @Override
    public void initData() {
        CACHE_NAME = UserEventFragment.class.getName();
        mReadState = OSCApplication.getReadState("sub_list");
        super.initData();
        mAdapter.setSystemTime(AppConfig.getAppConfig(getActivity()).get("system_time"));
    }

    @Override
    public void onItemClick(int position, long itemId) {
        SubBean sub = mAdapter.getItem(position);
        if (sub == null)
            return;
        switch (sub.getType()) {
            case News.TYPE_EVENT:
                EventDetailActivity.show(mContext, sub.getId());
                break;
            default:
                UIHelper.showUrlRedirect(mContext, sub.getHref());
                break;
        }
        mReadState.put(sub.getKey());
        mAdapter.updateItem(position);
    }

    @Override
    public void onRefreshing() {
        super.onRefreshing();
    }

    @Override
    protected void requestData() {
        super.requestData();
        User user = AccountHelper.getUser();
        if (user != null)
            OSChinaApi.getUserEvent(user.getId(), user.getName(), isRefreshing ? null : mBean.getNextPageToken(), mHandler);
    }

    @Override
    protected void setListData(ResultBean<PageBean<SubBean>> resultBean) {
        super.setListData(resultBean);
        mAdapter.setSystemTime(resultBean.getTime());
    }

    @Override
    protected BaseRecyclerAdapter<SubBean> getRecyclerAdapter() {
        return new UserEventAdapter(this, BaseRecyclerAdapter.ONLY_FOOTER);
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<SubBean>>>() {
        }.getType();
    }

    @Override
    protected Class<SubBean> getCacheClass() {
        return SubBean.class;
    }

}
