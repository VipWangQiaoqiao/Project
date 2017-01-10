package net.oschina.app.improve.search.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseActivity;
import net.oschina.app.improve.bean.News;
import net.oschina.app.improve.search.fragments.SearchArticleFragment;
import net.oschina.app.improve.search.fragments.SearchUserFragment;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 搜索界面
 * Created by thanatos on 16/9/7.
 */

public class SearchActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    @Bind(R.id.view_root)
    LinearLayout mViewRoot;
    @Bind(R.id.layout_tab)
    TabLayout mLayoutTab;
    @Bind(R.id.view_pager)
    ViewPager mViewPager;
    @Bind(R.id.view_searcher)
    SearchView mViewSearch;
    @Bind(R.id.search_mag_icon)
    ImageView mSearchIcon;
    @Bind(R.id.search_edit_frame)
    LinearLayout mLayoutEditFrame;
    @Bind(R.id.search_src_text)
    EditText mViewSearchEditor;

    private static boolean isMiUi = false;
    private List<Pair<String, Fragment>> mPagerItems;
    private String mSearchText;
    private Runnable mSearchRunnable = new Runnable() {
        @Override
        public void run() {
            if (TextUtils.isEmpty(mSearchText))
                return;
            SearchAction f = (SearchAction) mPagerItems.get(mViewPager.getCurrentItem()).second;
            f.search(mSearchText);
        }
    };

    public static void show(Context context) {
        Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
    }

    public void setStatusBarDarkMode(boolean darkMode) {
        if (isMiUi) {
            Class<? extends Window> clazz = getWindow().getClass();
            try {
                int darkModeFlag;
                Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                extraFlagField.invoke(getWindow(), darkMode ? darkModeFlag : 0, darkModeFlag);
            } catch (Exception e) {
                // 这个API, MDZZ
                e.printStackTrace();
            }
        }
    }

    /**
     * 静态域，获取系统版本是否基于MIUI
     */
    static {
        try {
            Class<?> sysClass = Class.forName("android.os.SystemProperties");
            Method getStringMethod = sysClass.getDeclaredMethod("get", String.class);
            String version = (String) getStringMethod.invoke(sysClass, "ro.miui.ui.version.name");
            isMiUi = version.compareTo("V6") >= 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // pass
    }

    @Override
    public void onPageSelected(int position) {
        String content = mSearchText;
        if (TextUtils.isEmpty(content)) return;
        doSearch(content, false);
        mViewSearch.clearFocus();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // pass
    }

    public interface SearchAction {
        void search(String content);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_v2_search;
    }

    @Override
    protected void initWindow() {
        mPagerItems = new ArrayList<>();

        mPagerItems.add(new Pair<>("软件", SearchArticleFragment.instantiate(this, News.TYPE_SOFTWARE)));
        mPagerItems.add(new Pair<>("博客", SearchArticleFragment.instantiate(this, News.TYPE_BLOG)));
        mPagerItems.add(new Pair<>("资讯", SearchArticleFragment.instantiate(this, News.TYPE_NEWS)));
        mPagerItems.add(new Pair<>("问答", SearchArticleFragment.instantiate(this, News.TYPE_QUESTION)));
        mPagerItems.add(new Pair<>("找人", SearchUserFragment.instantiate(this)));
    }

    @Override
    protected void onStop() {
        super.onStop();
        mViewSearch.clearFocus();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode(true);
        mViewSearchEditor.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        mViewSearch.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                // 阻止点击关闭按钮 collapse icon
                return true;
            }
        });
        mViewSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mViewSearch.clearFocus();
                return doSearch(query, false);
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return doSearch(newText, true);
            }
        });

        mViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mPagerItems.get(position).second;
            }

            @Override
            public int getCount() {
                return mPagerItems.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mPagerItems.get(position).first;
            }
        });
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setOffscreenPageLimit(0);
        mLayoutTab.setupWithViewPager(mViewPager);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mSearchIcon.getLayoutParams();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        mSearchIcon.setLayoutParams(params);

        LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) mLayoutEditFrame.getLayoutParams();
        params1.setMargins(0, 0, 0, 0);
        mLayoutEditFrame.setLayoutParams(params1);

        mViewSearch.post(new Runnable() {
            @Override
            public void run() {
//                TDevice.showSoftKeyboard(mViewSearch);
                mViewSearch.setIconified(false);
            }
        });
    }


    private boolean doSearch(String query, boolean fromTextChange) {
        mSearchText = query.trim();
        // Always cancel all request
        mViewPager.removeCallbacks(mSearchRunnable);
        // Search is'nt empty
        if (TextUtils.isEmpty(mSearchText)) {
            mLayoutTab.setVisibility(View.GONE);
            mViewPager.setVisibility(View.GONE);
            return false;
        }

        mLayoutTab.setVisibility(View.VISIBLE);
        mViewPager.setVisibility(View.VISIBLE);

        // In this we delay 1 seconds
        mViewPager.postDelayed(mSearchRunnable, fromTextChange ? 1000 : 0);
        return true;
    }

    @OnClick(R.id.tv_cancel)
    void onClickCancel() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
