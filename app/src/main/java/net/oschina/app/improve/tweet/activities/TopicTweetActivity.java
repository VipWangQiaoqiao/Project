package net.oschina.app.improve.tweet.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.oschina.app.R;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.base.activities.BaseActivity;
import net.oschina.app.improve.bean.simple.TweetComment;
import net.oschina.app.improve.behavior.CommentBar;
import net.oschina.app.improve.tweet.fragments.TweetFragment;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.util.UIHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 动弹话题详情
 * Created by thanatosx on 2016/11/8.
 */

public class TopicTweetActivity extends BaseActivity {

    @Bind(R.id.layout_coordinator)
    CoordinatorLayout mLayoutCoordinator;
    @Bind(R.id.layout_appbar)
    AppBarLayout mLayoutAppBar;
    @Bind(R.id.iv_wallpaper)
    ImageView mViewWallpaper;
    @Bind(R.id.tv_title)
    TextView mViewTitle;
    @Bind(R.id.tv_mix_title)
    TextView mViewMixTitle;
    @Bind(R.id.tv_count)
    TextView mViewCount;
    @Bind(R.id.tv_description)
    TextView mViewDescription;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.layout_tab)
    TabLayout mLayoutTab;
    @Bind(R.id.view_pager)
    ViewPager mViewPager;

    EditText mViewInput;
    private Dialog dialog;
    private TabLayoutOffsetChangeListener mOffsetChangerListener;
    private CommentBar mDelegation;

    private List<Pair<String, Fragment>> fragments;
    private List<TweetComment> replies = new ArrayList<>();

    public static void show(Context context) {
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
        fragments.add(Pair.create("最新", TweetFragment.instantiate(TweetFragment.CATALOG_NEW)));
        fragments.add(Pair.create("最热", TweetFragment.instantiate(TweetFragment.CATALOG_HOT)));

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

        mDelegation = CommentBar.delegation(this, mLayoutCoordinator);

        mDelegation.getBottomSheet().getEditText().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    handleKeyDel();
                }
                return false;
            }
        });

        mDelegation.getBottomSheet().showEmoji();
        mDelegation.getBottomSheet().setCommitListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mDelegation.getBottomSheet().getCommentText().replaceAll("[\\s\\n]+", " ");
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(TopicTweetActivity.this, "请输入文字", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!AccountHelper.isLogin()) {
                    UIHelper.showLoginActivity(TopicTweetActivity.this);
                    return;
                }
                if (replies != null && replies.size() > 0)
                    content = mViewInput.getHint() + ": " + content;
                dialog = DialogHelper.getProgressDialog(TopicTweetActivity.this, "正在发表评论...");
                dialog.show();
            }
        });


        /*mDelegation = KeyboardInputDelegation.delegation(this, mLayoutCoordinator, mBannerView);
        mDelegation.setBehavior(new FloatingAutoHideDownBehavior());
        mDelegation.showEmoji(getSupportFragmentManager());
        mDelegation.setAdapter(new KeyboardInputDelegation.KeyboardInputAdapter() {
            @Override
            public void onSubmit(TextView v, String content) {
                // TODO do on submit
            }

            @Override
            public void onFinalBackSpace(View v) {
                // TODO remove @someone
            }
        });*/
    }

    private void handleKeyDel() {
        if (replies == null || replies.size() == 0) return;
        replies.remove(replies.size() - 1);
        if (replies.size() == 0) {
            mViewInput.setHint("发表评论");
            return;
        }
        mViewInput.setHint("回复: @" + replies.get(0).getAuthor().getName());
        if (replies.size() == 2) {
            mViewInput.setHint(mViewInput.getHint() + " @" + replies.get(1).getAuthor()
                    .getName());
        }
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_share:
                // TODO share the topic
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
//                if (!mDelegation.onTurnBack()) return true;
                break;
        }
        return super.onKeyDown(keyCode, event);
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
