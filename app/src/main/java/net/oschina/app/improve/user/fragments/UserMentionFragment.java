package net.oschina.app.improve.user.fragments;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.base.fragments.BaseRecyclerViewFragment;
import net.oschina.app.improve.bean.Mention;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.simple.Origin;
import net.oschina.app.improve.detail.activities.BlogDetailActivity;
import net.oschina.app.improve.detail.activities.EventDetailActivity;
import net.oschina.app.improve.detail.activities.NewsDetailActivity;
import net.oschina.app.improve.detail.activities.QuestionDetailActivity;
import net.oschina.app.improve.detail.activities.SoftwareDetailActivity;
import net.oschina.app.improve.tweet.activities.TweetDetailActivity;
import net.oschina.app.improve.user.adapter.UserMentionAdapter;
import net.oschina.app.util.UIHelper;

import java.lang.reflect.Type;

/**
 * Created by huanghaibin_dev
 * on 2016/8/16.
 */

public class UserMentionFragment extends BaseRecyclerViewFragment<Mention> {

    @Override
    protected void requestData() {
        super.requestData();
        OSChinaApi.getMsgMentionList(mIsRefresh ? null : mBean.getNextPageToken(), mHandler);
    }

    @Override
    public void onItemClick(int position, long itemId) {
        Mention mention = mAdapter.getItem(position);
        Origin origin = mention.getOrigin();
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
    protected BaseRecyclerAdapter<Mention> getRecyclerAdapter() {
        return new UserMentionAdapter(this);
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<Mention>>>() {
        }.getType();
    }
}
