package net.oschina.app.improve.tweet.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.base.fragments.BaseRecyclerViewFragment;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.simple.TweetComment;
import net.oschina.app.improve.tweet.adapter.TweetCommentAdapter;
import net.oschina.app.improve.tweet.contract.TweetDetailContract;
import net.oschina.app.util.DialogHelp;
import net.oschina.app.util.HTMLUtil;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

/**
 * Created by thanatos
 * on 16/6/13.
 */
public class ListTweetCommentFragment extends BaseRecyclerViewFragment<TweetComment>
        implements TweetDetailContract.ICmnView, BaseRecyclerAdapter.OnItemLongClickListener {

    private TweetDetailContract.Operator mOperator;
    private TweetDetailContract.IAgencyView mAgencyView;
    private int mDeleteIndex = 0;
    private Dialog mDeleteDialog;

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
        TweetCommentAdapter adapter = new TweetCommentAdapter(getContext());
        adapter.setOnItemClickListener(this);
        adapter.setOnItemLongClickListener(this);
        return adapter;
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<TweetComment>>>() {
        }.getType();
    }

    @Override
    protected void onRequestSuccess(int code) {
        super.onRequestSuccess(code);
        if (mAdapter.getCount() < 20 && mAgencyView != null)
            mAgencyView.resetCmnCount(mAdapter.getCount());
    }

    @Override
    protected void onRequestFinish() {
        super.onRequestFinish();
    }

    @Override
    public void onLoadMore() {
        OSChinaApi.getTweetCommentList(
                mOperator.getTweetDetail().getId(),
                mIsRefresh ? mBean.getPrevPageToken() : mBean.getNextPageToken(),
                mHandler
        );
    }

    @Override
    public void requestData() {
        OSChinaApi.getTweetCommentList(mOperator.getTweetDetail().getId(), null, mHandler);
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
        super.onItemClick(position, itemId);
        TweetComment item = mAdapter.getItem(position);
        if (item != null)
            mOperator.toReply(item);
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
                        responseString, new TypeToken<ResultBean>() {
                        }.getType());
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
    public void onCommentSuccess(TweetComment comment) {
        onRefreshing();
    }

}
