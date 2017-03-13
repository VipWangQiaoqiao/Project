package net.oschina.app.improve.git.tree;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseBackActivity;
import net.oschina.app.improve.git.bean.Branch;
import net.oschina.app.improve.git.bean.Project;
import net.oschina.app.improve.git.branch.BranchPopupWindow;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by haibin
 * on 2017/3/13.
 */

public class TreeActivity extends BaseBackActivity implements View.OnClickListener, BranchPopupWindow.Callback {
    private BranchPopupWindow mBranchPopupWindow;

    @Bind(R.id.ll_branch)
    LinearLayout mLinearBranch;
    @Bind(R.id.tv_branch)
    TextView mTextBranch;
    @Bind(R.id.iv_arrow)
    ImageView mImageArrow;

    private TreeContract.Presenter mPresenter;

    public static void show(Context context, Project project) {
        Intent intent = new Intent(context, TreeActivity.class);
        intent.putExtra("project", project);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_tree;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        TreeFragment fragment = TreeFragment.newInstance();
        addFragment(R.id.fl_content, fragment);
        Project project = (Project) getIntent().getSerializableExtra("project");
        mTextBranch.setText(project.getDefaultBranch());
        mBranchPopupWindow = new BranchPopupWindow(this, project.getId(), this);
        mPresenter = new TreePresenter(fragment, project);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @OnClick({R.id.ll_branch})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_branch:
                mBranchPopupWindow.showAsDropDown(mLinearBranch);
                break;
        }
    }

    @Override
    public void onSelect(Branch branch) {
        mTextBranch.setText(branch.getName());
        mPresenter.setBranch(branch.getName());
        mPresenter.onRefreshing();
    }

    @Override
    public void onDismiss() {
        mImageArrow.setImageResource(R.mipmap.ic_arrow_bottom);
    }

    @Override
    public void onShow() {
        mImageArrow.setImageResource(R.mipmap.ic_arrow_top);
    }

    @Override
    public void onBackPressed() {
        TreePresenter presenter = (TreePresenter) mPresenter;
        if (presenter.isCanBack()) {
            super.onBackPressed();
        } else {
            presenter.preLoad();
        }
    }
}
