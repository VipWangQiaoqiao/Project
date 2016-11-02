package net.oschina.app.improve.main.tabs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.oschina.app.R;
import net.oschina.app.improve.base.fragments.BaseTitleFragment;
import net.oschina.app.improve.bean.News;
import net.oschina.app.improve.bean.SubTab;
import net.oschina.app.improve.general.fragments.BlogFragment;
import net.oschina.app.improve.general.fragments.EventFragment;
import net.oschina.app.improve.general.fragments.NewsFragment;
import net.oschina.app.improve.general.fragments.QuestionFragment;
import net.oschina.app.improve.main.MainActivity;
import net.oschina.app.improve.search.activities.SearchActivity;
import net.oschina.app.improve.widget.TabPickerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 动态栏目Fragment
 * Created by thanatosx on 16/10/26.
 */

public class DynamicTabFragment extends BaseTitleFragment {

    @Bind(R.id.layout_tab)
    TabLayout mLayoutTab;
    @Bind(R.id.view_tab_picker)
    TabPickerView mViewTabPicker;
    @Bind(R.id.view_pager)
    ViewPager mViewPager;
    @Bind(R.id.iv_arrow_down)
    ImageView mViewArrowDown;

    private MainActivity activity;
    List<SubTab> tabs;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
        activity.addOnTurnBackListener(new MainActivity.TurnBackListener() {
            @Override
            public boolean onTurnBack() {
                return mViewTabPicker != null && mViewTabPicker.onTurnBack();
            }
        });
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        mViewTabPicker.setTabPickerManager(new TabPickerView.TabPickerDataManager() {
            @Override
            public List<SubTab> setupActiveDataSet() {
                try {
                    StringBuilder buffer = new StringBuilder();
                    byte[] bytes = new byte[1024];
                    int length;

                    File file = getContext().getFileStreamPath("sub_tab_active.json");
                    if (file.exists()) {
                        FileInputStream fis = getContext().openFileInput("sub_tab_active.json");
                        while ((length = fis.read(bytes)) != -1) {
                            buffer.append(new String(bytes, 0, length));
                        }
                    }

                    if (TextUtils.isEmpty(buffer.toString()) || buffer.toString().trim().equals("")) {
                        InputStream is = getResources().getAssets().open("sub_tab_active.json");
                        while ((length = is.read(bytes)) != -1) {
                            buffer.append(new String(bytes, 0, length));
                        }
                    }
                    return new Gson().<ArrayList<SubTab>>fromJson(buffer.toString(),
                            new TypeToken<ArrayList<SubTab>>() {
                            }.getType());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public List<SubTab> setupOriginalDataSet() {
                try {
                    InputStream is = getResources().getAssets().open("sub_tab_original.json");
                    StringBuilder buffer = new StringBuilder();
                    byte[] bytes = new byte[1024];
                    int length;
                    while ((length = is.read(bytes)) != -1) {
                        buffer.append(new String(bytes, 0, length));
                    }
                    return new Gson().<ArrayList<SubTab>>fromJson(buffer.toString(),
                            new TypeToken<ArrayList<SubTab>>() {
                            }.getType());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });

        mViewTabPicker.setOnTabPickingListener(new TabPickerView.OnTabPickingListener() {

            private boolean isChangeIndex = false;

            @Override
            @SuppressWarnings("all")
            public void onSelected(final int position) {
                mViewPager.setCurrentItem(position);
            }

            @Override
            public void onRemove(int position, SubTab tab) {
                isChangeIndex = true;
                tabs.remove(position);
                mViewPager.getAdapter().notifyDataSetChanged();
//                mLayoutTab.removeTabAt(position);
            }

            @Override
            public void onInsert(SubTab tab) {
                isChangeIndex = true;
                mLayoutTab.addTab(mLayoutTab.newTab().setText(tab.getName()));
                tabs.add(tab);
                mViewPager.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onMove(int op, SubTab mover, int np, SubTab swapper) {
                isChangeIndex = true;
                if (op < np) {
                    for (int i = op; i < np; i++) {
                        Collections.swap(tabs, i, i + 1);
                    }
                } else {
                    for (int i = op; i > np; i--) {
                        Collections.swap(tabs, i, i - 1);
                    }
                }
                mViewPager.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onRestore(List<SubTab> activeTabs) {
                if (isChangeIndex) {
                    String json = new Gson().toJson(activeTabs);
                    try {
                        FileOutputStream fos = getContext().openFileOutput("sub_tab_active.json",
                                Context.MODE_PRIVATE);
                        fos.write(json.getBytes("UTF-8"));
                        fos.flush();
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    isChangeIndex = false;
                }
            }
        });

        mViewTabPicker.setOnShowAnimation(new TabPickerView.Action1<ViewPropertyAnimator>() {
            @Override
            public void call(ViewPropertyAnimator animator) {
                mViewArrowDown.setEnabled(false);

                mViewTabPicker.setVisibility(View.VISIBLE);
                mViewTabPicker.setTranslationY(mViewTabPicker.getHeight() * 0.2f);
                mViewTabPicker.setAlpha(0);
                activity.toggleNavTabView(false);
                animator.translationY(0)
                        .alpha(1)
                        .setDuration(380)
                        .setInterpolator(new DecelerateInterpolator())
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                mViewTabPicker.setVisibility(View.VISIBLE);
                                mViewTabPicker.setTranslationY(0);
                                mViewTabPicker.setAlpha(1);
                            }
                        });
            }
        });

        mViewTabPicker.setOnHideAnimator(new TabPickerView.Action1<ViewPropertyAnimator>() {
            @Override
            public void call(ViewPropertyAnimator animator) {
                activity.toggleNavTabView(true);
                animator.translationY(mViewTabPicker.getHeight() * 0.2f)
                        .setDuration(380)
                        .alpha(0)
                        .setInterpolator(new DecelerateInterpolator())
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                mViewTabPicker.setVisibility(View.GONE);
                                mViewArrowDown.setEnabled(true);
                            }
                        });
            }
        });

        tabs = new ArrayList<>();
        tabs.addAll(mViewTabPicker.getTabPickerManager().getActiveDataSet());
        for (SubTab tab : tabs) {
            mLayoutTab.addTab(mLayoutTab.newTab().setText(tab.getName()));
        }

        mViewPager.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                SubTab tab = tabs.get(position);
                return instanceFragment(tab.getType(), tab.getSubtype());
            }

            @Override
            public int getCount() {
                return tabs.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return tabs.get(position).getName();
            }

            //this is called when notifyDataSetChanged() is called
            @Override
            public int getItemPosition(Object object) {
                return PagerAdapter.POSITION_NONE;
            }

        });
        mViewPager.setOffscreenPageLimit(1);
        mLayoutTab.setupWithViewPager(mViewPager);
    }

    public Fragment instanceFragment(int type, int subtype) {
        switch (type) {
            case News.TYPE_NEWS:
                return new NewsFragment();
            case News.TYPE_EVENT:
                return new EventFragment();
            case News.TYPE_QUESTION:
                return QuestionFragment.instantiate(getContext(), subtype);
            case News.TYPE_BLOG:
                return BlogFragment.instantiate(getContext(), subtype);
        }
        throw new RuntimeException("Fuck you!!!!!");
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_dynamic_tab;
    }

    @Override
    protected int getTitleRes() {
        return R.string.main_tab_name_news;
    }

    @OnClick(R.id.iv_arrow_down)
    void onClickArrow() {
        mViewTabPicker.show(mLayoutTab.getSelectedTabPosition());
    }

    @Override
    protected int getIconRes() {
        return R.mipmap.btn_search_normal;
    }

    @Override
    protected View.OnClickListener getIconClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchActivity.show(getContext());
            }
        };
    }


}
