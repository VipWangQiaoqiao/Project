package net.oschina.app.improve.base;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseGeneralRecyclerAdapter;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.base.fragments.BaseFragment;
import net.oschina.app.improve.widget.RecyclerRefreshLayout;

import java.util.Date;
import java.util.List;

/**
 * MVP刷新列表基类，再无逻辑判断，清晰自然
 * Created by haibin
 * on 2016/12/27.
 */

public abstract class BaseRecyclerFragment<Presenter extends BaseListPresenter, Model> extends BaseFragment
        implements BaseListView<Presenter, Model>,
        BaseRecyclerAdapter.OnItemClickListener,
        RecyclerRefreshLayout.SuperRefreshLayoutListener,
        BaseGeneralRecyclerAdapter.Callback {
    protected RecyclerRefreshLayout mRefreshLayout;
    protected RecyclerView mRecyclerView;
    protected BaseRecyclerAdapter<Model> mAdapter;
    protected Presenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_base_recycler;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mRefreshLayout = (RecyclerRefreshLayout) mRoot.findViewById(R.id.refreshLayout);
        mRefreshLayout.setSuperRefreshLayoutListener(this);
        mRecyclerView = (RecyclerView) mRoot.findViewById(R.id.recyclerView);
        mAdapter = getAdapter();
        mRecyclerView.setLayoutManager(getLayoutManager());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        mRefreshLayout.setColorSchemeResources(
                R.color.swiperefresh_color1, R.color.swiperefresh_color2,
                R.color.swiperefresh_color3, R.color.swiperefresh_color4);
    }

    @Override
    protected void initData() {
        super.initData();
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
                mPresenter.onRefreshing();
            }
        });
    }

    @Override
    public void onItemClick(int position, long itemId) {
        Model model = mAdapter.getItem(position);
        if (model != null)
            onItemClick(model, position);
    }

    @Override
    public void onRefreshing() {
        mPresenter.onRefreshing();
    }

    @Override
    public void onLoadMore() {
        mPresenter.onLoadMore();
        mAdapter.setState(BaseRecyclerAdapter.STATE_LOADING, true);
    }

    @Override
    public void onRefreshSuccess(List<Model> data) {
        mAdapter.resetItem(data);
    }

    @Override
    public void onLoadMoreSuccess(List<Model> data) {
        mAdapter.addAll(data);
    }

    @Override
    public void showMoreMore() {
        mAdapter.setState(BaseRecyclerAdapter.STATE_NO_MORE, true);
    }

    @Override
    public void showNetworkError(int strId) {
        mAdapter.setState(BaseRecyclerAdapter.STATE_INVALID_NETWORK, true);
    }

    @Override
    public void onComplete() {
        mRefreshLayout.onComplete();
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.mPresenter = presenter;
    }

    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(mContext);
    }

    @Override
    public Date getSystemTime() {
        return new Date();
    }

    protected abstract BaseRecyclerAdapter<Model> getAdapter();

    protected abstract void onItemClick(Model model, int position);
}
