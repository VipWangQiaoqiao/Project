package net.oschina.app.improve.main.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.improve.base.fragments.BaseGeneralListFragment;
import net.oschina.app.improve.base.fragments.BaseViewPagerFragment;
import net.oschina.app.improve.tweet.fragments.TweetFragment;
import net.oschina.app.interf.OnTabReselectListener;

/**
 * Created by fei
 * on 2016/9/5.
 * <p>
 * Changed qiujuer
 * on 2016/9/5.
 */
public class TweetViewPagerFragment extends BaseViewPagerFragment implements OnTabReselectListener {

    /**
     * @param requestCategory 请求类型，1为普通动弹，2用户动弹
     * @param tweetType       1最新，2最热
     * @return Bundle
     */
    private Bundle getBundle(int requestCategory, int tweetType) {
        Bundle bundle = new Bundle();
        bundle.putInt("requestCategory", requestCategory);
        bundle.putInt("tweetType", tweetType);
        return bundle;
    }

    @Override
    public void onTabReselect() {

        if (mBaseViewPager != null) {
            BaseViewPagerAdapter pagerAdapter = (BaseViewPagerAdapter) mBaseViewPager.getAdapter();
            Fragment fragment = pagerAdapter.getCurFragment();
            if (fragment != null && fragment instanceof BaseGeneralListFragment) {
                ((BaseGeneralListFragment) fragment).onTabReselect();
            }
        }
    }

    @Override
    protected PagerInfo[] getPagers() {

        String[] titles = getResources().getStringArray(R.array.tweets_viewpage_arrays);
        PagerInfo[] infoList = new PagerInfo[3];

        infoList[0] = new PagerInfo(titles[0], TweetFragment.class,
                getBundle(TweetFragment.CATEGORY_TYPE, TweetFragment.TWEET_TYPE_NEW));
        infoList[1] = new PagerInfo(titles[1], TweetFragment.class,
                getBundle(TweetFragment.CATEGORY_TYPE, TweetFragment.TWEET_TYPE_HOT));
        infoList[2] = new PagerInfo(titles[2], TweetFragment.class,
                getBundle(TweetFragment.CATEGORY_USER, AppContext.getInstance().getLoginUid()));

        return infoList;
    }

    @Override
    protected int getTitleRes() {
        return R.string.main_tab_name_tweet;
    }
}
