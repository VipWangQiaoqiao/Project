package net.oschina.app.improve.user.activities;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseActivity;
import net.oschina.app.improve.bean.SubTab;
import net.oschina.app.improve.user.fragments.UserEventFragment;
import net.oschina.app.util.UIHelper;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by fei
 * on 2016/12/2.
 * desc:
 */

public class UserEventActivity extends BaseActivity implements View.OnClickListener {


    private static final String TAG = "UserEventActivity";
    @Bind(R.id.tv_navigation_label)
    TextView mTvBackLabel;
    @Bind(R.id.ib_event_scan)
    ImageButton mIbEventScan;
    @Bind(R.id.lay_container)
    FrameLayout mContainer;

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

        SubTab tab = new SubTab();

        tab.setName("我的活动");
        tab.setFixed(false);
        tab.setHref("https://www.oschina.net/action/apiv2/sub_list?token=727d77c15b2ca641fff392b779658512");
        tab.setNeedLogin(false);
        tab.setSubtype(1);
        tab.setOrder(74);
        tab.setToken("727d77c15b2ca641fff392b779658512");
        tab.setType(5);

        UserEventFragment userEventFragment = UserEventFragment.newInstance(this, tab);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.lay_container, userEventFragment)
                .commitNowAllowingStateLoss();
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
                finish();
                break;
            default:
                break;
        }

    }


}
