package net.oschina.app.improve.tweet.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import net.oschina.app.R;
import net.oschina.app.adapter.ViewPageFragmentAdapter;
import net.oschina.app.base.BaseViewPagerFragment;
import net.oschina.app.improve.base.fragments.BaseGeneralListFragment;
import net.oschina.app.interf.OnTabReselectListener;


/**
 * 动弹ViewPagerFragment
 * Created by huanghaibin_dev
 * on 2016/7/19.
 */
public class TweetViewPagerFragment extends BaseViewPagerFragment implements
        OnTabReselectListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
        String[] title = getResources().getStringArray(
                R.array.tweets_viewpage_arrays);

        adapter.addTab(title[0], "tweet_new", TweetFragment.class,
                getBundle(TweetFragment.CATEGORY_TYPE, TweetFragment.TWEET_TYPE_NEW));
        adapter.addTab(title[1], "tweet_hot", TweetFragment.class,
                getBundle(TweetFragment.CATEGORY_TYPE, TweetFragment.TWEET_TYPE_HOT));
        adapter.addTab(title[2], "tweet_mine", TweetFragment.class,
                getBundle(TweetFragment.CATEGORY_USER, 0));
    }

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
    protected void setScreenPageLimit() {
        mViewPager.setOffscreenPageLimit(3);
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void initView(View view) {

    }

    @Override
    public void initData() {

    }

    @Override
    public void onTabReselect() {
        Fragment fragment = mTabsAdapter.getItem(mViewPager.getCurrentItem());
        if (fragment != null && fragment instanceof BaseGeneralListFragment) {
            ((BaseGeneralListFragment) fragment).onTabReselect();
        }
    }
}