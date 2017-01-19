package net.oschina.app.improve.user.event;

import net.oschina.app.improve.base.BaseRecyclerFragment;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.detail.general.EventDetailActivity;

/**
 * Created by haibin
 * on 2017/1/18.
 */

public class UserEventFragment extends BaseRecyclerFragment<UserEventContract.Presenter, SubBean>
        implements UserEventContract.View {

    public static UserEventFragment newInstance() {
        return new UserEventFragment();
    }

    @Override
    protected void onItemClick(SubBean subBean, int position) {
        EventDetailActivity.show(mContext, subBean);
    }

    @Override
    protected BaseRecyclerAdapter<SubBean> getAdapter() {
        return new UserEventAdapter(this);
    }
}
