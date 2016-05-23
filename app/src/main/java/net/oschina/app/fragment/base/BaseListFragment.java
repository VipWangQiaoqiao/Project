package net.oschina.app.fragment.base;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import net.oschina.app.R;
import net.oschina.app.adapter.BaseListAdapter;
import net.oschina.app.widget.SuperRefreshLayout;

/**
 * Created by huanghaibin
 * on 16-5-23.
 */
public abstract class BaseListFragment<T> extends BaseFragment implements
        SuperRefreshLayout.SuperRefreshLayoutListener, AdapterView.OnItemClickListener {

    protected ListView mListView;
    protected SuperRefreshLayout mRefreshLayout;
    protected BaseListAdapter<T> mAdapter;

    protected boolean mIsRefresh;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_base_list;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mListView = (ListView) root.findViewById(R.id.listView);
        mRefreshLayout = (SuperRefreshLayout) root.findViewById(R.id.superRefreshLayout);
        mAdapter = getListAdapter();
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    public void onRefreshing() {
        mIsRefresh = true;
        requestData();
    }

    @Override
    public void onLoadMore() {
        requestData();
    }

    protected void requestData() {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        T item = mAdapter.getItem(position);
        if (item != null)
            onItemClick(item, position);
    }

    protected void onItemClick(T item, int position) {

    }

    protected abstract BaseListAdapter<T> getListAdapter();
}
