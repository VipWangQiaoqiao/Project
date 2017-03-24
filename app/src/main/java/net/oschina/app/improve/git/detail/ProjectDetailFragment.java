package net.oschina.app.improve.git.detail;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.fragments.BaseFragment;
import net.oschina.app.improve.dialog.ShareDialog;
import net.oschina.app.improve.git.bean.Project;
import net.oschina.app.improve.git.bean.User;
import net.oschina.app.improve.git.comment.CommentActivity;
import net.oschina.app.improve.git.tree.TreeActivity;
import net.oschina.app.improve.widget.OWebView;
import net.oschina.app.util.HTMLUtil;
import net.oschina.app.util.StringUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by haibin
 * on 2017/3/10.
 */
@SuppressWarnings("unused")
public class ProjectDetailFragment extends BaseFragment implements ProjectDetailContract.View,
        View.OnClickListener {
    @Bind(R.id.webView)
    OWebView mWebView;
    @Bind(R.id.tv_name)
    TextView mTextName;
    @Bind(R.id.tv_language)
    TextView mTextLanguage;
    @Bind(R.id.tv_update_date)
    TextView mTextUpdateDate;
    @Bind(R.id.tv_start_count)
    TextView mTextStarCount;
    @Bind(R.id.tv_watches_count)
    TextView mTextWatchCount;
    @Bind(R.id.tv_fork_count)
    TextView mTextForkCount;
    @Bind(R.id.tv_description)
    TextView mTextDescription;
    @Bind(R.id.tv_issues_count)
    TextView mTextIssuesCount;
    @Bind(R.id.tv_pr_count)
    TextView mTexPrCount;
    @Bind(R.id.tv_comment_count)
    TextView mTextCommentCount;

    private ProjectDetailContract.Presenter mPresenter;
    private Project mProject;
    private ShareDialog mAlertDialog;

    public static ProjectDetailFragment newInstance(Project project) {
        ProjectDetailFragment fragment = new ProjectDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("project", project);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_project_detail;
    }

    @Override
    protected void initBundle(Bundle bundle) {
        super.initBundle(bundle);
        mProject = (Project) bundle.getSerializable("project");
    }

    @Override
    protected void initData() {
        super.initData();
        initProject(mProject);
        if (mProject.getId() == 0) {
            mPresenter.getProjectDetail(mProject.getName(), mProject.getPathWithNamespace());
        } else {
            mPresenter.getProjectDetail(mProject.getId());
        }
    }

    @OnClick({R.id.ll_code, R.id.ll_share, R.id.ll_comment})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_code:
                TreeActivity.show(mContext, mProject);
                break;
            case R.id.ll_share:
                toShare();
                break;
            case R.id.ll_comment:
                CommentActivity.show(mContext, mProject);
                break;
        }
    }

    @Override
    public void showNetworkError(int strId) {

    }

    @Override
    public void showGetCommentCountSuccess(int count) {
        mTextCommentCount.setText(String.format("评论（%s）", count));
    }

    @Override
    public void showGetDetailSuccess(Project project, int strId) {
        mPresenter.getCommentCount(project.getId());
        mProject = project;
        initProject(project);
        mWebView.loadDetailDataAsync(project.getReadme(), new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    @Override
    public void showGetDetailFailure(int strId) {

    }

    @Override
    public void setPresenter(ProjectDetailContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    private void initProject(Project project) {
        if (project.getId() == 0 || project.getOwner() == null) return;
        User user = project.getOwner();
        if (user != null) {
            mTextName.setText(user.getName() + "/" + project.getName());
        }
        mTextLanguage.setText(project.getLanguage());
        mTextStarCount.setText(getCount(project.getStarsCount()));
        mTextWatchCount.setText(getCount(project.getWatchesCount()));
        mTextForkCount.setText(getCount(project.getForksCount()));
        mTextIssuesCount.setText(String.valueOf(project.getIssueCount()));
        mTexPrCount.setText(String.valueOf(project.getPullRequestCount()));
        mTextDescription.setText(project.getDescription());
        mTextLanguage.setVisibility(TextUtils.isEmpty(project.getLanguage()) ? View.GONE : View.VISIBLE);
        if(project.getLastPushTime() != null){
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            mTextUpdateDate.setText("上次更新于" + StringUtils.formatSomeAgo(dateFormat.format(project.getLastPushTime())));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }
    }

    private String getCount(int count) {
        DecimalFormat decimalFormat=new DecimalFormat(".0");
        return count >= 1000 ? String.format("%sk", decimalFormat.format((float)count / 1000)) : String.valueOf(count);
    }

    private boolean toShare() {
        String content = mProject.getDescription().trim();
        if (content.length() > 55) {
            content = HTMLUtil.delHTMLTag(content);
            if (content.length() > 55)
                content = StringUtils.getSubString(0, 55, content);
        } else {
            content = HTMLUtil.delHTMLTag(content);
        }
        if (TextUtils.isEmpty(content))
            content = "";

        // 分享
        if (mAlertDialog == null) {
            mAlertDialog = new
                    ShareDialog(getActivity())
                    .title(mProject.getOwner().getName() + "/" + mProject.getName())
                    .content(content)
                    .url(mPresenter.getShareUrl())
                    .bitmapResID(R.mipmap.ic_git)
                    .with();
        }
        mAlertDialog.show();

        return true;
    }
}
