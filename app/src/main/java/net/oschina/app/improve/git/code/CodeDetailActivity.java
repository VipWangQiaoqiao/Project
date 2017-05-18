package net.oschina.app.improve.git.code;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.ActionBar;
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
    private CodeDetailPresenter mPresenter;

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
        mPresenter = new CodeDetailPresenter(fragment, mProject,
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
                toShare();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }
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
                    ShareDialog(this)
                    .title(mProject.getOwner().getName() + "/" + mProject.getName())
                    .content(content)
                    .url(mPresenter.getShareUrl())
                    .bitmapResID(R.mipmap.ic_git)
                    .with();
        }
        mAlertDialog.show();

        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ActionBar bar = getSupportActionBar();
            if(bar != null)
                bar.hide();
            mPresenter.changeConfig(true);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mPresenter.changeConfig(false);
            ActionBar bar = getSupportActionBar();
            if(bar != null)
                bar.show();
        }
    }
}
