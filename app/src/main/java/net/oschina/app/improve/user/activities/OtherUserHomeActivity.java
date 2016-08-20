package net.oschina.app.improve.user.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.base.activities.BaseActivity;
import net.oschina.app.improve.bean.User;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.simple.UserRelation;
import net.oschina.app.improve.user.fragments.UserActiveFragment;
import net.oschina.app.improve.user.fragments.UserBlogFragment;
import net.oschina.app.improve.user.fragments.UserQuestionFragment;
import net.oschina.app.improve.user.fragments.UserTweetFragment;
import net.oschina.app.improve.widget.SolarSystemView;
import net.oschina.app.util.DialogHelp;
import net.oschina.app.util.UIHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 别的用户的主页
 * Created by thanatos on 16/7/13.
 */
public class OtherUserHomeActivity extends BaseActivity implements View.OnClickListener {

    public static final String KEY_BUNDLE = "KEY_BUNDLE_IN_OTHER_USER_HOME";

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.iv_portrait)
    CircleImageView mPortrait;
    @Bind(R.id.tv_nick)
    TextView mNick;
    @Bind(R.id.tv_summary)
    TextView mSummary;
    @Bind(R.id.tv_score)
    TextView mScore;
    @Bind(R.id.tv_count_follow)
    TextView mCountFollow;
    @Bind(R.id.tv_count_fans)
    TextView mCountFans;
    @Bind(R.id.view_solar_system)
    SolarSystemView mSolarSystem;
    @Bind(R.id.layout_appbar)
    AppBarLayout mLayoutAppBar;
    @Bind(R.id.iv_logo_portrait)
    CircleImageView mLogoPortrait;
    @Bind(R.id.tv_logo_nick)
    TextView mLogoNick;
    @Bind(R.id.iv_gender)
    ImageView mGenderImage;
    @Bind(R.id.layout_tab)
    TabLayout mTabLayout;
    @Bind(R.id.view_pager)
    ViewPager mViewPager;
    @Bind(R.id.view_divider)
    View mDivider;

    private MenuItem mFollowMenu;
    private User user;
    private List<Pair<String, Fragment>> fragments;

    public static void show(Context context, User user) {
        if (user == null) return;
        Intent intent = new Intent(context, OtherUserHomeActivity.class);
        intent.putExtra(KEY_BUNDLE, user);
        context.startActivity(intent);
    }

    public static void show(Context context, long id) {
        if (id <= 0) return;
        User user = new User();
        user.setId((int) id);
        show(context, user);
    }

    public static void show(Context context, String nick){
        if (TextUtils.isEmpty(nick)) return;
        User user = new User();
        user.setName(nick);
        show(context, user);

    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        user = (User) bundle.getSerializable(KEY_BUNDLE);
        if (user == null) return false;
        if (user.getId() <= 0 && TextUtils.isEmpty(user.getName())) return false;
        return super.initBundle(bundle);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_other_user_home;
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

        mLayoutAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int mScrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (mScrollRange == -1) {
                    mScrollRange = appBarLayout.getTotalScrollRange();
                }
                if (mScrollRange + verticalOffset == 0) {
                    mLogoNick.setVisibility(View.VISIBLE);
                    mLogoPortrait.setVisibility(View.VISIBLE);
                    mDivider.setVisibility(View.GONE);
                    isShow = true;
                } else if (isShow) {
                    mLogoNick.setVisibility(View.GONE);
                    mLogoPortrait.setVisibility(View.GONE);
                    mDivider.setVisibility(View.VISIBLE);
                    isShow = false;
                }
                mTabLayout.getBackground().setAlpha(Math.round(255 - Math.abs(verticalOffset) / (float) mScrollRange * 255));
            }
        });

        mPortrait.post(new Runnable() {
            @Override
            public void run() {
                int mStatusBarHeight = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Rect rectangle = new Rect();
                    Window window = getWindow();
                    window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
                    mStatusBarHeight = rectangle.top;
                }
                float x = mPortrait.getX();
                float y = mPortrait.getY();
                ViewGroup parent = (ViewGroup) mPortrait.getParent();
                float px = x + parent.getX() + mPortrait.getWidth() / 2;
                float py = y + parent.getY() + mPortrait.getHeight() / 2 + mStatusBarHeight;
                int mMaxRadius = (int) (mSolarSystem.getHeight() - py + 50);

                int r = mPortrait.getWidth() / 2;
                Random random = new Random(System.currentTimeMillis());
                for (int i = 50, radius = r + i; ; i = (int) (i * 1.4), radius += i){
                    SolarSystemView.Planet planet = new SolarSystemView.Planet();
                    planet.setClockwise(random.nextInt(10) % 2 == 0);
                    planet.setAngleRate(random.nextInt(35) / 1000.f);
                    planet.setRadius(radius);
                    mSolarSystem.addPlanets(planet);
                    if (radius > mMaxRadius) break;
                }
                mSolarSystem.setPivotPoint(px, py);
            }
        });

        mTabLayout.addTab(mTabLayout.newTab().setCustomView(getTabView("0", "动弹")));
        mTabLayout.addTab(mTabLayout.newTab().setCustomView(getTabView("0", "博客")));
        mTabLayout.addTab(mTabLayout.newTab().setCustomView(getTabView("0", "问答")));
        mTabLayout.addTab(mTabLayout.newTab().setCustomView(getTabView("0", "动态")));
        injectDataToView();
        injectDataToViewPager();
    }

    @SuppressWarnings("all")
    private void injectDataToViewPager(){
        if (user.getId() <= 0) return;
        if (fragments == null){
            fragments = new ArrayList<>();
            fragments.add(new Pair<>(String.format("%s\n动弹", user.getTweetCount()), UserTweetFragment.instantiate(user.getId())));
            fragments.add(new Pair<>(String.format("%s\n博客", user.getBlogCount()), UserBlogFragment.instantiate(user.getId())));
            fragments.add(new Pair<>(String.format("%s\n问答", user.getAnswerCount()), UserQuestionFragment.instantiate((int) user.getId())));
            fragments.add(new Pair<>(String.format("%s\n动态", user.getDiscussCount()), UserActiveFragment.instantiate(user.getId())));

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

            mTabLayout.setupWithViewPager(mViewPager);
            // TabLayout will remove up all tabs after setted up view pager
            // so we set it again
            mTabLayout.getTabAt(0).setCustomView(getTabView(String.valueOf(user.getTweetCount()), "动弹"));
            mTabLayout.getTabAt(1).setCustomView(getTabView(String.valueOf(user.getBlogCount()), "博客"));
            mTabLayout.getTabAt(2).setCustomView(getTabView(String.valueOf(user.getAnswerCount()), "问答"));
            mTabLayout.getTabAt(3).setCustomView(getTabView(String.valueOf(user.getDiscussCount()), "动态"));
        }else {
            setupTabText(mTabLayout.getTabAt(0), String.valueOf(user.getTweetCount()));
            setupTabText(mTabLayout.getTabAt(1), String.valueOf(user.getBlogCount()));
            setupTabText(mTabLayout.getTabAt(2), String.valueOf(user.getAnswerCount()));
            setupTabText(mTabLayout.getTabAt(3), String.valueOf(user.getDiscussCount()));
        }
    }

    private void setupTabText(TabLayout.Tab tab, String str){
        View view = tab.getCustomView();
        if (view == null) return;
        TabViewHolder holder = (TabViewHolder) view.getTag();
        holder.mViewCount.setText(str);
    }

    private View getTabView(String cs, String tag){
        View view = LayoutInflater.from(this).inflate(R.layout.tab_item_other_user, mTabLayout, false);
        TabViewHolder holder = new TabViewHolder(view);
        holder.mViewCount.setText(cs);
        holder.mViewTag.setText(tag);
        view.setTag(holder);
        return view;
    }

    private void injectDataToView() {
        getImageLoader()
                .load(user.getPortrait())
                .asBitmap()
                .placeholder(R.mipmap.widget_dface)
                .error(R.mipmap.widget_dface)
                .into(mPortrait);
        getImageLoader()
                .load(user.getPortrait())
                .asBitmap()
                .placeholder(R.mipmap.widget_dface)
                .error(R.mipmap.widget_dface)
                .into(mLogoPortrait);
        mLogoNick.setText(user.getName());
        mNick.setText(user.getName());
        // TODO summary
        mScore.setText(String.format("积分 %s", user.getScore()));
        mCountFans.setText(String.format("粉丝 %s", user.getFansCount()));
        mCountFollow.setText(String.format("关注 %s", user.getFollowCount()));

        if (user.getGender() == 1){
            mGenderImage.setImageResource(R.mipmap.ic_male);
        }else if (user.getGender() == 2){
            mGenderImage.setImageResource(R.mipmap.ic_female);
        }else {
            mGenderImage.setVisibility(View.GONE);
        }

        if (mFollowMenu != null) {
            switch (user.getRelation()) {
                case User.RELATION_TYPE_BOTH:
                    mFollowMenu.setIcon(getResources().getDrawable(R.drawable.selector_user_following_botn));
                    break;
                case User.RELATION_TYPE_ONLY_FANS_HIM:
                    mFollowMenu.setIcon(getResources().getDrawable(R.drawable.selector_user_following));
                    break;
                case User.RELATION_TYPE_ONLY_FANS_ME:
                    mFollowMenu.setIcon(getResources().getDrawable(R.drawable.selector_user_follow));
                    break;
                case User.RELATION_TYPE_NULL:
                    mFollowMenu.setIcon(getResources().getDrawable(R.drawable.selector_user_follow));
                    break;
                default:
                    mFollowMenu.setIcon(getResources().getDrawable(R.drawable.selector_user_follow));
            }
        }
    }

    @Override
    protected void initData() {
        super.initData();
        OSChinaApi.getUserInfo(user.getId(), user.getName(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(OtherUserHomeActivity.this, "获取用户信息失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                ResultBean<User> result = AppContext.createGson().fromJson(
                        responseString, new TypeToken<ResultBean<User>>(){}.getType());
                if (result.isSuccess() && result.getResult() == null) return;
                user = result.getResult();
                injectDataToView();
                injectDataToViewPager();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO make the user bean same
        net.oschina.app.bean.User mLoginUser = AppContext.getInstance().getLoginUser();
        if (user.getId() == mLoginUser.getId() || user.getName().equals(mLoginUser.getName()))
            return false;
        getMenuInflater().inflate(R.menu.menu_other_user, menu);
        mFollowMenu = menu.getItem(1);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_count_follow:
                UIHelper.showFriends(this, (int) user.getId(), 0);
                break;
            case R.id.tv_count_fans:
                UIHelper.showFriends(this, (int) user.getId(), 1);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_pm:
                if (user.getId() == AppContext.getInstance().getLoginUid()) {
                    AppContext.showToast("不能给自己发送留言:)");
                    return true;
                }
                if (!AppContext.getInstance().isLogin()) {
                    UIHelper.showLoginActivity(this);
                    return true;
                }
                UIHelper.showMessageDetail(this, (int) user.getId(), user.getName());
                break;
            case R.id.menu_follow:
                // 判断登录
                final AppContext ac = AppContext.getInstance();
                if (!AppContext.getInstance().isLogin()) {
                    UIHelper.showLoginActivity(this);
                    return true;
                }
                String mDialogTitle = "";
                switch (user.getRelation()) {
                    case User.RELATION_TYPE_BOTH:
                        mDialogTitle = "确定取消互粉吗？";
                        break;
                    case User.RELATION_TYPE_ONLY_FANS_HIM:
                        mDialogTitle = "确定取消关注吗？";
                        break;
                    case User.RELATION_TYPE_ONLY_FANS_ME:
                        mDialogTitle = "确定关注Ta吗？";
                        break;
                    case User.RELATION_TYPE_NULL:
                        mDialogTitle = "确定关注Ta吗？";
                        break;
                }
                DialogHelp.getConfirmDialog(this, mDialogTitle, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        OSChinaApi.addUserRelationReverse(user.getId(), new TextHttpResponseHandler() {
                            @Override
                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                Toast.makeText(OtherUserHomeActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                                net.oschina.app.improve.bean.base.ResultBean<UserRelation> result = AppContext.createGson().fromJson(
                                        responseString, new TypeToken<net.oschina.app.improve.bean.base.ResultBean<UserRelation>>(){}.getType());
                                if (result.isSuccess()){
                                    int relation = result.getResult().getRelation();
                                    switch (relation) {
                                        case User.RELATION_TYPE_BOTH:
                                            item.setIcon(getResources().getDrawable(R.drawable.selector_user_following_botn));
                                            break;
                                        case User.RELATION_TYPE_ONLY_FANS_HIM:
                                            item.setIcon(getResources().getDrawable(R.drawable.selector_user_following));
                                            break;
                                        case User.RELATION_TYPE_ONLY_FANS_ME:
                                            item.setIcon(getResources().getDrawable(R.drawable.selector_user_follow));
                                            break;
                                        case User.RELATION_TYPE_NULL:
                                            item.setIcon(getResources().getDrawable(R.drawable.selector_user_follow));
                                            break;
                                    }
                                    user.setRelation(relation);
                                }else{
                                    onFailure(statusCode, headers, responseString, null);
                                }
                            }
                        });
                    }
                }).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private static class TabViewHolder{
        private TextView mViewCount;
        private TextView mViewTag;

        public TabViewHolder(View view){
            mViewCount = (TextView) view.findViewById(R.id.tv_count);
            mViewTag = (TextView) view.findViewById(R.id.tv_tag);
        }
    }

}
