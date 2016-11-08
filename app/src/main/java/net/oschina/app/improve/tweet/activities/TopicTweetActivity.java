package net.oschina.app.improve.tweet.activities;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseActivity;
import net.oschina.app.improve.tweet.fragments.TweetFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 动弹话题详情
 * Created by thanatosx on 2016/11/8.
 */

public class TopicTweetActivity extends BaseActivity {

    @Bind(R.id.layout_appbar) AppBarLayout mLayoutAppBar;
    @Bind(R.id.iv_wallpaper) ImageView mViewWallpaper;
    @Bind(R.id.tv_title) TextView mViewTitle;
    @Bind(R.id.tv_mix_title) TextView mViewMixTitle;
    @Bind(R.id.tv_count) TextView mViewCount;
    @Bind(R.id.tv_description) TextView mViewDescription;
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.layout_tab) TabLayout mLayoutTab;
    @Bind(R.id.view_pager) ViewPager mViewPager;

    private List<Pair<String, Fragment>> fragments;
    private TabLayoutOffsetChangeListener mOffsetChangerListener;

    public static void show(Context context){
        Intent intent = new Intent(context, TopicTweetActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_topic_tweet;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mToolbar.setTitle("");
        mToolbar.setSubtitle("");
        mToolbar.setNavigationIcon(R.mipmap.btn_back_normal);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mViewTitle.setText("#开源中国客户端#");
        mViewMixTitle.setText("#开源中国客户端#");
        mViewCount.setText("共有 212 人参与");
        mViewDescription.setText("你对开源中国客户端有什么看法呢？或者有什么好的idea想与大家分享？不要吝啬你的手指，赶快来忘记我吧！");
        mLayoutAppBar.addOnOffsetChangedListener(mOffsetChangerListener = new TabLayoutOffsetChangeListener());

        fragments = new ArrayList<>();
        fragments.add(Pair.create("最新", TweetFragment.instantiate(TweetFragment.CATEGORY_TYPE, 1)));
        fragments.add(Pair.create("最热", TweetFragment.instantiate(TweetFragment.CATEGORY_TYPE, 2)));

        mViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position).second;
            }

            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return fragments.get(position).first;
            }
        });
        mLayoutTab.setupWithViewPager(mViewPager);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    private class TabLayoutOffsetChangeListener implements AppBarLayout.OnOffsetChangedListener {
        boolean isShow = false;
        int mScrollRange = -1;

        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            if (mScrollRange == -1) {
                mScrollRange = appBarLayout.getTotalScrollRange();
            }
            if (mScrollRange + verticalOffset == 0) {
                mViewMixTitle.setVisibility(View.VISIBLE);
                isShow = true;
            } else if (isShow) {
                mViewMixTitle.setVisibility(View.GONE);
                isShow = false;
            }
        }

        public void resetRange() {
            mScrollRange = -1;
        }
    }
}
