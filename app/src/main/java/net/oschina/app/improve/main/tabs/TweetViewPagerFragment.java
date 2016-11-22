package net.oschina.app.improve.main.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import net.oschina.app.R;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.base.fragments.BaseGeneralListFragment;
import net.oschina.app.improve.base.fragments.BaseGeneralRecyclerFragment;
import net.oschina.app.improve.base.fragments.BaseViewPagerFragment;
import net.oschina.app.improve.search.activities.SearchActivity;
import net.oschina.app.improve.tweet.fragments.TopicTweetFragment;
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
            if (fragment != null) {
                if (fragment instanceof BaseGeneralListFragment)
                    ((BaseGeneralListFragment) fragment).onTabReselect();
                else if (fragment instanceof BaseGeneralRecyclerFragment)
                    ((BaseGeneralRecyclerFragment) fragment).onTabReselect();
            }
        }
    }

    @Override
    protected PagerInfo[] getPagers() {

        /*PagerInfo[] infoList = new PagerInfo[4];

        infoList[0] = new PagerInfo("好友动弹", TweetFragment.class,
                getBundle(TweetFragment.CATEGORY_FRIEND, 0));

        infoList[1] = new PagerInfo("推荐话题", TopicTweetFragment.class, null);

        infoList[2] = new PagerInfo("热门动弹", TweetFragment.class,
                getBundle(TweetFragment.CATEGORY_TYPE, TweetFragment.TWEET_TYPE_HOT));

        infoList[3] = new PagerInfo("最新动弹", TweetFragment.class,
                getBundle(TweetFragment.CATEGORY_TYPE, TweetFragment.TWEET_TYPE_NEW));*/

        PagerInfo[] infoList = new PagerInfo[3];

        infoList[2] = new PagerInfo("我的动弹", TweetFragment.class,
                getBundle(TweetFragment.CATEGORY_USER, (int) AccountHelper.getUserId()));
        infoList[1] = new PagerInfo("热门动弹", TweetFragment.class,
                getBundle(TweetFragment.CATEGORY_TYPE, TweetFragment.TWEET_TYPE_HOT));
        infoList[0] = new PagerInfo("最新动弹", TweetFragment.class,
                getBundle(TweetFragment.CATEGORY_TYPE, TweetFragment.TWEET_TYPE_NEW));

        return infoList;
    }

    @Override
    protected int getTitleRes() {
        return R.string.main_tab_name_tweet;
    }

    @Override
    protected int getIconRes() {
        return R.mipmap.btn_search_normal;
    }

    @Override
    protected View.OnClickListener getIconClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchActivity.show(getContext());
            }
        };
    }
}
