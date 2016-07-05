package net.oschina.app.improve.activities;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.improve.adapter.base.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.widget.RecyclerRefreshLayout;

import java.lang.reflect.Type;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;

/**
 * Created by huanghaibin_dev
 * on 16-6-23.
 */
@SuppressWarnings("All")
public abstract class BaseRecyclerViewActivity<T> extends BaseBackActivity implements
        BaseRecyclerAdapter.OnItemClickListener, RecyclerRefreshLayout.SuperRefreshLayoutListener {

    @Bind(R.id.refreshLayout)
    RecyclerRefreshLayout mRefreshLayout;

    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;

    protected BaseRecyclerAdapter<T> mAdapter;

    protected TextHttpResponseHandler mHandler;

    protected PageBean<T> mBean;

    protected boolean mIsRefresh;

    @Override
    protected int getContentView() {
        return R.layout.activity_base_recycler;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mAdapter = getRecyclerAdapter();
        mRecyclerView.setLayoutManager(getLayoutManager());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        mRefreshLayout.setRefreshing(true);
        mRefreshLayout.setColorSchemeResources(
                R.color.swiperefresh_color1, R.color.swiperefresh_color2,
                R.color.swiperefresh_color3, R.color.swiperefresh_color4);
    }

    @Override
    protected void initData() {
        super.initData();
        mBean = new PageBean<>();
        mHandler = new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                onLoadingFailure();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultBean<PageBean<T>> resultBean = AppContext.createGson().fromJson(responseString, getType());
                    if (resultBean != null && resultBean.isSuccess()) {
                        onLoadingSuccess();
                        setListData(resultBean);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }

            @Override
            public void onStart() {
                super.onStart();
                onLoadingStart();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                onLoadingFinish();
            }
        };

        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
            }
        });
    }

    @Override
    public void onItemClick(int position, long itemId) {
        onItemClick(mAdapter.getItem(position), position);
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

    protected void onItemClick(T item, int position) {

    }

    protected void requestData() {

    }

    protected void setListData(ResultBean<PageBean<T>> resultBean) {
        mBean.setNextPageToken(resultBean.getResult().getNextPageToken());
        if (mIsRefresh) {
            mBean.setItems(resultBean.getResult().getItems());
            mAdapter.clear();
            mAdapter.addAll(mBean.getItems());
            mBean.setPrevPageToken(resultBean.getResult().getPrevPageToken());
            mRefreshLayout.setCanLoadMore(true);

        } else {
            mAdapter.addAll(resultBean.getResult().getItems());
        }
        if (resultBean.getResult().getItems().size() < 20) {
            mAdapter.setState(BaseRecyclerAdapter.STATE_NO_MORE, false);
        }
    }

    protected void onLoadingStart(){

    }

    protected void onLoadingSuccess(){

    }

    protected void onLoadingFinish(){
        mRefreshLayout.onComplete();
        mIsRefresh = false;
    }

    protected void onLoadingFailure(){

    }


    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(this);
    }

    protected abstract Type getType();

    protected abstract BaseRecyclerAdapter<T> getRecyclerAdapter();
}
