package net.oschina.app.improve.user.activities;

import android.widget.FrameLayout;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseBackActivity;
import net.oschina.app.improve.bean.SubTab;
import net.oschina.app.improve.user.fragments.UserEventFragment;

import butterknife.Bind;

/**
 * Created by fei
 * on 2016/12/2.
 * desc:
 */

public class UserEventActivity extends BaseBackActivity {


    @Bind(R.id.lay_container)
    FrameLayout mContainer;

    @Override
    protected int getContentView() {
        return R.layout.activity_main_user_event;
    }

    @Override
    protected void initData() {
        super.initData();

        SubTab tab = new SubTab();
        UserEventFragment.newInstance(this, tab);

       // getSupportFragmentManager().beginTransaction().replace(R.id.lay_container, ).
    }
}
