package net.oschina.app.improve.search;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.widget.LinearLayout;

import net.oschina.app.R;
import net.oschina.app.bean.SearchList;
import net.oschina.app.fragment.SearchFragment;
import net.oschina.app.improve.base.activities.BaseActivity;
import net.oschina.app.util.TDevice;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 搜索界面
 * Created by thanatos on 16/9/7.
 */

public class SearchActivity extends BaseActivity {

    @Bind(R.id.view_root) LinearLayout mViewRoot;
    @Bind(R.id.layout_tab) TabLayout mLayoutTab;
    @Bind(R.id.view_pager) ViewPager mViewPager;
    @Bind(R.id.view_searcher) SearchView mViewSearch;
    @Bind(R.id.toolbar) Toolbar mToolbar;

    private List<Pair<String, Fragment>> mPagerItems;

    public static void show(Context context){
        Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_search;
    }

    @Override
    protected void initWindow() {
        mPagerItems = new ArrayList<>();

        mPagerItems.add(new Pair<>("软件", instantiate(SearchFragment.class, SearchList.CATALOG_SOFTWARE)));
        mPagerItems.add(new Pair<>("问答", instantiate(SearchFragment.class, SearchList.CATALOG_POST)));
        mPagerItems.add(new Pair<>("博客", instantiate(SearchFragment.class, SearchList.CATALOG_BLOG)));
        mPagerItems.add(new Pair<>("新闻", instantiate(SearchFragment.class, SearchList.CATALOG_NEWS)));
    }

    @Override
    protected void initWidget() {
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportFinish();
            }
        });

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
                if (TextUtils.isEmpty(query)) return false;
                for (Pair<String, Fragment> pair : mPagerItems){
                    ((SearchFragment) pair.second).search(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TDevice.isWifiOpen()) return false;
                if (TextUtils.isEmpty(newText)) return false;
                for (Pair<String, Fragment> pair : mPagerItems){
                    ((SearchFragment) pair.second).search(newText);
                }
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
        mViewPager.setOffscreenPageLimit(4);
        mLayoutTab.setupWithViewPager(mViewPager);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startAnimation();
        }else {
            mViewSearch.setIconified(false);
        }
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

    private Fragment instantiate(Class<? extends Fragment> clazz, String catalog){
        Bundle bundle = new Bundle();
        bundle.putString(SearchFragment.BUNDLE_KEY_SEARCH_CATALOG, catalog);
        return Fragment.instantiate(this, clazz.getName(), bundle);
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
}
