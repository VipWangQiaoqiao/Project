package net.oschina.app.improve.git.gist;

import android.content.Context;
import android.content.Intent;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseBackActivity;

/**
 * 代码片段
 * Created by haibin on 2017/5/10.
 */

public class GistActivity extends BaseBackActivity{
    private GistPresenter mPresenter;

    public static void show(Context context){
        context.startActivity(new Intent(context,GistActivity.class));
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_gist;
    }
}
