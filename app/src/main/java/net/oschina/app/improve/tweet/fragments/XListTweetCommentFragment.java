package net.oschina.app.improve.tweet.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.Comment;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.base.fragments.BaseRecyclerViewFragment;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.simple.TweetComment;
import net.oschina.app.improve.tweet.adapter.TweetCommentAdapter;
import net.oschina.app.improve.tweet.adapter.XTweetCommentAdapter;
import net.oschina.app.improve.tweet.contract.TweetDetailContract;
import net.oschina.app.util.DialogHelp;
import net.oschina.app.util.HTMLUtil;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by thanatos
 * on 16/6/13.
 */
public class XListTweetCommentFragment extends BaseRecyclerViewFragment<TweetComment>
        implements TweetDetailContract.ICmnView, BaseRecyclerAdapter.OnItemLongClickListener {

    private TweetDetailContract.Operator mOperator;
    private TweetDetailContract.IAgencyView mAgencyView;
    private int pageNum = 0;
    private int mDeleteIndex = 0;
    private Dialog mDeleteDialog;
    private TextHttpResponseHandler reqHandler;

    public static XListTweetCommentFragment instantiate(TweetDetailContract.Operator operator, TweetDetailContract.IAgencyView mAgencyView) {
        XListTweetCommentFragment fragment = new XListTweetCommentFragment();
        fragment.mOperator = operator;
        fragment.mAgencyView = mAgencyView;
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mOperator = (TweetDetailContract.Operator) activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reqHandler = new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mRefreshLayout.onComplete();
                mAdapter.setState(BaseRecyclerAdapter.STATE_LOAD_ERROR, true);
                Log.d("oschina", throwable.getMessage() == null ? "请求失败" : throwable.getMessage());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                ResultBean<PageBean<TweetComment>> result = AppContext.createGson().fromJson(
                        responseString, new TypeToken<ResultBean<PageBean<TweetComment>>>(){}.getType());
                if (result.isSuccess()){
                    List<TweetComment> comments = result.getResult().getItems();
                    setListData(comments);
                    onRequestSuccess(1);
                    onRequestFinish();
                    if (mAdapter.getCount() < 20 && mAgencyView != null)
                        mAgencyView.resetCmnCount(mAdapter.getCount());
                }else{
                    onFailure(statusCode, headers, responseString, null);
                }
            }
        };
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    mOperator.onScroll();
                }
            }
        });
    }

    @Override
    protected BaseRecyclerAdapter<TweetComment> getRecyclerAdapter() {
        XTweetCommentAdapter adapter = new XTweetCommentAdapter(getContext());
        adapter.setOnItemClickListener(this);
        adapter.setOnItemLongClickListener(this);
        return adapter;
    }

    @Override
    protected Type getType() {
        return null;
    }

    @Override
    public void onLoadMore() {
        requestData(pageNum);
    }

    @Override
    protected void requestData() {
        requestData(0);
    }

    @Override
    protected void onRequestSuccess(int code) {
        super.onRequestSuccess(code);
        if (mIsRefresh) pageNum = 0;
        ++pageNum;
    }

    private void requestData(int pageNum) {
        OSChinaApi.getTweetCommentList(mOperator.getTweetDetail().getId(), reqHandler);
    }

    private void setListData(List<TweetComment> comments) {
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

    /*@Override
    public void onCommentSuccess(TweetComment comment) {
        onRefreshing();
    }*/



    @Override
    public void onItemClick(int position, long itemId) {
        super.onItemClick(position, itemId);
//        mOperator.toReply(mAdapter.getItem(position));
    }

    @Override
    public void onLongClick(int position, long itemId) {
        final TweetComment comment = mAdapter.getItem(position);
        if (comment == null) return;
        int itemsLen = comment.getAuthor().getId() == AppContext.getInstance().getLoginUid() ? 2 : 1;
        String[] items = new String[itemsLen];
        items[0] = getResources().getString(R.string.copy);
        if (itemsLen == 2) {
            items[1] = getResources().getString(R.string.delete);
        }
        mDeleteIndex = position;
        DialogHelp.getSelectDialog(getActivity(), items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    TDevice.copyTextToBoard(HTMLUtil.delHTMLTag(comment.getContent()));
                } else if (i == 1) {
                    handleDeleteComment(comment);
                }
            }
        }).show();
    }

    private void handleDeleteComment(TweetComment comment) {
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.showLoginActivity(getActivity());
            return;
        }
        mDeleteDialog = DialogHelp.getWaitDialog(getContext(), "正在删除...");
        OSChinaApi.deleteTweetComment(mOperator.getTweetDetail().getId(), comment.getId(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getContext(), "删除失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                ResultBean result = AppContext.createGson().fromJson(
                        responseString, new TypeToken<ResultBean>(){}.getType());
                if (result.isSuccess()) {
                    mAdapter.removeItem(mDeleteIndex);
                    int count = mOperator.getTweetDetail().getCommentCount() - 1;
                    mOperator.getTweetDetail().setCommentCount(count); // Bean就这样写的,我也不知道为什么!!!!
                    mAgencyView.resetCmnCount(count);
                } else {
                    Toast.makeText(getContext(), "删除失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (mDeleteDialog != null) {
                    mDeleteDialog.dismiss();
                    mDeleteDialog = null;
                }
            }
        });
    }

    @Override
    public void onCommentSuccess(Comment comment) {

    }
}
