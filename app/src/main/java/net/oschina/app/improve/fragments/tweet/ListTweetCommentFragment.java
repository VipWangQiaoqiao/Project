package net.oschina.app.improve.fragments.tweet;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;

import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.Comment;
import net.oschina.app.bean.CommentList;
import net.oschina.app.bean.TweetLikeUserList;
import net.oschina.app.bean.User;
import net.oschina.app.improve.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.adapter.TweetCommentAdapter;
import net.oschina.app.improve.adapter.TweetLikeUsersAdapter;
import net.oschina.app.improve.fragments.base.BaseRecyclerViewFragment;
import net.oschina.app.util.XmlUtils;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by thanatos on 16/6/13.
 */
public class ListTweetCommentFragment extends BaseRecyclerViewFragment<Comment>{

    public static final String BUNDLE_KEY_TWEET_ID = "BUNDLE_KEY_TWEET_ID";

    private int tid;
    private int pageNum = 0;
    private AsyncHttpResponseHandler reqHandler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tid = getArguments().getInt(BUNDLE_KEY_TWEET_ID, 0);

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
        return new TweetCommentAdapter(getContext());
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
        OSChinaApi.getCommentList(tid, 3, pageNum, reqHandler);
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
}
