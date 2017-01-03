package net.oschina.app.improve.base.fragments;

import net.oschina.app.interf.OnTabReselectListener;

/**
 * Created by JuQiu
 * on 16/6/6.
 */

public abstract class BaseGeneralListFragment<T> extends BaseListFragment<T> implements OnTabReselectListener {
    @Override
    public void onTabReselect() {
        if (mListView != null) {
            mListView.setSelection(0);
            mRefreshLayout.setRefreshing(true);
            onRefreshing();
        }
    }
}
