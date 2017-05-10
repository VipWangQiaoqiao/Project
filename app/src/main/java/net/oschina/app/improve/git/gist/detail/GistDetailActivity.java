package net.oschina.app.improve.git.gist.detail;

import android.content.Context;
import android.content.Intent;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseBackActivity;
import net.oschina.app.improve.git.bean.Gist;

/**
 * 代码片段详情
 * Created by haibin on 2017/5/10.
 */

public class GistDetailActivity extends BaseBackActivity {
    public static void show(Context context, Gist gist) {
        Intent intent = new Intent(context, GistDetailActivity.class);
        intent.putExtra("gist", gist);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_gist;
    }
}
