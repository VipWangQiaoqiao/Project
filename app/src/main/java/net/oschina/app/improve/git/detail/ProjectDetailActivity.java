package net.oschina.app.improve.git.detail;

import android.content.Context;
import android.content.Intent;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseBackActivity;
import net.oschina.app.improve.git.bean.Project;

/**
 * Created by haibin
 * on 2017/3/9.
 */

public class ProjectDetailActivity extends BaseBackActivity {

    public static void show(Context context, Project project) {
        Intent intent = new Intent(context, ProjectDetailActivity.class);
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
        ProjectDetailFragment fragment = ProjectDetailFragment.newInstance((Project) getIntent()
                .getExtras()
                .getSerializable("project"));
        new ProjectDetailPresenter(fragment);
        addFragment(R.id.fl_content, fragment);
    }
}
