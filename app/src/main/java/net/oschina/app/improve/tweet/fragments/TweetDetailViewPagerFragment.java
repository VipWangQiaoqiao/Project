package net.oschina.app.improve.tweet.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.oschina.app.R;
import net.oschina.app.bean.User;
import net.oschina.app.improve.bean.simple.TweetComment;
import net.oschina.app.improve.tweet.contract.TweetDetailContract;

/**
 * 赞 | 评论
 * Created by thanatos on 16/6/12.
 * <p>
 * TweetDetailActivity    TweetDetailViewPagerFragment   ListTweetCommentFragment    ListTweetLikeUsersFragment
 * |                         |                            |                            |
 * |   on comment successful |    on comment successful   |                            |
 * | ----------------------> | -------------------------->|                            |
 * |                         |                            |                            |
 * |   on admire successful  |                 on admire successful                    |
 * | ----------------------> | ----------------------------------------------------->  |
 * |                         |                            |                            |
 * | to reset comment count  |  to reset comment count    |                            |
 * | ----------------------> | <------------------------- |                            |
 * |                         |                            |                            |
 * | to admire comment count |              to admire comment count                    |
 * | ----------------------> | <-------------------------------------------------------|
 * |                         |                            |                            |
 * |                onScroll, getTweetDetail...           |                            |
 * | <----------------------------------------------------|                            |
 * |                         |                            |                            |
 * |                                  onScroll                                         |
 * |<----------------------------------------------------------------------------------|
 */
public class TweetDetailViewPagerFragment extends Fragment
        implements TweetDetailContract.ICmnView, TweetDetailContract.IThumbupView, TweetDetailContract.IAgencyView {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    protected FragmentStatePagerAdapter mAdapter;
    private TweetDetailContract.ICmnView mCmnViewImp;
    private TweetDetailContract.IThumbupView mThumbupViewImp;
    private TweetDetailContract.Operator mOperator;

    public static TweetDetailViewPagerFragment instantiate() {
        return new TweetDetailViewPagerFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mOperator = (TweetDetailContract.Operator) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweet_view_pager, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
        mTabLayout = (TabLayout) view.findViewById(R.id.tab_nav);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mAdapter == null) {
            final ListTweetLikeUsersFragment mCmnFrag;
            mThumbupViewImp = mCmnFrag = ListTweetLikeUsersFragment.instantiate(mOperator, this);

            final ListTweetCommentFragment mThumbupFrag;
            mCmnViewImp = mThumbupFrag = ListTweetCommentFragment.instantiate(mOperator, this);

            mViewPager.setAdapter(mAdapter = new FragmentStatePagerAdapter(getChildFragmentManager()) {
                @Override
                public Fragment getItem(int position) {
                    switch (position) {
                        case 0:
                            return mCmnFrag;

                        case 1:
                            return mThumbupFrag;

                    }
                    return null;
                }

                @Override
                public int getCount() {
                    return 2;
                }

                @Override
                public CharSequence getPageTitle(int position) {
                    switch (position) {
                        case 0:
                            return String.format("赞(%s)", mOperator.getTweetDetail().getLikeCount());
                        case 1:
                            return String.format("评论(%s)", mOperator.getTweetDetail().getCommentCount());
                    }
                    return null;
                }
            });
            mTabLayout.setupWithViewPager(mViewPager);
            mViewPager.setCurrentItem(1);
        } else {
            mViewPager.setAdapter(mAdapter);
        }
    }

    @Override
    public void onCommentSuccess(TweetComment comment) {
        mOperator.getTweetDetail().setCommentCount(mOperator.getTweetDetail().getCommentCount() + 1); // Bean的事,真不是我想这样干
        if (mCmnViewImp != null) mCmnViewImp.onCommentSuccess(comment);
        TabLayout.Tab tab = mTabLayout.getTabAt(1);
        if (tab != null)
            tab.setText(String.format("评论(%s)", mOperator.getTweetDetail().getCommentCount()));
    }

    @Override
    public void onLikeSuccess(boolean isUp, User user) {
        mOperator.getTweetDetail().setLikeCount(mOperator.getTweetDetail().getLikeCount() + (isUp ? 1 : -1));
        if (mThumbupViewImp != null) mThumbupViewImp.onLikeSuccess(isUp, user);
        TabLayout.Tab tab = mTabLayout.getTabAt(0);
        if (tab != null)
            tab.setText(String.format("赞(%s)", mOperator.getTweetDetail().getLikeCount()));
    }

    @Override
    public void resetLikeCount(int count) {
        mOperator.getTweetDetail().setLikeCount(count);
        TabLayout.Tab tab = mTabLayout.getTabAt(0);
        if (tab != null) tab.setText(String.format("赞(%s)", count));
    }

    @Override
    public void resetCmnCount(int count) {
        mOperator.getTweetDetail().setCommentCount(count);
        TabLayout.Tab tab = mTabLayout.getTabAt(1);
        if (tab != null) tab.setText(String.format("评论(%s)", count));
    }

    public TweetDetailContract.ICmnView getCommentViewHandler() {
        return this;
    }

    public TweetDetailContract.IThumbupView getThumbupViewHandler() {
        return this;
    }

    public TweetDetailContract.IAgencyView getAgencyViewHandler() {
        return this;
    }
}
