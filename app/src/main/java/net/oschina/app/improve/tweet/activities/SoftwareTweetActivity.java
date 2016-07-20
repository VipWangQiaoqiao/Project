package net.oschina.app.improve.tweet.activities;

import android.os.Bundle;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.base.activities.BaseRecyclerViewActivity;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.Tweet;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.tweet.adapter.SoftwareTweetAdapter;

import java.lang.reflect.Type;

import static net.oschina.app.improve.base.adapter.BaseRecyclerAdapter.ONLY_FOOTER;

/**
 * Created by fei on 2016/7/20.
 */

public class SoftwareTweetActivity extends BaseRecyclerViewActivity<Tweet> {

    public static final String BUNDLE_KEY_NAME = "bundle_key_name";
    private String softwareName;

    @Override
    protected boolean initBundle(Bundle bundle) {
        softwareName = bundle.getString(BUNDLE_KEY_NAME);
        return super.initBundle(bundle);
    }


    @Override
    protected void requestData() {
        super.requestData();
        OSChinaApi.getSoftwareTweetList(softwareName, mIsRefresh ? mBean.getPrevPageToken() : mBean.getNextPageToken(), mHandler);
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<Tweet>>>() {
        }.getType();
    }

    @Override
    protected BaseRecyclerAdapter<Tweet> getRecyclerAdapter() {
        return new SoftwareTweetAdapter(this, ONLY_FOOTER);
    }

    @Override
    protected void onItemClick(Tweet item, int position) {
        super.onItemClick(item, position);
        TweetDetailActivity.show(this, item);
    }
}
