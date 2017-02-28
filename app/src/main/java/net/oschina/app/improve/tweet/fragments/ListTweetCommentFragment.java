package net.oschina.app.improve.tweet.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.base.fragments.BaseRecyclerViewFragment;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.simple.TweetComment;
import net.oschina.app.improve.tweet.adapter.TweetCommentAdapter;
import net.oschina.app.improve.tweet.contract.TweetDetailContract;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.improve.utils.QuickOptionDialogHelper;
import net.oschina.app.util.HTMLUtil;
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

    public static ListTweetCommentFragment instantiate(TweetDetailContract.Operator operator, TweetDetailContract.IAgencyView mAgencyView) {
        ListTweetCommentFragment fragment = new ListTweetCommentFragment();
        fragment.mOperator = operator;
        fragment.mAgencyView = mAgencyView;
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mOperator = (TweetDetailContract.Operator) context;
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
    public void requestData() {
        String token = isRefreshing ? null : mBean.getNextPageToken();
        OSChinaApi.getTweetCommentList(mOperator.getTweetDetail().getId(), token, mHandler);
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
        mDeleteIndex = position;

        QuickOptionDialogHelper.with(getContext())
                .addCopy(HTMLUtil.delHTMLTag(comment.getContent()))
                .addOther(comment.getAuthor().getId() == AccountHelper.getUserId(),
                        R.string.delete, new Runnable() {
                            @Override
                            public void run() {
                                handleDeleteComment(comment);
                            }
                        }).show();

    }

    private void handleDeleteComment(TweetComment comment) {
        if (!AccountHelper.isLogin()) {
            UIHelper.showLoginActivity(getActivity());
            return;
        }
        OSChinaApi.deleteTweetComment(mOperator.getTweetDetail().getId(), comment.getId(), new TextHttpResponseHandler() {
            private ProgressDialog dialog = DialogHelper.getProgressDialog(getContext(), "正在删除……", false);

            @Override
            public void onStart() {
                super.onStart();
                dialog.show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                AppContext.showToastShort("删除失败");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                ResultBean result = AppOperator.createGson().fromJson(
                        responseString, new TypeToken<ResultBean>() {
                        }.getType());
                if (result.isSuccess()) {
                    mAdapter.removeItem(mDeleteIndex);
                    int count = mOperator.getTweetDetail().getCommentCount() - 1;
                    mOperator.getTweetDetail().setCommentCount(count); // Bean就这样写的,我也不知道为什么!!!!
                    mAgencyView.resetCmnCount(count);
                    AppContext.showToastShort("删除成功");
                } else {
                    AppContext.showToastShort("删除失败");
                }
            }
        });
    }

    @Override
    public void onCommentSuccess(TweetComment comment) {
        onRefreshing();
    }

}
