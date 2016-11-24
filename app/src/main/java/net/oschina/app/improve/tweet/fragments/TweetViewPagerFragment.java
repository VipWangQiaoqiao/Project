package net.oschina.app.improve.tweet.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.adapter.ViewPageFragmentAdapter;
import net.oschina.app.base.BaseViewPagerFragment;
import net.oschina.app.improve.base.fragments.BaseGeneralListFragment;
import net.oschina.app.improve.utils.UIUtil;
import net.oschina.app.interf.OnTabReselectListener;


/**
 * 动弹ViewPagerFragment
 * Created by huanghaibin_dev
 * on 2016/7/19.
 */
public class TweetViewPagerFragment extends BaseViewPagerFragment implements
        OnTabReselectListener {

    @Override
    protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {

        FrameLayout generalActionBar = (FrameLayout) mRoot.findViewById(R.id.general_actionbar);
        TextView tvTitle = (TextView) generalActionBar.findViewById(R.id.tv_explore_scan);
        ImageView ivDiscover = (ImageView) generalActionBar.findViewById(R.id.iv_explore_discover);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) generalActionBar.getLayoutParams();
            layoutParams.topMargin = UIUtil.getStatusHeight(getActivity());
        }
        tvTitle.setText(R.string.main_tab_name_tweet);
        ivDiscover.setVisibility(View.INVISIBLE);

        String[] title = getResources().getStringArray(
                R.array.tweets_viewpage_arrays);

        adapter.addTab(title[0], "tweet_new", TweetFragment.class,
                getBundle(TweetFragment.CATALOG_NEW));
        adapter.addTab(title[1], "tweet_hot", TweetFragment.class,
                getBundle(TweetFragment.CATALOG_HOT));
        adapter.addTab(title[2], "tweet_mine", TweetFragment.class,
                getBundle(TweetFragment.CATALOG_MYSELF));

    }

    /**
     * @param catalog {@link TweetFragment}
     * @return Bundle
     */
    private Bundle getBundle(int catalog) {
        Bundle bundle = new Bundle();
        bundle.putInt(TweetFragment.BUNDLE_KEY_REQUEST_CATALOG, catalog);
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
        FrameLayout actionBar = (FrameLayout) view.findViewById(R.id.general_actionbar);
        TextView tvTweetTitle = (TextView) actionBar.findViewById(R.id.tv_explore_scan);
        tvTweetTitle.setText(R.string.main_tab_name_tweet);
        ImageView ivDiscover = (ImageView) actionBar.findViewById(R.id.iv_explore_discover);
        ivDiscover.setVisibility(View.INVISIBLE);

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