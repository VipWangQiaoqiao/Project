package net.oschina.app.improve.git.gist;

import android.content.Context;
import android.content.Intent;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseBackActivity;

/**
 * 代码片段
 * Created by haibin on 2017/5/10.
 */

public class GistActivity extends BaseBackActivity {
    public static void show(Context context) {
        context.startActivity(new Intent(context, GistActivity.class));
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_gist;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        GistFragment fragment = GistFragment.newInstance();
        new GistPresenter(fragment);
        addFragment(R.id.fl_content, fragment);
    }
}
