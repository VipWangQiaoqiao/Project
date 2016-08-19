package net.oschina.app.improve.user.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.base.fragments.BaseRecyclerViewFragment;
import net.oschina.app.improve.bean.Blog;
import net.oschina.app.improve.bean.Tweet;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.detail.activities.BlogDetailActivity;
import net.oschina.app.improve.tweet.activities.TweetDetailActivity;
import net.oschina.app.improve.user.adapter.UserBlogAdapter;
import net.oschina.app.improve.user.adapter.UserTweetAdapter;

import java.lang.reflect.Type;

/**
 * created by fei  on 2016/8/16.
 * desc: question list module
 */
public class UserTweetFragment extends BaseRecyclerViewFragment<Tweet> {

    public static final String BUNDLE_KEY_USER_ID = "BUNDLE_KEY_USER_ID";
    private long userId;

    public static Fragment instantiate(long uid){
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_KEY_USER_ID, uid);
        Fragment fragment = new UserTweetFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initBundle(Bundle bundle) {
        super.initBundle(bundle);
        userId = bundle.getLong(BUNDLE_KEY_USER_ID, 0);
    }

    @Override
    protected void requestData() {
        OSChinaApi.getUserTweetList(userId, null, mHandler);
    }

    @Override
    protected BaseRecyclerAdapter<Tweet> getRecyclerAdapter() {
        return new UserTweetAdapter(getContext(), BaseRecyclerAdapter.ONLY_FOOTER);
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<Tweet>>>() {
        }.getType();
    }

    @Override
    protected boolean isNeedCache() {
        return false;
    }

    @Override
    protected boolean isNeedEmptyView() {
        return false;
    }

    @Override
    public void onItemClick(int position, long itemId) {
        Tweet tweet = mAdapter.getItem(position);
        TweetDetailActivity.show(getActivity(), tweet.getId());
    }

    @Override
    public void onLoadMore() {
        OSChinaApi.getUserTweetList(userId, mBean.getNextPageToken(), mHandler);
    }
}
