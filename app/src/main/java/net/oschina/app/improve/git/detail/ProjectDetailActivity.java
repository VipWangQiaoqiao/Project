package net.oschina.app.improve.git.detail;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseBackActivity;
import net.oschina.app.improve.git.bean.Project;
import net.oschina.app.ui.empty.EmptyLayout;

import butterknife.Bind;

/**
 * Created by haibin
 * on 2017/3/9.
 */

public class ProjectDetailActivity extends BaseBackActivity implements ProjectDetailContract.EmptyView {

    @Bind(R.id.emptyLayout)
    EmptyLayout mEmptyLayout;

    public static void show(Context context, Project project) {
        Intent intent = new Intent(context, ProjectDetailActivity.class);
        intent.putExtra("project", project);
        context.startActivity(intent);
    }

    public static void show(Context context, String pathWithNamespace, String name) {
        Intent intent = new Intent(context, ProjectDetailActivity.class);
        Project project = new Project();
        project.setName(name);
        project.setPathWithNamespace(pathWithNamespace);
        intent.putExtra("project", project);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_project_detail;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        final Project project = (Project) getIntent()
                .getExtras()
                .getSerializable("project");
        ProjectDetailFragment fragment = ProjectDetailFragment.newInstance(project);
        final ProjectDetailContract.Presenter presenter = new ProjectDetailPresenter(fragment, this,project);
        mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmptyLayout.getErrorState() != EmptyLayout.NETWORK_LOADING) {
                    mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                    assert project != null;
                    if (project.getId() == 0) {
                        presenter.getProjectDetail(project.getName(), project.getPathWithNamespace());
                    } else {
                        presenter.getProjectDetail(project.getId());
                    }
                }
            }
        });

        addFragment(R.id.fl_content, fragment);
    }

    @Override
    public void showGetDetailSuccess(int strId) {
        mEmptyLayout.setErrorType(strId);
    }

    @Override
    public void showGetDetailFailure(int strId) {
        mEmptyLayout.setErrorType(strId);
    }
}
