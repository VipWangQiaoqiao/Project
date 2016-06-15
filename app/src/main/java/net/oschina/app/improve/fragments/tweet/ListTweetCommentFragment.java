package net.oschina.app.improve.fragments.tweet;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;

import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.Comment;
import net.oschina.app.bean.CommentList;
import net.oschina.app.improve.adapter.base.BaseRecyclerAdapter;
import net.oschina.app.improve.adapter.tweet.TweetCommentAdapter;
import net.oschina.app.improve.contract.TweetDetailContract;
import net.oschina.app.improve.fragments.base.BaseRecyclerViewFragment;
import net.oschina.app.util.XmlUtils;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by thanatos on 16/6/13.
 */
public class ListTweetCommentFragment extends BaseRecyclerViewFragment<Comment>
        implements TweetCommentAdapter.OnClickReplyCallback, TweetDetailContract.CmnView {

    private TweetDetailContract.Operator mOperator;
    private int pageNum = 0;
    private AsyncHttpResponseHandler reqHandler;

    public static ListTweetCommentFragment instantiate(TweetDetailContract.Operator operator){
        ListTweetCommentFragment fragment = new ListTweetCommentFragment();
        fragment.mOperator = operator;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reqHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                CommentList data = XmlUtils.toBean(CommentList.class, responseBody);
                setListData(data.getList());
                onRequestSuccess(1);
                onRequestFinish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("oschina", error.getMessage());
            }
        };
    }

    @Override
    protected BaseRecyclerAdapter<Comment> getRecyclerAdapter() {
        TweetCommentAdapter adapter = new TweetCommentAdapter(getContext());
        adapter.setOnClickReplyCallback(this);
        return adapter;
    }

    @Override
    protected Type getType() {
        return new TypeToken<CommentList>() {}.getType();
    }

    @Override
    public void onLoadMore() {
        requestData(pageNum + 1);
    }

    @Override
    protected void requestData() {
        requestData(0);
    }

    @Override
    protected void onRequestSuccess(int code) {
        super.onRequestSuccess(code);
        ++pageNum;
    }

    private void requestData(int pageNum){
        OSChinaApi.getCommentList(mOperator.getTweetDetail().getId(), 3, pageNum, reqHandler);
    }

    private void setListData(List<Comment> comments){
        if (mIsRefresh) {
            //cache the time
            mAdapter.clear();
            mAdapter.addAll(comments);
            mRefreshLayout.setCanLoadMore(true);
        } else {
            mAdapter.addAll(comments);
        }
        if (comments.size() < 20) {
            mAdapter.setState(BaseRecyclerAdapter.STATE_NO_MORE, true);
        }
    }

    @Override
    public void onReplyOther(Comment comment) {
        mOperator.toReply(comment);
    }

    @Override
    public void onCommentSuccess(Comment comment) {
        onRefreshing();
    }
}
