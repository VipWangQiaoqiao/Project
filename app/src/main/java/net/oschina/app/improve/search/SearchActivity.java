package net.oschina.app.improve.search;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
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
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.widget.LinearLayout;

import net.oschina.app.R;
import net.oschina.app.base.BaseListFragment;
import net.oschina.app.bean.SearchList;
import net.oschina.app.fragment.SearchFragment;
import net.oschina.app.improve.base.activities.BaseActivity;

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

        mViewSearch.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
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

        startAnimation();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
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
                mViewRoot.setVisibility(View.INVISIBLE);
                finish();
            }

        });
        animator.start();
    }

    @Override
    protected void initData() {
        super.initData();
    }

    private Fragment instantiate(Class<? extends Fragment> clazz, String catalog){
        Bundle bundle = new Bundle();
        bundle.putString(BaseListFragment.BUNDLE_KEY_CATALOG, catalog);
        return Fragment.instantiate(this, clazz.getName(), bundle);
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            endAnimation();
        }else {
            finish();
        }
    }
}
