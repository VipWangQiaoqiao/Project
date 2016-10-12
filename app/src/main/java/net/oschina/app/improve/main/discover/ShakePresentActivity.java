package net.oschina.app.improve.main.discover;

import android.content.Context;
import android.content.Intent;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseBackActivity;

/**
 * 摇一摇抽奖
 * Created by haibin
 * on 2016/10/10.
 */

public class ShakePresentActivity extends BaseBackActivity {

    public static void show(Context context) {
        context.startActivity(new Intent(context, ShakePresentActivity.class));
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_shake_present;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        addFragment(R.id.fl_content, ShakeNewsFragment.newInstance());
    }
}
