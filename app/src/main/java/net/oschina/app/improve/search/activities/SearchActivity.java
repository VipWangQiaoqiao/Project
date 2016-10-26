package net.oschina.app.improve.search.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseActivity;
import net.oschina.app.improve.bean.News;
import net.oschina.app.improve.search.fragments.SearchArticleFragment;
import net.oschina.app.improve.search.fragments.SearchUserFragment;
import net.oschina.app.util.TDevice;

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

    @Bind(R.id.view_root) LinearLayout mViewRoot;
    @Bind(R.id.layout_tab) TabLayout mLayoutTab;
    @Bind(R.id.view_pager) ViewPager mViewPager;
    @Bind(R.id.view_searcher) SearchView mViewSearch;
    @Bind(R.id.search_mag_icon) ImageView mSearchIcon;
    @Bind(R.id.search_edit_frame) LinearLayout mLayoutEditFrame;
    @Bind(R.id.search_src_text) EditText mViewSearchEditor;

    private List<Pair<String, Fragment>> mPagerItems;
    private QueryTimer mQueryTimer;

    private static boolean isMiUi = false;

    public static void show(Context context){
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
        String content = mViewSearch.getQuery().toString();
        Log.d("oschina", "content: " + content + "");
        if (TextUtils.isEmpty(content)) return;
        doSearch(content);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // pass
    }

    public interface SearchAction{
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
        mPagerItems.add(new Pair<>("问答", SearchArticleFragment.instantiate(this, News.TYPE_QUESTION)));
        mPagerItems.add(new Pair<>("博客", SearchArticleFragment.instantiate(this, News.TYPE_BLOG)));
        mPagerItems.add(new Pair<>("新闻", SearchArticleFragment.instantiate(this, News.TYPE_NEWS)));
        mPagerItems.add(new Pair<>("人", SearchUserFragment.instantiate(this)));

        mQueryTimer = new QueryTimer();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode(true);
        mViewSearchEditor.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        mViewSearch.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                // 阻止点击关闭按钮 collapse icon
                return true;
            }
        });
        mViewSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                if (TextUtils.isEmpty(query)) return false;
                mLayoutTab.setVisibility(View.VISIBLE);
                mViewPager.setVisibility(View.VISIBLE);
//                if (!mQueryTimer.isCancelled()) mQueryTimer.cancel(true);
                mViewPager.post(new Runnable() {
                    @Override
                    public void run() {
                        doSearch(query);
                    }
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                if (TextUtils.isEmpty(newText)) {
                    mLayoutTab.setVisibility(View.GONE);
                    mViewPager.setVisibility(View.GONE);
                    return false;
                };
                if (!TDevice.isWifiOpen()) return false;
                mLayoutTab.setVisibility(View.VISIBLE);
                mViewPager.setVisibility(View.VISIBLE);
//                if (!mQueryTimer.isCancelled()) mQueryTimer.cancel(true);
//                mQueryTimer.execute();
                mViewPager.post(new Runnable() {
                    @Override
                    public void run() {
                        doSearch(newText);
                    }
                });
                return true;
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            startAnimation();
        }else {
            mViewSearch.setIconified(false);
        }
    }

    private void doSearch(String query) {
        SearchAction f = (SearchAction) mPagerItems.get(mViewPager.getCurrentItem()).second;
        f.search(query);
    }

    @OnClick(R.id.tv_cancel) void onClickCancel(){
        supportFinish();
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private void startAnimation(){
        mViewRoot.post(new Runnable() {
            @Override
            public void run() {
                int w = mViewRoot.getWidth();
                int h = mViewRoot.getHeight();
                Animator animator = ViewAnimationUtils.createCircularReveal(
                        mViewRoot, mViewRoot.getWidth(), 0, 0, (float) Math.pow(w * w + h * h, 1.f / 2));
                animator.setDuration(300);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mViewSearch.setIconified(false);
                    }
                });
                animator.start();
            }
        });
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private void endAnimation(){
        int w = mViewRoot.getWidth();
        int h = mViewRoot.getHeight();
        Animator animator = ViewAnimationUtils.createCircularReveal(
                mViewRoot, mViewRoot.getWidth(), 0, (float) Math.pow(w * w + h * h, 1.f / 2), 0);
        animator.setDuration(300);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mViewRoot.setVisibility(View.INVISIBLE); // avoid splash
                finish();
            }

        });
        animator.start();
    }

    @Override
    public void onBackPressed() {
        supportFinish();
    }

    private void supportFinish(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            endAnimation();
        }else {
            finish();
        }
    }

    private class QueryTimer extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.d("oschina", "----------------------");
            try {
                Thread.sleep(1000);
                return true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            if (!bool) return;
            Log.d("oschina", "=============");
            doSearch(mViewSearch.getQuery().toString());
        }
    }
}
