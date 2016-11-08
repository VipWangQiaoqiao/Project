package net.oschina.app.improve.tweet.fragments;

import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.base.fragments.BaseGeneralRecyclerFragment;
import net.oschina.app.improve.tweet.adapter.TopicTweetAdapter;

import java.lang.reflect.Type;

/**
 * Created by thanatosx on 2016/11/7.
 */

public class TopicTweetFragment extends BaseGeneralRecyclerFragment {

    @Override
    protected BaseRecyclerAdapter getRecyclerAdapter() {
        return new TopicTweetAdapter(getContext());
    }

    @Override
    protected Type getType() {
        return null;
    }

    @Override
    public void initData() {
        super.initData();
        mAdapter.setState(BaseRecyclerAdapter.STATE_LOAD_MORE, true);
        mRefreshLayout.setRefreshing(false);
    }

    @Override
    protected boolean isNeedEmptyView() {
        return false;
    }

    @Override
    public void onRefreshing() {

    }

    @Override
    protected boolean isNeedCache() {
        return false;
    }
}
