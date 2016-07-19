package net.oschina.app.improve.tweet.fragments;

import android.os.Bundle;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.AppContext;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.base.adapter.BaseListAdapter;
import net.oschina.app.improve.base.fragments.BaseListFragment;
import net.oschina.app.improve.bean.Tweet;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.tweet.adapter.TweetAdapter;

import java.lang.reflect.Type;

/**
 * 动弹列表
 * Created by huanghaibin_dev
 * on 2016/7/18.
 */
public class TweetFragment extends BaseListFragment<Tweet> {
    public static final int CATEGORY_TYPE = 1; //请求最新或者最热
    public static final int CATEGORY_USER = 2; //请求用户

    public static final int TWEET_TYPE_NEW = 1;
    public static final int TWEET_TYPE_HOT = 2;

    public static final String CACHE_NEW_TWEET = "cache_new_tweet";
    public static final String CACHE_HOT_TWEET = "cache_hot_tweet";
    public static final String CACHE_USER_TWEET = "cache_user_tweet";

    public int requestType;
    public int tweetType;

    @Override
    protected void initBundle(Bundle bundle) {
        super.initBundle(bundle);
        requestType = bundle.getInt("requestType", CATEGORY_TYPE);
        tweetType = bundle.getInt("tweetType", TWEET_TYPE_NEW);
    }

    /**
     * fragment被销毁的时候重新调用，初始化保存的数据
     *
     * @param bundle onSaveInstanceState
     */
    @Override
    protected void onRestartInstance(Bundle bundle) {
        super.onRestartInstance(bundle);
        requestType = bundle.getInt("requestType", 1);
        tweetType = bundle.getInt("tweetType", 1);
    }

    @Override
    protected void initData() {
        super.initData();
        switch (requestType) {
            case CATEGORY_TYPE:
                CACHE_NAME = tweetType == TWEET_TYPE_NEW ? CACHE_NEW_TWEET : CACHE_HOT_TWEET;
                break;
            case CATEGORY_USER:
                CACHE_NAME = CACHE_USER_TWEET;
                break;
        }
    }

    @Override
    protected void requestData() {
        super.requestData();
        switch (requestType) {
            case CATEGORY_TYPE:
                OSChinaApi.getTweetList(tweetType, mBean.getNextPageToken(), mHandler);
                break;
            case CATEGORY_USER:
                OSChinaApi.getTweetList(AppContext.getInstance().getLoginUid(), mBean.getNextPageToken(), mHandler);
                break;
        }
    }

    @Override
    protected BaseListAdapter getListAdapter() {
        return new TweetAdapter(this);
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<Tweet>>>() {
        }.getType();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("requestType", requestType);
        outState.putInt("tweetType", tweetType);
        super.onSaveInstanceState(outState);
    }
}
