package net.oschina.app.improve.user.activities;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

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
 * on 2016/8/16.
 */
public class UserMessageActivity extends BaseBackActivity {

    @Bind(R.id.tabLayout)
    TabLayout tabLayout;
    @Bind(R.id.vp_user_message)
    ViewPager vp_user_message;

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

        tabLayout.setupWithViewPager(vp_user_message);
        vp_user_message.setAdapter(mAdapter);
        vp_user_message.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    if (mNotice.getMention() > 0)
                        NoticeManager.clear(getApplicationContext(), NoticeManager.FLAG_CLEAR_MENTION);
                } else if (position == 1) {
                    if (mNotice.getReview() > 0)
                        NoticeManager.clear(getApplicationContext(), NoticeManager.FLAG_CLEAR_REVIEW);
                } else {
                    if (mNotice.getLetter() > 0)
                        NoticeManager.clear(getApplicationContext(), NoticeManager.FLAG_CLEAR_LETTER);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        int currentView = 0;
        if (mNotice.getMention() > 0) {
            NoticeManager.clear(getApplicationContext(), NoticeManager.FLAG_CLEAR_MENTION);
        } else if (mNotice.getReview() > 0) {
            currentView = 1;
        } else if (mNotice.getLetter() > 0) {
            currentView = 2;
        }
        vp_user_message.setCurrentItem(currentView);

    }

    private FragmentStatePagerAdapter mAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return mUserMentionFragment;
            } else if (position == 1) {
                return mUserCommentFragment;
            }
            return mUserMessageFragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return formatMessageCount("@我", mNotice.getMention());
            } else if (position == 1) {
                return formatMessageCount("评论", mNotice.getReview());
            }
            return formatMessageCount("私信", mNotice.getLetter());
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

        }

    };

    private String formatMessageCount(String title, int messageCount) {
        return messageCount == 0 ? title : String.format(title + "（%s）", messageCount);
    }
}
