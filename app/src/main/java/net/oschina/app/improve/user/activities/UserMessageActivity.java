package net.oschina.app.improve.user.activities;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseBackActivity;
import net.oschina.app.improve.notice.NoticeBean;
import net.oschina.app.improve.notice.NoticeManager;
import net.oschina.app.improve.user.fragments.UserCommentFragment;
import net.oschina.app.improve.user.fragments.UserMentionFragment;
import net.oschina.app.improve.user.fragments.UserMessageFragment;

import butterknife.Bind;

/**
 * Created by huanghaibin_dev
 * Updated by Dominic Thanatosx
 * on 2016/8/16.
 */
public class UserMessageActivity extends BaseBackActivity implements NoticeManager.NoticeNotify {

    @Bind(R.id.tabLayout)       TabLayout mLayoutTab;
    @Bind(R.id.vp_user_message) ViewPager mViewPager;

    private static final int INDEX_MENTION = 0;
    private static final int INDEX_COMMENT = 1;
    private static final int INDEX_MESSAGE = 2;

    private UserMentionFragment mUserMentionFragment;
    private UserCommentFragment mUserCommentFragment;
    private UserMessageFragment mUserMessageFragment;

    private NoticeBean mNotice;

    public static void show (Context context) {

        context.startActivity(new Intent(context, UserMessageActivity.class));
    }

    @Override
    protected int getContentView () {
        return R.layout.activity_user_message;
    }

    @Override
    protected void initWidget () {
        super.initWidget();
        mNotice = NoticeManager.getNotice();

        mUserMentionFragment = new UserMentionFragment();
        mUserCommentFragment = new UserCommentFragment();
        mUserMessageFragment = new UserMessageFragment();

        NoticeManager.bindNotify(this);

        mLayoutTab.setupWithViewPager(mViewPager);
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected (int position) {
                clearSpecificNoticeIfNecessary(position);
            }
        });

        final int mCurrentViewIndex = mNotice.getMention() > 0
                ? INDEX_MENTION
                : mNotice.getReview() > 0
                ? INDEX_COMMENT
                : mNotice.getLetter() > 0
                ? INDEX_MESSAGE
                : INDEX_MENTION;

        mViewPager.setCurrentItem(mCurrentViewIndex);
        clearSpecificNoticeIfNecessary(mCurrentViewIndex);
    }

    private void clearSpecificNoticeIfNecessary(int position) {
        switch (position) {
            case INDEX_MENTION:
                if (mNotice.getMention() <= 0) break;
                clearSpecificNotice(position);
                break;
            case INDEX_COMMENT:
                if (mNotice.getReview() <= 0) break;
                clearSpecificNotice(position);
                break;
            case INDEX_MESSAGE:
                if (mNotice.getLetter() <= 0) break;
                clearSpecificNotice(position);
                break;
        }
    }

    private void clearSpecificNotice(int position) {
        switch (position) {
            case INDEX_MENTION:
                NoticeManager
                        .clear(getApplicationContext(), NoticeManager.FLAG_CLEAR_MENTION);
                break;
            case INDEX_COMMENT:
                NoticeManager
                        .clear(getApplicationContext(), NoticeManager.FLAG_CLEAR_REVIEW);
                break;
            case INDEX_MESSAGE:
                NoticeManager
                        .clear(getApplicationContext(), NoticeManager.FLAG_CLEAR_LETTER);
                break;
        }
    }

    private void postChangeTitle (final int position, int delay) {
        mViewPager.postDelayed(new Runnable() {
            @Override
            public void run () {
                TabLayout.Tab tab = mLayoutTab.getTabAt(position);
                if (tab == null) return;
                tab.setText(mAdapter.getPageTitle(position));
            }
        }, delay);
    }

    public void onRequestSuccess (int position) {
        if (mViewPager.getCurrentItem() != position) return;
        clearSpecificNoticeIfNecessary(position);
    }

    private void analyzeOldAndNew(int _old, int _new, int position) {
        if (_old == _new) return;
        if (mViewPager.getCurrentItem() != position || _new != 0) {
            postChangeTitle(position, 0);
        }else {
            postChangeTitle(position, 1500);
        }
    }

    @Override
    public void onNoticeArrived (NoticeBean bean) {
        NoticeBean nb = mNotice;
        mNotice = bean;
        analyzeOldAndNew(nb.getMention(), bean.getMention(), INDEX_MENTION);
        analyzeOldAndNew(nb.getReview(), bean.getReview(), INDEX_COMMENT);
        analyzeOldAndNew(nb.getLetter(), bean.getLetter(), INDEX_MESSAGE);
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        NoticeManager.unBindNotify(this);
        clearSpecificNotice(INDEX_MENTION);
        clearSpecificNotice(INDEX_COMMENT);
        clearSpecificNotice(INDEX_MESSAGE);
    }

    private FragmentPagerAdapter mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
        @Override
        public Fragment getItem (int position) {
            switch (position) {
                case INDEX_MENTION:
                    return mUserMentionFragment;
                case INDEX_COMMENT:
                    return mUserCommentFragment;
                default:
                    return mUserMessageFragment;
            }
        }

        @Override
        public int getCount () {
            return 3;
        }

        @Override
        public CharSequence getPageTitle (int position) {
            switch (position) {
                case INDEX_MENTION:
                    return formatMessageCount("@我", mNotice.getMention());
                case INDEX_COMMENT:
                    return formatMessageCount("评论", mNotice.getReview());
                default:
                    return formatMessageCount("私信", mNotice.getLetter());
            }
        }
    };

    private String formatMessageCount (String title, int messageCount) {
        return messageCount == 0 ? title : String.format(title + "（%s）", messageCount);
    }
}
