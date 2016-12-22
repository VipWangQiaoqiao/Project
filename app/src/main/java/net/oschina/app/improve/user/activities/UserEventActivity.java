package net.oschina.app.improve.user.activities;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseActivity;
import net.oschina.app.improve.user.fragments.UserEventFragment;
import net.oschina.app.util.UIHelper;

import butterknife.OnClick;

/**
 * Created by fei
 * on 2016/12/2.
 * desc:
 */

public class UserEventActivity extends BaseActivity implements View.OnClickListener {

    /**
     * show the  activity
     *
     * @param context context
     */
    public static void show(Context context) {
        Intent intent = new Intent(context, UserEventActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main_user_event;
    }

    @Override
    protected void initData() {
        super.initData();

        UserEventFragment userEventFragment = UserEventFragment.newInstance();

        getSupportFragmentManager().beginTransaction().replace(R.id.lay_container, userEventFragment,
                UserEventFragment.class.getName()).commitNowAllowingStateLoss();
    }

    @OnClick({R.id.tv_navigation_label, R.id.ib_event_scan})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_navigation_label:
                finish();
                break;
            case R.id.ib_event_scan:
                UIHelper.showScanActivity(this);
                //finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
