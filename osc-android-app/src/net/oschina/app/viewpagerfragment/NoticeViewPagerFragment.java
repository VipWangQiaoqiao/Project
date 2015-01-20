package net.oschina.app.viewpagerfragment;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.adapter.ViewPageFragmentAdapter;
import net.oschina.app.base.BaseListFragment;
import net.oschina.app.base.BaseViewPagerFragment;
import net.oschina.app.bean.ActiveList;
import net.oschina.app.bean.Constants;
import net.oschina.app.bean.FriendsList;
import net.oschina.app.bean.Notice;
import net.oschina.app.fragment.ActiveFragment;
import net.oschina.app.fragment.FriendsFragment;
import net.oschina.app.fragment.MessageFragment;
import net.oschina.app.ui.MainActivity;
import net.oschina.app.widget.BadgeView;
import net.oschina.app.widget.PagerSlidingTabStrip.OnPagerChangeLis;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;

/**
 * 消息中心页面
 * 
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @created 2014年9月25日 下午2:21:52
 * 
 */
public class NoticeViewPagerFragment extends BaseViewPagerFragment {

    public BadgeView mBvAtMe, mBvComment, mBvMsg, mBvFans;
    public static boolean[] sRefreshed = new boolean[] { false, false, false,
            false };
    public static int sCurrentPage = 0;
    private BroadcastReceiver mNoticeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setNoticeTip();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        setNoticeTip();
        changePagers();
        mViewPager.setOffscreenPageLimit(2);
    }

    /**
     * 设置tip
     */
    private void setNoticeTip() {
        if (MainActivity.mNotice != null) {
            Notice notice = MainActivity.mNotice;
            changeTip(mBvAtMe, notice.getAtmeCount());// @我
            changeTip(mBvComment, notice.getReviewCount());// 评论
            changeTip(mBvMsg, notice.getMsgCount());// 留言
            changeTip(mBvFans, notice.getNewFansCount());// 新粉丝
        } else {
            switch (mViewPager.getCurrentItem()) {
            case 0:
                changeTip(mBvAtMe, -1);
                break;
            case 1:
                changeTip(mBvComment, -1);
                break;
            case 2:
                changeTip(mBvMsg, -1);
                break;
            case 3:
                changeTip(mBvFans, -1);
                break;
            }
        }
    }

    /**
     * 判断指定控件是否应该显示tip红点
     * 
     * @author kymjs
     */
    private void changeTip(BadgeView view, int count) {
        if (count > 0) {
            view.setText(count + "");
            view.show();
        } else {
            view.hide();
        }
    }

    /**
     * 切换到有tip的page
     */
    private void changePagers() {
        Notice notice = MainActivity.mNotice;
        if (notice == null) {
            return;
        }
        if (notice.getAtmeCount() != 0) {
            mViewPager.setCurrentItem(0);
        } else if (notice.getReviewCount() != 0) {
            mViewPager.setCurrentItem(1);
        } else if (notice.getMsgCount() != 0) {
            mViewPager.setCurrentItem(2);
        } else if (notice.getNewFansCount() != 0) {
            mViewPager.setCurrentItem(3);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        IntentFilter filter = new IntentFilter(Constants.INTENT_ACTION_NOTICE);
        getActivity().registerReceiver(mNoticeReceiver, filter);
        mBvAtMe = new BadgeView(getActivity(), mTabStrip.getBadgeView(0));
        mBvAtMe.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        mBvAtMe.setBadgePosition(BadgeView.POSITION_CENTER);
        mBvAtMe.setGravity(Gravity.CENTER);
        mBvAtMe.setBackgroundResource(R.drawable.notification_bg);

        mBvComment = new BadgeView(getActivity(), mTabStrip.getBadgeView(1));
        mBvComment.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        mBvComment.setBadgePosition(BadgeView.POSITION_CENTER);
        mBvComment.setGravity(Gravity.CENTER);
        mBvComment.setBackgroundResource(R.drawable.notification_bg);

        mBvMsg = new BadgeView(getActivity(), mTabStrip.getBadgeView(2));
        mBvMsg.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        mBvMsg.setBadgePosition(BadgeView.POSITION_CENTER);
        mBvMsg.setGravity(Gravity.CENTER);
        mBvMsg.setBackgroundResource(R.drawable.notification_bg);

        mBvFans = new BadgeView(getActivity(), mTabStrip.getBadgeView(3));
        mBvFans.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        mBvFans.setBadgePosition(BadgeView.POSITION_CENTER);
        mBvFans.setGravity(Gravity.CENTER);
        mBvFans.setBackgroundResource(R.drawable.notification_bg);

        mTabStrip.getBadgeView(0).setVisibility(View.VISIBLE);
        mTabStrip.getBadgeView(1).setVisibility(View.VISIBLE);
        mTabStrip.getBadgeView(2).setVisibility(View.VISIBLE);
        mTabStrip.getBadgeView(3).setVisibility(View.VISIBLE);
        initData();
        initView(view);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mNoticeReceiver);
        mNoticeReceiver = null;
    }

    @Override
    protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
        String[] title = getResources().getStringArray(
                R.array.mymes_viewpage_arrays);
        adapter.addTab(title[0], "active_me", ActiveFragment.class,
                getBundle(ActiveList.CATALOG_ATME));
        adapter.addTab(title[1], "active_comment", ActiveFragment.class,
                getBundle(ActiveList.CATALOG_COMMENT));
        adapter.addTab(title[2], "active_mes", MessageFragment.class, null);
        Bundle bundle = getBundle(FriendsList.TYPE_FANS);
        bundle.putInt(FriendsFragment.BUNDLE_KEY_UID, AppContext.getInstance()
                .getLoginUid());
        adapter.addTab(title[3], "active_fans", FriendsFragment.class, bundle);
    }

    private Bundle getBundle(int catalog) {
        Bundle bundle = new Bundle();
        bundle.putInt(BaseListFragment.BUNDLE_KEY_CATALOG, catalog);
        return bundle;
    }

    @Override
    public void onClick(View v) {}

    @Override
    public void initView(View view) {
        mTabStrip.setOnPagerChange(new OnPagerChangeLis() {
            @Override
            public void onChanged(int page) {
                sRefreshed[page] = true;
                sCurrentPage = page;
            }
        });
    }

    @Override
    public void initData() {}
}
