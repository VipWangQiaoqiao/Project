package net.oschina.app.improve.user.activities;

import android.content.Context;
import android.content.Intent;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseBackActivity;

/**
 * Created by fei
 * on 2016/12/22.
 * desc:
 */

public class UserSelectFriendsActivity extends BaseBackActivity {

    public static void show(Context context) {
        Intent intent = new Intent(context, UserSelectFriendsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main_user_select_friends;
    }


}
