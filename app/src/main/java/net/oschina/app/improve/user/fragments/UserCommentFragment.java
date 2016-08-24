package net.oschina.app.improve.user.fragments;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.base.fragments.BaseRecyclerViewFragment;
import net.oschina.app.improve.bean.Mention;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.simple.Comment;
import net.oschina.app.improve.tweet.activities.TweetDetailActivity;
import net.oschina.app.improve.user.adapter.UserMentionAdapter;

import java.lang.reflect.Type;

/**
 * Created by huanghaibin_dev
 * on 2016/8/16.
 */

public class UserCommentFragment extends BaseRecyclerViewFragment<Mention> {

    @Override
    protected void requestData() {
        super.requestData();
        OSChinaApi.getMsgCommentList(mIsRefresh ? null : mBean.getNextPageToken(), mHandler);
    }

    @Override
    public void onItemClick(int position, long itemId) {
        Mention mention = mAdapter.getItem(position);
        TweetDetailActivity.show(getContext(), mention.getId());
    }

    @Override
    protected BaseRecyclerAdapter<Mention> getRecyclerAdapter() {
        return new UserMentionAdapter(this);
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<Comment>>>() {
        }.getType();
    }
}
