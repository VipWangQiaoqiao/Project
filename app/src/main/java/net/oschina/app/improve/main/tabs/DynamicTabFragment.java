package net.oschina.app.improve.main.tabs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.base.fragments.BaseTitleFragment;
import net.oschina.app.improve.bean.SubTab;
import net.oschina.app.improve.main.MainActivity;
import net.oschina.app.improve.main.subscription.SubFragment;
import net.oschina.app.improve.search.activities.SearchActivity;
import net.oschina.app.improve.widget.FragmentPagerAdapter;
import net.oschina.app.improve.widget.TabPickerView;
import net.oschina.app.interf.OnTabReselectListener;
import net.oschina.app.util.TDevice;
import net.oschina.common.utils.StreamUtil;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 动态栏目Fragment
 * Created by thanatosx on 16/10/26.
 */

public class DynamicTabFragment extends BaseTitleFragment implements OnTabReselectListener {

    @Bind(R.id.layout_tab)
    TabLayout mLayoutTab;
    @Bind(R.id.view_tab_picker)
    TabPickerView mViewTabPicker;
    @Bind(R.id.view_pager)
    ViewPager mViewPager;
    @Bind(R.id.iv_arrow_down)
    ImageView mViewArrowDown;

    private MainActivity activity;
    private Fragment mCurFragment;
    private FragmentPagerAdapter mAdapter;
    private static TabPickerView.TabPickerDataManager mTabPickerDataManager;
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

    public static TabPickerView.TabPickerDataManager initTabPickerManager() {
        if (mTabPickerDataManager == null) {
            mTabPickerDataManager = new TabPickerView.TabPickerDataManager() {
                @Override
                public List<SubTab> setupActiveDataSet() {
                    FileReader reader = null;
                    try {
                        File file = AppContext.getInstance().getFileStreamPath("sub_tab_active.json");
                        if (!file.exists()) return null;
                        reader = new FileReader(file);
                        return AppOperator.getGson().fromJson(reader,
                                new TypeToken<ArrayList<SubTab>>() {
                                }.getType());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        StreamUtil.close(reader);
                    }
                    return null;
                }

                @Override
                public List<SubTab> setupOriginalDataSet() {
                    InputStreamReader reader = null;
                    try {
                        reader = new InputStreamReader(
                                AppContext.getInstance().getAssets().open("sub_tab_original.json")
                                , "UTF-8");
                        return AppOperator.getGson().<ArrayList<SubTab>>fromJson(reader,
                                new TypeToken<ArrayList<SubTab>>() {
                                }.getType());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        StreamUtil.close(reader);
                    }
                    return null;
                }

                @Override
                public void restoreActiveDataSet(List<SubTab> mActiveDataSet) {
                    OutputStreamWriter writer = null;
                    try {
                        writer = new OutputStreamWriter(
                                AppContext.getInstance().openFileOutput(
                                        "sub_tab_active.json", Context.MODE_PRIVATE)
                                , "UTF-8");
                        AppOperator.getGson().toJson(mActiveDataSet, writer);
                        AppContext.set("TabsMask", TDevice.getVersionCode());
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        StreamUtil.close(writer);
                    }
                }
            };
        }
        return mTabPickerDataManager;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        mViewTabPicker.setTabPickerManager(initTabPickerManager());
        mViewTabPicker.setOnTabPickingListener(new TabPickerView.OnTabPickingListener() {

            private boolean isChangeIndex = false;

            @Override
            @SuppressWarnings("all")
            public void onSelected(final int position) {
                final int index = mViewPager.getCurrentItem();
                mViewPager.setCurrentItem(position);
                if (position == index) {
                    mAdapter.commitUpdate();
                    // notifyDataSetChanged为什么会导致TabLayout位置偏移，而且需要延迟设置才能起效？？？
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mLayoutTab.getTabAt(position).select();
                        }
                    }, 50);
                }
            }

            @Override
            public void onRemove(int position, SubTab tab) {
                isChangeIndex = true;
            }

            @Override
            public void onInsert(SubTab tab) {
                isChangeIndex = true;
            }

            @Override
            public void onMove(int op, int np) {
                isChangeIndex = true;
            }

            @Override
            public void onRestore(final List<SubTab> mActiveDataSet) {
                if (!isChangeIndex) return;
                AppOperator.getExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        OutputStreamWriter writer = null;
                        try {
                            writer = new OutputStreamWriter(
                                    AppContext.getInstance().openFileOutput(
                                            "sub_tab_active.json", Context.MODE_PRIVATE)
                                    , "UTF-8");
                            AppOperator.getGson().toJson(mActiveDataSet, writer);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            StreamUtil.close(writer);
                        }

                        /*String json = AppOperator.getGson().toJson(mActiveDataSet);
                        FileOutputStream fos = null;
                        try {
                            fos = AppContext.getInstance().openFileOutput("sub_tab_active.json",
                                    Context.MODE_PRIVATE);
                            fos.write(json.getBytes("UTF-8"));
                            fos.flush();
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            StreamUtil.close(fos);
                        }*/
                    }
                });
                isChangeIndex = false;
                tabs.clear();
                tabs.addAll(mActiveDataSet);
                mAdapter.notifyDataSetChanged();
            }
        });

        mViewTabPicker.setOnShowAnimation(new TabPickerView.Action1<ViewPropertyAnimator>() {
            @Override
            public void call(ViewPropertyAnimator animator) {
                mViewArrowDown.setEnabled(false);
                activity.toggleNavTabView(false);
                mViewArrowDown.animate()
                        .rotation(225)
                        .setDuration(380)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animator) {
                                super.onAnimationEnd(animator);
                                mViewArrowDown.setRotation(45);
                                mViewArrowDown.setEnabled(true);
                            }
                        }).start();

            }
        });

        mViewTabPicker.setOnHideAnimator(new TabPickerView.Action1<ViewPropertyAnimator>() {
            @Override
            public void call(ViewPropertyAnimator animator) {
                mViewArrowDown.setEnabled(false);
                activity.toggleNavTabView(true);
                mViewArrowDown.animate()
                        .rotation(-180)
                        .setDuration(380)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animator) {
                                super.onAnimationEnd(animator);
                                mViewArrowDown.setRotation(0);
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

        mViewPager.setAdapter(mAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return SubFragment.newInstance(getContext(), tabs.get(position));
            }

            @Override
            public int getCount() {
                return tabs.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return tabs.get(position).getName();
            }

            @Override
            public void setPrimaryItem(ViewGroup container, int position, Object object) {
                super.setPrimaryItem(container, position, object);
                if (mCurFragment == null) {
                    commitUpdate();
                }
                mCurFragment = (Fragment) object;
            }

            //this is called when notifyDataSetChanged() is called
            @Override
            public int getItemPosition(Object object) {
                return PagerAdapter.POSITION_NONE;
            }

        });
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    mAdapter.commitUpdate();
                }
            }
        });
        mLayoutTab.setupWithViewPager(mViewPager);
        mLayoutTab.setSmoothScrollingEnabled(true);
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
        if (mViewArrowDown.getRotation() != 0) {
            mViewTabPicker.onTurnBack();
        } else {
            mViewTabPicker.show(mLayoutTab.getSelectedTabPosition());
        }
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


    @Override
    public void onTabReselect() {
        if (mCurFragment != null && mCurFragment instanceof OnTabReselectListener) {
            ((OnTabReselectListener) mCurFragment).onTabReselect();
        }
    }
}
