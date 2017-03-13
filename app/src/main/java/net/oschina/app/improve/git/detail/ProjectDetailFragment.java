package net.oschina.app.improve.git.detail;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.fragments.BaseFragment;
import net.oschina.app.improve.git.bean.Project;
import net.oschina.app.improve.git.bean.User;
import net.oschina.app.improve.git.tree.TreeActivity;
import net.oschina.app.improve.widget.OWebView;
import net.oschina.app.util.StringUtils;

import java.text.SimpleDateFormat;

import butterknife.Bind;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

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
        mPresenter.getProjectDetail(mProject.getId());
    }

    @OnClick({R.id.ll_code})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_code:
                TreeActivity.show(mContext, mProject);
                break;
        }
    }

    @Override
    public void showNetworkError(int strId) {

    }

    @Override
    public void showGetDetailSuccess(Project project, int strId) {
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

    private void initProject(Project project) {
        User user = project.getOwner();
        if (user != null) {
            mTextName.setText(user.getName() + "/" + project.getName());
        }
        mTextLanguage.setText(project.getLanguage());
        mTextStarCount.setText(String.valueOf(project.getStarsCount()));
        mTextWatchCount.setText(String.valueOf(project.getWatchesCount()));
        mTextForkCount.setText(String.valueOf(project.getForksCount()));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mTextUpdateDate.setText("上次更新于" + StringUtils.formatSomeAgo(dateFormat.format(project.getLastPushTime())));
        mTextIssuesCount.setText(String.valueOf(project.getIssueCount()));
        mTexPrCount.setText(String.valueOf(project.getPullRequestCount()));
        mTextDescription.setText(project.getDescription());
        mTextLanguage.setVisibility(TextUtils.isEmpty(project.getLanguage()) ? View.GONE : View.VISIBLE);
    }
}
