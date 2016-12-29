package net.oschina.app.improve.user.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.base.fragments.BaseRecyclerViewFragment;
import net.oschina.app.improve.bean.Active;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.simple.Origin;
import net.oschina.app.improve.detail.general.BlogDetailActivity;
import net.oschina.app.improve.detail.general.EventDetailActivity;
import net.oschina.app.improve.detail.general.NewsDetailActivity;
import net.oschina.app.improve.detail.general.QuestionDetailActivity;
import net.oschina.app.improve.detail.general.SoftwareDetailActivity;
import net.oschina.app.improve.tweet.activities.TweetDetailActivity;
import net.oschina.app.improve.user.adapter.UserActiveAdapter;
import net.oschina.app.util.UIHelper;

import java.lang.reflect.Type;


/**
 * 某用户的动态(讨论)列表
 * Created by thanatos on 16/8/16.
 */
public class UserActiveFragment extends BaseRecyclerViewFragment<Active> {

    public static final String BUNDLE_KEY_USER_ID = "BUNDLE_KEY_USER_ID";

    private long uid;

    public static Fragment instantiate(Long uid) {
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_KEY_USER_ID, uid);
        Fragment fragment = new UserActiveFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initBundle(Bundle bundle) {
        super.initBundle(bundle);
        uid = bundle.getLong(BUNDLE_KEY_USER_ID);
    }

    @Override
    protected BaseRecyclerAdapter<Active> getRecyclerAdapter() {
        return new UserActiveAdapter(getContext());
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<Active>>>() {
        }.getType();
    }

    @Override
    protected void requestData() {
        String token = isRefreshing ? null : mBean.getNextPageToken();
        OSChinaApi.getUserActives(uid, token, mHandler);
    }

    @Override
    public void onItemClick(int position, long itemId) {
        Origin origin = mAdapter.getItem(position).getOrigin();
        if (origin == null)
            return;
        switch (origin.getType()) {
            case Origin.ORIGIN_TYPE_LINK:
                UIHelper.showUrlRedirect(getContext(), origin.getHref());
                break;
            case Origin.ORIGIN_TYPE_SOFTWARE:
                SoftwareDetailActivity.show(getContext(), origin.getId());
                break;
            case Origin.ORIGIN_TYPE_DISCUSS:
                QuestionDetailActivity.show(getContext(), origin.getId());
                break;
            case Origin.ORIGIN_TYPE_BLOG:
                BlogDetailActivity.show(getContext(), origin.getId());
                break;
            case Origin.ORIGIN_TYPE_TRANSLATION:
                NewsDetailActivity.show(getContext(), origin.getId());
                break;
            case Origin.ORIGIN_TYPE_ACTIVE:
                EventDetailActivity.show(getContext(), origin.getId());
                break;
            case Origin.ORIGIN_TYPE_NEWS:
                NewsDetailActivity.show(getContext(), origin.getId());
                break;
            case Origin.ORIGIN_TYPE_TWEETS:
                TweetDetailActivity.show(getContext(), origin.getId());
                break;
            default:
                // pass
        }
    }

    @Override
    protected boolean isNeedCache() {
        return false;
    }

    @Override
    protected boolean isNeedEmptyView() {
        return false;
    }
}
