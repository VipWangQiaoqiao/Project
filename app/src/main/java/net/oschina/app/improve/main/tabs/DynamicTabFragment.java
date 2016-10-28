package net.oschina.app.improve.main.tabs;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.oschina.app.R;
import net.oschina.app.improve.base.fragments.BaseFragment;
import net.oschina.app.improve.base.fragments.BaseTitleFragment;
import net.oschina.app.improve.bean.SubTab;
import net.oschina.app.improve.main.subscription.SubFragment;
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
 * Created by thanatosx on 16/10/26.
 */

public class DynamicTabFragment extends BaseTitleFragment {

    @Bind(R.id.layout_tab) TabLayout mLayoutTab;
    @Bind(R.id.view_tab_picker) TabPickerView mViewTabPicker;
    @Bind(R.id.view_pager) ViewPager mViewPager;

    List<Pair<SubTab, Fragment>> fragments;

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
                    if (file.exists()){
                        FileInputStream fis = getContext().openFileInput("sub_tab_active.json");
                        while ((length = fis.read(bytes)) != -1){
                            buffer.append(new String(bytes, 0, length));
                        }
                    }

                    if (TextUtils.isEmpty(buffer.toString()) || buffer.toString().trim().equals("")){
                        InputStream is = getResources().getAssets().open("sub_tab_active.json");
                        while ((length = is.read(bytes)) != -1){
                            buffer.append(new String(bytes, 0, length));
                        }
                    }
                    return new Gson().<ArrayList<SubTab>>fromJson(buffer.toString(),
                            new TypeToken<ArrayList<SubTab>>(){}.getType());
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
                    while ((length = is.read(bytes)) != -1){
                        buffer.append(new String(bytes, 0, length));
                    }
                    return new Gson().<ArrayList<SubTab>>fromJson(buffer.toString(),
                            new TypeToken<ArrayList<SubTab>>(){}.getType());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });

        mViewTabPicker.setOnTabPickingListener(new TabPickerView.OnTabPickingListener() {

            private boolean isChangeIndex = false;
            private boolean isMoveIndex = false;

            @Override
            @SuppressWarnings("all")
            public void onSelected(int position) {
                mLayoutTab.getTabAt(position).select();
            }

            @Override
            public void onRemove(int position, SubTab tab) {
                isChangeIndex = true;
                mLayoutTab.removeTabAt(position);
                fragments.remove(position);
                mViewPager.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onInsert(SubTab tab) {
                isChangeIndex = true;
                mLayoutTab.addTab(mLayoutTab.newTab().setText(tab.getName()));
                fragments.add(Pair.<SubTab, Fragment>create(tab, SubFragment.newInstance(getContext(), tab)));
                mViewPager.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onMove(int op, SubTab mover, int np, SubTab swapper) {
                isChangeIndex = isMoveIndex = true;
                Collections.swap(fragments, op, np);
                mViewPager.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onRestore(List<SubTab> activeTabs) {
                if (isChangeIndex){
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
                if (isMoveIndex){
                    mLayoutTab.removeAllTabs();
                    for (SubTab tab : activeTabs){
                        mLayoutTab.addTab(mLayoutTab.newTab().setText(tab.getName()));
                    }
                }
            }
        });

        List<SubTab> tabs = mViewTabPicker.getTabPickerManager().getActiveDataSet();
        fragments = new ArrayList<>();
        for (SubTab tab : tabs){
            mLayoutTab.addTab(mLayoutTab.newTab().setText(tab.getName()));
            fragments.add(Pair.<SubTab, Fragment>create(tab, SubFragment.newInstance(getContext(), tab)));
        }

        mViewPager.setAdapter(new FragmentStatePagerAdapter(getFragmentManager()) {
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
                return fragments.get(position).first.getName();
            }
        });
        mLayoutTab.setupWithViewPager(mViewPager);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_dynamic_tab;
    }

    @Override
    protected int getTitleRes() {
        return R.string.main_tab_name_news;
    }

    @OnClick(R.id.iv_arrow_down) void onClickArrow(){
        mViewTabPicker.show(mLayoutTab.getSelectedTabPosition());
    }


}
