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
import net.oschina.app.bean.SimpleBackPage;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.base.activities.BaseActivity;
import net.oschina.app.improve.bean.User;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.simple.Author;
import net.oschina.app.improve.bean.simple.UserRelation;
import net.oschina.app.improve.media.ImageGalleryActivity;
import net.oschina.app.improve.tweet.fragments.TweetFragment;
import net.oschina.app.improve.user.fragments.UserActiveFragment;
import net.oschina.app.improve.user.fragments.UserBlogFragment;
import net.oschina.app.improve.user.fragments.UserQuestionFragment;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.improve.widget.SolarSystemView;
import net.oschina.app.util.UIHelper;
import net.oschina.app.widget.AvatarView;

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
public class OtherUserHomeActivity extends BaseActivity
        implements View.OnClickListener, DialogInterface.OnClickListener {

    public static final String KEY_BUNDLE = "KEY_BUNDLE_IN_OTHER_USER_HOME";

    /* 谁格式化了我这里的代码我就打谁 */
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

    private User user;
    private MenuItem mFollowMenu;
    private List<Pair<String, Fragment>> fragments;
    private TabLayoutOffsetChangeListener mOffsetChangerListener;

    public static void show(Context context, Author author) {
        if (author == null) return;
        User user = new User();
        user.setId(author.getId());
        user.setName(author.getName());
        user.setPortrait(author.getPortrait());
        show(context, user);
    }

    public static void show(Context context, long id) {
        if (id <= 0) return;
        User user = new User();
        user.setId((int) id);
        show(context, user);
    }

    public static void show(Context context, String nick) {
        if (TextUtils.isEmpty(nick)) return;
        User user = new User();
        user.setName(nick);
        show(context, user);
    }

    /**
     * @param context context
     * @param id      无效值,随便填,只是用来区别{{@link #show(Context, String)}}方法的
     * @param suffix  个性后缀
     */
    public static void show(Context context, long id, String suffix) {
        if (TextUtils.isEmpty(suffix)) return;
        User user = new User();
        user.setSuffix(suffix);
        show(context, user);
    }

    public static void show(Context context, User user) {
        if (user == null) return;
        Intent intent = new Intent(context, OtherUserHomeActivity.class);
        intent.putExtra(KEY_BUNDLE, user);
        context.startActivity(intent);
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        user = (User) bundle.getSerializable(KEY_BUNDLE);
        if (user == null || (user.getId() <= 0 && TextUtils.isEmpty(user.getName())
                && TextUtils.isEmpty(user.getSuffix()))) {
            Toast.makeText(this, "没有此用户", Toast.LENGTH_SHORT).show();
            return false;
        }
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

        mLayoutAppBar.addOnOffsetChangedListener(mOffsetChangerListener = new TabLayoutOffsetChangeListener());

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
                for (int i = 60, radius = r + i; ; i = (int) (i * 1.4), radius += i) {
                    SolarSystemView.Planet planet = new SolarSystemView.Planet();
                    planet.setClockwise(random.nextInt(10) % 2 == 0);
                    planet.setAngleRate((random.nextInt(35) + 1) / 1000.f);
                    planet.setRadius(radius);
                    mSolarSystem.addPlanets(planet);
                    if (radius > mMaxRadius) break;
                }
                mSolarSystem.setPivotPoint(px, py);
                float ry = mSolarSystem.getHeight() - py;
                double rx = Math.pow(px * px + ry * ry, 1.f / 2.f);
                mSolarSystem.setRadialGradient(px, py, (float) rx, 0XFF24CF5F, 0XFF20B955);
            }
        });

        mTabLayout.addTab(mTabLayout.newTab().setCustomView(getTabView("0", "动弹")));
        mTabLayout.addTab(mTabLayout.newTab().setCustomView(getTabView("0", "博客")));
        mTabLayout.addTab(mTabLayout.newTab().setCustomView(getTabView("0", "问答")));
        mTabLayout.addTab(mTabLayout.newTab().setCustomView(getTabView("0", "讨论")));
        injectDataToView();
        injectDataToViewPager();
    }

    @SuppressWarnings("all")
    private void injectDataToViewPager() {
        if (user.getId() <= 0) return;

        int t = 0, b = 0, a = 0, d = 0;
        if (user.getStatistics() != null) {
            t = user.getStatistics().getTweet();
            b = user.getStatistics().getBlog();
            a = user.getStatistics().getAnswer();
            d = user.getStatistics().getDiscuss();
        }

        if (fragments == null) {
            fragments = new ArrayList<>();
            fragments.add(new Pair<>(
                    String.format("%s\n动弹", 0),
                    TweetFragment.instantiate(user.getId(), 0)));
            fragments.add(new Pair<>(
                    String.format("%s\n博客", 0),
                    UserBlogFragment.instantiate(user.getId())));
            fragments.add(new Pair<>(
                    String.format("%s\n问答", 0),
                    UserQuestionFragment.instantiate((int) user.getId())));
            fragments.add(new Pair<>(
                    String.format("%s\n讨论", 0),
                    UserActiveFragment.instantiate(user.getId())));

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
            mTabLayout.getTabAt(0).setCustomView(getTabView(formatNumeric(t), "动弹"));
            mTabLayout.getTabAt(1).setCustomView(getTabView(formatNumeric(b), "博客"));
            mTabLayout.getTabAt(2).setCustomView(getTabView(formatNumeric(a), "问答"));
            mTabLayout.getTabAt(3).setCustomView(getTabView(formatNumeric(d), "讨论"));
        } else { // when request user detail info successfully
            setupTabText(mTabLayout.getTabAt(0), t);
            setupTabText(mTabLayout.getTabAt(1), b);
            setupTabText(mTabLayout.getTabAt(2), a);
            setupTabText(mTabLayout.getTabAt(3), d);
        }
    }

    @SuppressWarnings("all")
    private void setupTabText(TabLayout.Tab tab, int count) {
        View view = tab.getCustomView();
        if (view == null) return;
        TabViewHolder holder = (TabViewHolder) view.getTag();
        holder.mViewCount.setText(formatNumeric(count));
    }

    private String formatNumeric(int count) {
        if (count > 1000) {
            int a = count / 100;
            int b = a % 10;
            int c = a / 10;
            String str;
            if (c <= 9 && b != 0) str = c + "." + b;
            else str = String.valueOf(c);
            return str + "k";
        } else {
            return String.valueOf(count);
        }
    }

    private View getTabView(String cs, String tag) {
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
                .placeholder(R.mipmap.widget_default_face)
                .error(R.mipmap.widget_default_face)
                .into(mPortrait);
        getImageLoader()
                .load(user.getPortrait())
                .asBitmap()
                .placeholder(R.mipmap.widget_default_face)
                .error(R.mipmap.widget_default_face)
                .into(mLogoPortrait);
        mLogoNick.setText(user.getName());
        mNick.setText(user.getName());
        String desc = user.getDesc();
        mSummary.setText(TextUtils.isEmpty(desc) ? "这人很懒,什么都没写" : desc);
        if (user.getStatistics() != null) {
            mScore.setText(String.format("积分 %s", user.getStatistics().getScore()));
            mCountFans.setText(String.format("粉丝 %s", user.getStatistics().getFans()));
            mCountFollow.setText(String.format("关注 %s", user.getStatistics().getFollow()));
        } else {
            mScore.setText("积分 0");
            mCountFans.setText("粉丝 0");
            mCountFollow.setText("关注 0");
        }

        mGenderImage.setVisibility(View.VISIBLE);
        if (user.getGender() == User.GENDER_MALE) {
            mGenderImage.setImageResource(R.mipmap.ic_male);
        } else if (user.getGender() == User.GENDER_FEMALE) {
            mGenderImage.setImageResource(R.mipmap.ic_female);
        } else {
            mGenderImage.setVisibility(View.GONE);
        }

        mOffsetChangerListener.resetRange();
    }

    @Override
    protected void initData() {
        super.initData();
        OSChinaApi.getUserInfo(user.getId(), user.getName(), user.getSuffix(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(OtherUserHomeActivity.this, "获取用户信息失败", Toast.LENGTH_SHORT).show();
            }

            @SuppressWarnings("RestrictedApi")
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                ResultBean<User> result = AppOperator.createGson().fromJson(
                        responseString, new TypeToken<ResultBean<User>>() {
                        }.getType());
                if (result.isSuccess() && result.getResult() == null) return;
                user = result.getResult();
                if (user == null) {
                    Toast.makeText(OtherUserHomeActivity.this, "该用户不存在", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                injectDataToView();
                injectDataToViewPager();
                // after request user successful we could get user id when the static method show passed in user name
                // before which, we hide the menu
                invalidateOptionsMenu();
            }
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (AccountHelper.isLogin() && user.getId() > 0 && AccountHelper.getUserId() != user.getId()) {
            getMenuInflater().inflate(R.menu.menu_other_user, menu);
            mFollowMenu = menu.getItem(1);
            if (mFollowMenu == null) return false;
            switch (user.getRelation()) {
                case User.RELATION_TYPE_BOTH:
                    mFollowMenu.setIcon(getResources().getDrawable(
                            R.drawable.selector_user_following_botn));
                    break;
                case User.RELATION_TYPE_ONLY_FANS_HIM:
                    mFollowMenu.setIcon(getResources().getDrawable(
                            R.drawable.selector_user_following));
                    break;
                case User.RELATION_TYPE_ONLY_FANS_ME:
                    mFollowMenu.setIcon(getResources().getDrawable(
                            R.drawable.selector_user_follow));
                    break;
                case User.RELATION_TYPE_NULL:
                    mFollowMenu.setIcon(getResources().getDrawable(
                            R.drawable.selector_user_follow));
                    break;
                default:
                    mFollowMenu.setIcon(getResources().getDrawable(
                            R.drawable.selector_user_follow));
            }
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (user == null || user.getId() <= 0) return;
        switch (v.getId()) {
            case R.id.tv_count_follow:
                UserFollowsActivity.show(this, user.getId());
                break;
            case R.id.tv_count_fans:
                UserFansActivity.show(this, user.getId());
                break;
            case R.id.view_solar_system:
                Bundle userBundle = new Bundle();
                userBundle.putSerializable("user_info", user);
                UIHelper.showSimpleBack(this, SimpleBackPage.MY_INFORMATION_DETAIL, userBundle);
                break;
            case R.id.iv_portrait:
                String url;
                if (user == null || TextUtils.isEmpty(url = user.getPortrait())) return;
                url = AvatarView.getLargeAvatar(url);
                ImageGalleryActivity.show(this, url);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_pm:
                if (user.getId() == AccountHelper.getUserId()) {
                    AppContext.showToast("不能给自己发送留言:)");
                    return true;
                }
                if (!AccountHelper.isLogin()) {
                    UIHelper.showLoginActivity(this);
                    return true;
                }
                UserSendMessageActivity.show(this, user);
                break;
            case R.id.menu_follow:
                // 判断登录
                if (!AccountHelper.isLogin()) {
                    UIHelper.showLoginActivity(this);
                    return true;
                }
                String mDialogTitle;
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
                    default:
                        return false;
                }
                DialogHelper.getConfirmDialog(this, mDialogTitle, this).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (mFollowMenu == null) return;
        OSChinaApi.addUserRelationReverse(user.getId(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString
                    , Throwable throwable) {
                Toast.makeText(OtherUserHomeActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                ResultBean<UserRelation> result = AppOperator.createGson().fromJson(
                        responseString, new TypeToken<ResultBean<UserRelation>>() {
                        }.getType());
                if (result.isSuccess()) {
                    int relation = result.getResult().getRelation();
                    switch (relation) {
                        case User.RELATION_TYPE_BOTH:
                            mFollowMenu.setIcon(getResources().getDrawable(
                                    R.drawable.selector_user_following_botn));
                            break;
                        case User.RELATION_TYPE_ONLY_FANS_HIM:
                            mFollowMenu.setIcon(getResources().getDrawable(
                                    R.drawable.selector_user_following));
                            break;
                        case User.RELATION_TYPE_ONLY_FANS_ME:
                            mFollowMenu.setIcon(getResources().getDrawable(
                                    R.drawable.selector_user_follow));
                            break;
                        case User.RELATION_TYPE_NULL:
                            mFollowMenu.setIcon(getResources().getDrawable(
                                    R.drawable.selector_user_follow));
                            break;
                    }
                    user.setRelation(relation);
                } else {
                    onFailure(statusCode, headers, responseString, null);
                }
            }
        });
    }

    private static class TabViewHolder {
        private TextView mViewCount;
        private TextView mViewTag;

        public TabViewHolder(View view) {
            mViewCount = (TextView) view.findViewById(R.id.tv_count);
            mViewTag = (TextView) view.findViewById(R.id.tv_tag);
        }
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

        public void resetRange() {
            mScrollRange = -1;
        }
    }

}
