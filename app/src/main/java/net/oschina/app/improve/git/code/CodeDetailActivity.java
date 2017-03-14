package net.oschina.app.improve.git.code;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseBackActivity;
import net.oschina.app.improve.dialog.ShareDialog;
import net.oschina.app.improve.git.bean.Project;
import net.oschina.app.util.HTMLUtil;
import net.oschina.app.util.StringUtils;

/**
 * Created by haibin
 * on 2017/3/13.
 */

public class CodeDetailActivity extends BaseBackActivity {
    private ShareDialog mAlertDialog;
    private Project mProject;

    public static void show(Context context, Project project, String path, String branch) {
        Intent intent = new Intent(context, CodeDetailActivity.class);
        Log.e("path", path);
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
        mProject = (Project) intent.getSerializableExtra("project");
        new CodeDetailPresenter(fragment, mProject,
                intent.getStringExtra("path"),
                intent.getStringExtra("branch"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_code_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_share:
                //toShare(mBean.getTitle(), mBean.getBody(), mBean.getHref());
                break;
        }
        return true;
    }

    private boolean toShare(String title, String content, String url) {

        content = content.trim();
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
                    ShareDialog(this, mProject.getId())
                    .type(mProject.getParentId())
                    .title(title)
                    .content(content)
                    .url(url).with();
        }
        mAlertDialog.show();

        return true;
    }
}
