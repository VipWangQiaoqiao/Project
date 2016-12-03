package net.oschina.app.improve.detail.v2;

import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseBackActivity;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.behavior.CommentBar;
import net.oschina.app.improve.dialog.ShareDialogBuilder;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.HTMLUtil;
import net.oschina.app.util.StringUtils;

/**
 * 新版本详情页实现
 * Created by haibin
 * on 2016/11/30.
 */

public abstract class DetailActivity extends BaseBackActivity implements DetailContract.EmptyView, Runnable {
    private DetailPresenter mPresenter;
    protected EmptyLayout mEmptyLayout;
    protected DetailFragment mDetailFragment;
    private ShareDialogBuilder mShareDialogBuilder;
    protected AlertDialog mAlertDialog;
    protected TextView mCommentCountView;

    protected CommentBar mDelegation;
    private LinearLayout mLayComment;

    @Override
    protected int getContentView() {
        return R.layout.activity_detail_v2;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mLayComment = (LinearLayout) findViewById(R.id.ll_comment);
        mEmptyLayout = (EmptyLayout) findViewById(R.id.lay_error);
        mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmptyLayout.getErrorState() != EmptyLayout.NETWORK_LOADING) {
                    mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                    mPresenter.getDetail();
                }
            }
        });
        mDelegation = CommentBar.delegation(this, mLayComment);
        mDetailFragment = getDetailFragment();
        addFragment(R.id.lay_container, mDetailFragment);
        mPresenter = new DetailPresenter(mDetailFragment, this, (SubBean) getIntent().getSerializableExtra("sub_bean"));
        mPresenter.getDetail();
    }

    @Override
    public void hideEmptyLayout() {
        mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
    }

    @Override
    public void showErrorLayout(int errorType) {
        mEmptyLayout.setErrorType(errorType);
    }

    @Override
    public void run() {
        hideEmptyLayout();
    }

    int getOptionsMenuId() {
        return R.menu.menu_detail;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int menuId = getOptionsMenuId();
        if (menuId <= 0)
            return false;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(menuId, menu);
        MenuItem item = menu.findItem(R.id.menu_scroll_comment);
        if (item != null) {
            View action = item.getActionView();
            if (action != null) {
                action.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mDetailFragment != null) {
                            mDetailFragment.scrollToBottom();
                        }
                    }
                });
                View tv = action.findViewById(R.id.tv_comment_count);
                if (tv != null)
                    mCommentCountView = (TextView) tv;
            }
        }
        return true;
    }


    protected boolean toShare(String title, String content, String url) {
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content) || TextUtils.isEmpty(url))
            return false;

        content = content.trim();
        if (content.length() > 55) {
            content = HTMLUtil.delHTMLTag(content);
            if (content.length() > 55)
                content = StringUtils.getSubString(0, 55, content);
        } else {
            content = HTMLUtil.delHTMLTag(content);
        }
        if (TextUtils.isEmpty(content))
            return false;

        // 分享
        if (mShareDialogBuilder == null) {
            mShareDialogBuilder = ShareDialogBuilder.with(this)
                    .title(title)
                    .content(content)
                    .url(url)
                    .build();
        }
        if (mAlertDialog == null)
            mAlertDialog = mShareDialogBuilder.create();
        mAlertDialog.show();
        return true;
    }

    protected abstract DetailFragment getDetailFragment();
}
