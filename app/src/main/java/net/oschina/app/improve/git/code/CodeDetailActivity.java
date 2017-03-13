package net.oschina.app.improve.git.code;

import android.content.Context;
import android.content.Intent;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseBackActivity;
import net.oschina.app.improve.git.bean.Project;

/**
 * Created by haibin
 * on 2017/3/13.
 */

public class CodeDetailActivity extends BaseBackActivity {
    public static void show(Context context, Project project, String path, String branch) {
        Intent intent = new Intent(context, CodeDetailActivity.class);
        intent.putExtra("path", path);
        intent.putExtra("branch", branch);
        intent.putExtra("project", project);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_code_detail;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        Intent intent = getIntent();
        CodeDetailFragment fragment = CodeDetailFragment.newInstance(intent.getStringExtra("path"));
        addFragment(R.id.fl_content, fragment);
        new CodeDetailPresenter(fragment, (Project) intent.getSerializableExtra("project"),
                intent.getStringExtra("path"),
                intent.getStringExtra("branch"));
    }
}
