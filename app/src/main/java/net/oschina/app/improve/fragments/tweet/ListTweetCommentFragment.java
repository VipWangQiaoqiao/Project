package net.oschina.app.improve.fragments.tweet;

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
import com.loopj.android.http.AsyncHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.Comment;
import net.oschina.app.bean.CommentList;
import net.oschina.app.bean.Result;
import net.oschina.app.bean.ResultBean;
import net.oschina.app.improve.adapter.base.BaseRecyclerAdapter;
import net.oschina.app.improve.adapter.tweet.TweetCommentAdapter;
import net.oschina.app.improve.detail.contract.TweetDetailContract;
import net.oschina.app.improve.fragments.base.BaseRecyclerViewFragment;
import net.oschina.app.util.DialogHelp;
import net.oschina.app.util.HTMLUtil;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;
import net.oschina.app.util.XmlUtils;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by thanatos on 16/6/13.
 */
public class ListTweetCommentFragment extends BaseRecyclerViewFragment<Comment>
        implements TweetDetailContract.ICmnView, BaseRecyclerAdapter.OnItemLongClickListener {

    private TweetDetailContract.Operator mOperator;
    private TweetDetailContract.IAgencyView mAgencyView;
    private int pageNum = 0;
    private int mDeleteIndex = 0;
    private Dialog mDeleteDialog;
    private AsyncHttpResponseHandler reqHandler;

    public static ListTweetCommentFragment instantiate(TweetDetailContract.Operator operator, TweetDetailContract.IAgencyView mAgencyView) {
        ListTweetCommentFragment fragment = new ListTweetCommentFragment();
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
        reqHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                CommentList data = XmlUtils.toBean(CommentList.class, responseBody);
                setListData(data.getList());
                onRequestSuccess(1);
                onRequestFinish();
                if (mAdapter.getCount() < 20 && mAgencyView != null)
                    mAgencyView.resetCmnCount(mAdapter.getCount());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mRefreshLayout.onComplete();
                mAdapter.setState(BaseRecyclerAdapter.STATE_LOAD_ERROR, true);
                Log.d("oschina", error.getMessage() == null ? "未知原因" : error.getMessage());
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
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING){
                    mOperator.onScroll();
                }
            }
        });
    }

    @Override
    protected BaseRecyclerAdapter<Comment> getRecyclerAdapter() {
        TweetCommentAdapter adapter = new TweetCommentAdapter(getContext());
        adapter.setOnItemClickListener(this);
        adapter.setOnItemLongClickListener(this);
        return adapter;
    }

    @Override
    protected Type getType() {
        return new TypeToken<CommentList>() {
        }.getType();
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
        if(mIsRefresh) pageNum = 0;
        ++pageNum;
    }

    private void requestData(int pageNum) {
        OSChinaApi.getCommentList(mOperator.getTweetDetail().getId(), 3, pageNum, reqHandler);
    }

    private void setListData(List<Comment> comments) {
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
    public void onCommentSuccess(Comment comment) {
        onRefreshing();
    }

    @Override
    public void onItemClick(int position, long itemId) {
        super.onItemClick(position, itemId);
        mOperator.toReply(mAdapter.getItem(position));
    }

    @Override
    public void onLongClick(int position, long itemId) {
        final Comment comment = mAdapter.getItem(position);
        if (comment == null) return;
        int itemsLen = comment.getAuthorId() == AppContext.getInstance().getLoginUid() ? 2 : 1;
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

    private void handleDeleteComment(Comment comment) {
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.showLoginActivity(getActivity());
            return;
        }
        mDeleteDialog = DialogHelp.getWaitDialog(getContext(), "正在删除...");
        OSChinaApi.deleteComment(mOperator.getTweetDetail().getId(), 3, comment.getId(), comment.getAuthorId(),
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Result res = XmlUtils.toBean(ResultBean.class, responseBody).getResult();
                        if (res.OK()) {
                            if (mDeleteDialog != null) {
                                mDeleteDialog.dismiss();
                                mDeleteDialog = null;
                            }
                            mAdapter.removeItem(mDeleteIndex);
                            int count = Integer.valueOf(mOperator.getTweetDetail().getCommentCount()) -1;
                            mOperator.getTweetDetail().setCommentCount(String.valueOf(count)); // Bean就这样写的,我也不知道为什么!!!!
                            mAgencyView.resetCmnCount(count);
                        } else {
                            if (mDeleteDialog != null) {
                                mDeleteDialog.dismiss();
                                mDeleteDialog = null;
                            }
                            Toast.makeText(getContext(), "删除失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        if (mDeleteDialog != null) {
                            mDeleteDialog.dismiss();
                            mDeleteDialog = null;
                        }
                        Toast.makeText(getContext(), "删除失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
