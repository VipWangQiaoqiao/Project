package net.oschina.app.team.fragment;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.adapter.CommentAdapter;
import net.oschina.app.adapter.CommentAdapter.OnOperationListener;
import net.oschina.app.api.OperationResponseHandler;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BeseHaveHeaderListFragment;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.bean.Comment;
import net.oschina.app.bean.CommentList;
import net.oschina.app.bean.ListEntity;
import net.oschina.app.bean.Result;
import net.oschina.app.bean.ResultBean;
import net.oschina.app.emoji.EmojiFragment.EmojiTextListener;
import net.oschina.app.interf.EmojiFragmentControl;
import net.oschina.app.team.bean.TeamActive;
import net.oschina.app.team.bean.TeamActiveDetail;
import net.oschina.app.ui.dialog.CommonDialog;
import net.oschina.app.ui.dialog.DialogHelper;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.HTMLUtil;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;
import net.oschina.app.util.XmlUtils;
import net.oschina.app.widget.AvatarView;

import org.apache.http.Header;

import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * Team动态的详情界面
 * 
 * @author kymjs (https://github.com/kymjs)
 * 
 */
public class TeamTweetDetailFragment extends
        BeseHaveHeaderListFragment<Comment, TeamActiveDetail> implements
        EmojiTextListener, EmojiFragmentControl, OnOperationListener,
        OnItemClickListener, OnItemLongClickListener {

    private static final String CACHE_KEY_PREFIX = "team_tweet_";

    private AvatarView img_head;
    private TextView tv_name;
    private TextView tv_active;
    private TextView mTvCommentCount;
    private TextView tv_content;
    private LinearLayout ll_event_list;
    private TextView tv_client;
    private TextView tv_date;

    private TeamActive active;
    private int teamId;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // 使用simpleBackActivity传递的时候使用
        // Bundle bundle = getActivity().getIntent().getBundleExtra(
        // SimpleBackActivity.BUNDLE_KEY_ARGS);
        Bundle bundle = getActivity().getIntent().getExtras();
        active = (TeamActive) bundle
                .getSerializable(DynamicFragment.DYNAMIC_FRAGMENT_KEY);
        teamId = bundle.getInt(DynamicFragment.DYNAMIC_FRAGMENT_TEAM_KEY, 0);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected View initHeaderView() {
        View headView = View.inflate(getActivity(),
                R.layout.frag_dynamic_detail, null);

        img_head = (AvatarView) headView
                .findViewById(R.id.event_listitem_userface);
        tv_name = (TextView) headView
                .findViewById(R.id.event_listitem_username);
        tv_active = (TextView) headView
                .findViewById(R.id.event_listitem_active);
        mTvCommentCount = (TextView) headView
                .findViewById(R.id.tv_comment_count);
        tv_content = (TextView) headView
                .findViewById(R.id.event_listitem_content);
        ll_event_list = (LinearLayout) headView
                .findViewById(R.id.event_listitem_commits_list);
        tv_client = (TextView) headView
                .findViewById(R.id.event_listitem_client);
        tv_date = (TextView) headView.findViewById(R.id.event_listitem_date);

        img_head.setAvatarUrl(active.getAuthor().getPortrait());
        tv_name.setText(active.getAuthor().getName());
        ll_event_list.setVisibility(View.GONE);
        // tv_active.setText(data.getBody().getDetail());
        tv_content.setText(Html.fromHtml(active.getBody().getDetail()));
        tv_date.setText(active.getCreateTime());
        // tv_client.setText("");
        return headView;
    }

    /**
     * 点击发送按钮时
     */
    @Override
    public void onSendClick(String text) {
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.tip_network_error);
            return;
        }
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.showLoginActivity(getActivity());
            mEmojiFragment.hideKeyboard();
            return;
        }
        if (TextUtils.isEmpty(text)) {
            AppContext.showToastShort(R.string.tip_comment_content_empty);
            mEmojiFragment.requestFocusInput();
            return;
        }
        handleComment(text);
    }

    /**
     * 处理回复的提交
     * 
     * @param text
     */
    private void handleComment(String text) {
        showWaitDialog(R.string.progress_submit);
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.showLoginActivity(getActivity());
            return;
        }
        if (mEmojiFragment.getInputTag() != null) {
            Comment comment = (Comment) mEmojiFragment.getInputTag();
            OSChinaApi
                    .replyComment(active.getId(), CommentList.CATALOG_TWEET,
                            comment.getId(), comment.getAuthorId(), AppContext
                                    .getInstance().getLoginUid(), text,
                            mCommentHandler);
        } else {
            OSChinaApi.publicComment(CommentList.CATALOG_TWEET, active.getId(),
                    AppContext.getInstance().getLoginUid(), text, 0,
                    mCommentHandler);
        }
    }

    /*********************************************************/

    @Override
    protected void requestDetailData(boolean isRefresh) {
        OSChinaApi.getDynamicDetail(active.getId(), teamId, AppContext
                .getInstance().getLoginUid(), mDetailHandler);
    }

    @Override
    protected String getDetailCacheKey() {
        return CACHE_KEY_PREFIX + active.getId();
    }

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + active.getId() + mCurrentPage;
    }

    @Override
    protected CommentList readList(Serializable seri) {
        super.readList(seri);
        return (CommentList) seri;
    }

    @Override
    protected void executeOnLoadDetailSuccess(TeamActiveDetail detailBean) {
        mListView.setHeaderDividersEnabled(false);
        mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        this.active = detailBean.getTeamActive();
        tv_content.setText(Html.fromHtml(this.active.getBody().getTitle()));
        mAdapter.setNoDataText(R.string.comment_empty);
    }

    @Override
    protected ListEntity<Comment> parseList(InputStream is) throws Exception {
        super.parseList(is);
        CommentList list = XmlUtils.toBean(CommentList.class, is);
        return list;
    }

    @Override
    protected TeamActiveDetail getDetailBean(ByteArrayInputStream is) {
        return XmlUtils.toBean(TeamActiveDetail.class, is);
    }

    @Override
    protected ListBaseAdapter<Comment> getListAdapter() {
        return new CommentAdapter(this, true);
    }

    @Override
    protected void sendRequestData() {
        OSChinaApi.getCommentList(active.getId(), CommentList.CATALOG_TWEET,
                mCurrentPage, mHandler);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
            int position, long id) {
        if (position - 1 == -1) {
            return false;
        }
        final Comment item = (Comment) mAdapter.getItem(position - 1);
        if (item == null)
            return false;
        int itemsLen = item.getAuthorId() == AppContext.getInstance()
                .getLoginUid() ? 2 : 1;
        String[] items = new String[itemsLen];
        items[0] = getResources().getString(R.string.copy);
        if (itemsLen == 2) {
            items[1] = getResources().getString(R.string.delete);
        }
        final CommonDialog dialog = DialogHelper
                .getPinterestDialogCancelable(getActivity());
        dialog.setNegativeButton(R.string.cancle, null);
        dialog.setItemsWithoutChk(items, new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                dialog.dismiss();
                if (position == 0) {
                    TDevice.copyTextToBoard(HTMLUtil.delHTMLTag(item
                            .getContent()));
                } else if (position == 1) {
                    handleDeleteComment(item);
                }
            }
        });
        dialog.show();
        return true;
    }

    private void handleDeleteComment(Comment comment) {
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.showLoginActivity(getActivity());
            return;
        }
        AppContext.showToastShort(R.string.deleting);
        OSChinaApi.deleteComment(active.getId(), CommentList.CATALOG_TWEET,
                comment.getId(), comment.getAuthorId(),
                new DeleteOperationResponseHandler(comment));
    }

    class DeleteOperationResponseHandler extends OperationResponseHandler {

        DeleteOperationResponseHandler(Object... args) {
            super(args);
        }

        @Override
        public void onSuccess(int code, ByteArrayInputStream is, Object[] args) {
            try {
                Result res = XmlUtils.toBean(ResultBean.class, is).getResult();
                if (res.OK()) {
                    AppContext.showToastShort(R.string.delete_success);
                    mAdapter.removeItem(args[0]);
                    // setTweetCommentCount();
                } else {
                    AppContext.showToastShort(res.getErrorMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                onFailure(code, e.getMessage(), args);
            }
        }

        @Override
        public void onFailure(int code, String errorMessage, Object[] args) {
            AppContext.showToastShort(R.string.delete_faile);
        }
    }

    private final AsyncHttpResponseHandler mCommentHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
            try {
                ResultBean rsb = XmlUtils.toBean(ResultBean.class,
                        new ByteArrayInputStream(arg2));
                Result res = rsb.getResult();
                if (res.OK()) {
                    hideWaitDialog();
                    AppContext.showToastShort(R.string.comment_publish_success);
                    mAdapter.setState(ListBaseAdapter.STATE_NO_MORE);
                    mAdapter.addItem(0, rsb.getComment());
                    mEmojiFragment.reset();
                    // setTweetCommentCount();
                } else {
                    hideWaitDialog();
                    AppContext.showToastShort(res.getErrorMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                onFailure(arg0, arg1, arg2, e);
            }
        }

        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                Throwable arg3) {
            hideWaitDialog();
            AppContext.showToastShort(R.string.comment_publish_faile);
        }
    };

    @Override
    protected void executeOnLoadDataSuccess(java.util.List<Comment> data) {
        super.executeOnLoadDataSuccess(data);
        if (mTvCommentCount != null && data != null) {
            mTvCommentCount.setText("评论(" + (mAdapter.getCount() - 1) + ")");
        }
    };

    @Override
    public void onMoreClick(Comment comment) {}
}
