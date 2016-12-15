package net.oschina.app.improve.user.activities;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import net.oschina.app.R;
import net.oschina.app.bean.Notice;
import net.oschina.app.improve.base.activities.BaseBackActivity;
import net.oschina.app.improve.notice.NoticeBean;
import net.oschina.app.improve.notice.NoticeManager;
import net.oschina.app.improve.user.fragments.UserCommentFragment;
import net.oschina.app.improve.user.fragments.UserMentionFragment;
import net.oschina.app.improve.user.fragments.UserMessageFragment;
import net.oschina.app.util.TLog;

import butterknife.Bind;

/**
 * Created by huanghaibin_dev
 * Updated by Dominic Thanatosx
 * on 2016/8/16.
 */
public class UserMessageActivity extends BaseBackActivity implements NoticeManager.NoticeNotify{

    @Bind(R.id.tabLayout)       TabLayout mLayoutTab;
    @Bind(R.id.vp_user_message) ViewPager mViewPager;

    private UserMentionFragment mUserMentionFragment;
    private UserCommentFragment mUserCommentFragment;
    private UserMessageFragment mUserMessageFragment;

    private NoticeBean mNotice;

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
        mNotice = NoticeManager.getNotice();

        mUserMentionFragment = new UserMentionFragment();
        mUserCommentFragment = new UserCommentFragment();
        mUserMessageFragment = new UserMessageFragment();

        NoticeManager.bindNotify(this);

        mLayoutTab.setupWithViewPager(mViewPager);
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                TLog.i("oschina", "On Page Select: " + position);
                switch (position) {
                    case 0:
                        if (mNotice.getMention() <= 0) break;
                        NoticeManager.clear(getApplicationContext(), NoticeManager.FLAG_CLEAR_MENTION);
                        break;
                    case 1:
                        if (mNotice.getReview() <= 0) break;
                        NoticeManager.clear(getApplicationContext(), NoticeManager.FLAG_CLEAR_REVIEW);
                        break;
                    default:
                        if (mNotice.getLetter() <= 0) break;
                        NoticeManager.clear(getApplicationContext(), NoticeManager.FLAG_CLEAR_LETTER);
                        break;
                }
            }
        });

        final int mCurrentViewIndex = mNotice.getMention() > 0
                ? 0
                : mNotice.getReview() > 0
                ? 1
                : mNotice.getLetter() > 0
                ? 2
                : 0;

        mViewPager.setCurrentItem(mCurrentViewIndex);

        switch (mCurrentViewIndex) {
            case 0:
                NoticeManager.clear(getApplicationContext(), NoticeManager.FLAG_CLEAR_MENTION);
                break;
            case 1:
                NoticeManager.clear(getApplicationContext(), NoticeManager.FLAG_CLEAR_REVIEW);
                break;
            default:
                NoticeManager.clear(getApplicationContext(), NoticeManager.FLAG_CLEAR_LETTER);
                break;
        }
    }

    private void postChangeTitle(final int position, int delay){
        mViewPager.postDelayed(new Runnable() {
            @Override
            public void run() {
                TabLayout.Tab tab = mLayoutTab.getTabAt(position);
                if (tab == null) return;
                tab.setText(mAdapter.getPageTitle(position));
            }
        }, delay);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NoticeManager.unBindNotify(this);
        NoticeManager.clear(getApplicationContext(), NoticeManager.FLAG_CLEAR_MENTION);
        NoticeManager.clear(getApplicationContext(), NoticeManager.FLAG_CLEAR_REVIEW);
        NoticeManager.clear(getApplicationContext(), NoticeManager.FLAG_CLEAR_LETTER);
    }

    private FragmentPagerAdapter mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return mUserMentionFragment;
                case 1:
                    return mUserCommentFragment;
                default:
                    return mUserMessageFragment;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return formatMessageCount("@我", mNotice.getMention());
                case 1:
                    return formatMessageCount("评论", mNotice.getReview());
                default:
                    return formatMessageCount("私信", mNotice.getLetter());
            }
        }
    };

    private String formatMessageCount(String title, int messageCount) {
        return messageCount == 0 ? title : String.format(title + "（%s）", messageCount);
    }

    public void onRequestSuccess(int position) {
        if (mViewPager.getCurrentItem() != position) return;
        switch (position) {
            case 0:
                if (mNotice.getMention() <= 0) break;
                NoticeManager.clear(getApplicationContext(), NoticeManager.FLAG_CLEAR_MENTION);
                break;
            case 1:
                if (mNotice.getReview() <= 0) break;
                NoticeManager.clear(getApplicationContext(), NoticeManager.FLAG_CLEAR_REVIEW);
                break;
            default:
                if (mNotice.getLetter() <= 0) break;
                NoticeManager.clear(getApplicationContext(), NoticeManager.FLAG_CLEAR_LETTER);
                break;
        }
    }

    @Override
    public void onNoticeArrived(NoticeBean bean) {
        TLog.i("oschina", "On Notice Arrived");
        NoticeBean nb = mNotice;
        mNotice = bean;
        if (nb.getMention() != bean.getMention()) {
            if (mViewPager.getCurrentItem() == 0) {
                if (bean.getMention() == 0){
                    postChangeTitle(0, 1500);
                }else {
                    //mUserMentionFragment.onRefreshing();
                    //NoticeManager.clear(getApplicationContext(), NoticeManager.FLAG_CLEAR_MENTION);
                    postChangeTitle(0, 0);
                }
            } else {
                postChangeTitle(0, 0);
            }
        }

        if (nb.getReview() != bean.getReview()) {
            if (mViewPager.getCurrentItem() == 1) {
                if (bean.getReview() == 0){
                    postChangeTitle(1, 1500);
                }else {
                    //mUserCommentFragment.onRefreshing();
                    //NoticeManager.clear(getApplicationContext(), NoticeManager.FLAG_CLEAR_REVIEW);
                    postChangeTitle(1, 0);
                }
            } else {
                postChangeTitle(1, 0);
            }
        }

        if (nb.getLetter() != bean.getLetter()) {
            if (mViewPager.getCurrentItem() == 2) {
                if (bean.getLetter() == 0){
                    postChangeTitle(2, 1500);
                }else {
                    //mUserMessageFragment.onRefreshing();
                    //NoticeManager.clear(getApplicationContext(), NoticeManager.FLAG_CLEAR_LETTER);
                    postChangeTitle(2, 0);
                }
            } else {
                postChangeTitle(2, 0);
            }
        }
    }
}
