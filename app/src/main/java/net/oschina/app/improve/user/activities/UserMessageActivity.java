package net.oschina.app.improve.user.activities;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseBackActivity;

import butterknife.Bind;

/**
 * Created by huanghaibin_dev
 * on 2016/8/16.
 */
public class UserMessageActivity extends BaseBackActivity {

    @Bind(R.id.tabLayout)
    TabLayout tabLayout;
    @Bind(R.id.vp_user_message)
    ViewPager vp_user_message;

    public static void show(Context context) {
        context.startActivity(new Intent(context, UserMessageActivity.class));
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_user_message;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        vp_user_message.setAdapter(mAdapter);
        tabLayout.setupWithViewPager(vp_user_message);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    private FragmentStatePagerAdapter mAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
        @Override
        public Fragment getItem(int position) {
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return super.getPageTitle(position);
        }
    };
}
