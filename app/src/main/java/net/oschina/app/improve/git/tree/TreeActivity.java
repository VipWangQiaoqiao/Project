package net.oschina.app.improve.git.tree;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseBackActivity;
import net.oschina.app.improve.git.bean.Project;

import butterknife.OnClick;

/**
 * Created by haibin
 * on 2017/3/13.
 */

public class TreeActivity extends BaseBackActivity implements View.OnClickListener {
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
        addFragment(R.id.fl_content,fragment);
        mPresenter = new TreePresenter(fragment, (Project) getIntent().getSerializableExtra("project"));
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
                mPresenter.getBranch();
                break;
        }
    }
}
