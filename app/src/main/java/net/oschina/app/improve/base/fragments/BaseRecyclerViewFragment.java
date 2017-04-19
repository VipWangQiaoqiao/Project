package net.oschina.app.improve.base.fragments;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppConfig;
import net.oschina.app.R;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.base.adapter.BaseGeneralRecyclerAdapter;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.utils.CacheManager;
import net.oschina.app.improve.widget.RecyclerRefreshLayout;
import net.oschina.app.improve.widget.SimplexToast;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.TLog;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;


/**
 * 基本列表类，重写getLayoutId()自定义界面
 * Created by huanghaibin_dev
 * on 2016/4/12.
 */
public abstract class BaseRecyclerViewFragment<T> extends BaseFragment implements
        RecyclerRefreshLayout.SuperRefreshLayoutListener,
        BaseRecyclerAdapter.OnItemClickListener,
        View.OnClickListener,
        BaseGeneralRecyclerAdapter.Callback {
    private final String TAG = this.getClass().getSimpleName();
    protected BaseRecyclerAdapter<T> mAdapter;
    protected RecyclerView mRecyclerView;
    protected RecyclerRefreshLayout mRefreshLayout;
    protected boolean isRefreshing;
    protected TextHttpResponseHandler mHandler;
    protected PageBean<T> mBean;
    protected String CACHE_NAME = getClass().getName();
    protected EmptyLayout mErrorLayout;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_base_recycler_view;
    }

    @Override
    protected void initWidget(View root) {
        mRecyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        mRefreshLayout = (RecyclerRefreshLayout) root.findViewById(R.id.refreshLayout);
        mErrorLayout = (EmptyLayout) root.findViewById(R.id.error_layout);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initData() {
        mBean = new PageBean<>();
        mAdapter = getRecyclerAdapter();
        mAdapter.setState(BaseRecyclerAdapter.STATE_HIDE, false);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        mErrorLayout.setOnLayoutClickListener(this);
        mRefreshLayout.setSuperRefreshLayoutListener(this);
        mAdapter.setState(BaseRecyclerAdapter.STATE_HIDE, false);
        mRecyclerView.setLayoutManager(getLayoutManager());
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (RecyclerView.SCROLL_STATE_DRAGGING == newState && getActivity() != null
                        && getActivity().getCurrentFocus() != null) {
                    TDevice.hideSoftKeyboard(getActivity().getCurrentFocus());
                }
            }
        });
        mRefreshLayout.setColorSchemeResources(
                R.color.swiperefresh_color1, R.color.swiperefresh_color2,
                R.color.swiperefresh_color3, R.color.swiperefresh_color4);


        mHandler = new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                log("HttpResponseHandler:onStart");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                onRequestError();
                log("HttpResponseHandler:onFailure responseString:" + responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                log("HttpResponseHandler:onSuccess responseString:" + responseString);
                try {
                    ResultBean<PageBean<T>> resultBean = AppOperator.createGson().fromJson(responseString, getType());
                    if (resultBean != null && resultBean.isSuccess() && resultBean.getResult().getItems() != null) {
                        setListData(resultBean);
                        onRequestSuccess(resultBean.getCode());
                    } else {
                        if (resultBean.getCode() == ResultBean.RESULT_TOKEN_ERROR) {
                            SimplexToast.show(getActivity(), resultBean.getMessage());
                        }
                        mAdapter.setState(BaseRecyclerAdapter.STATE_NO_MORE, true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                onRequestFinish();
                log("HttpResponseHandler:onFinish");
            }

            @Override
            public void onCancel() {
                super.onCancel();
                onRequestFinish();
            }
        };

        boolean isNeedEmptyView = isNeedEmptyView();
        if (isNeedEmptyView) {
            mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
            mRefreshLayout.setVisibility(View.GONE);
            mBean = new PageBean<>();

            List<T> items = isNeedCache()
                    ? (List<T>) CacheManager.readListJson(getActivity(), CACHE_NAME, getCacheClass())
                    : null;

            mBean.setItems(items);
            //if is the first loading
            if (items == null) {
                mBean.setItems(new ArrayList<T>());
                onRefreshing();
            } else {
                mAdapter.addAll(mBean.getItems());
                mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                mRefreshLayout.setVisibility(View.VISIBLE);
                mRoot.post(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.setRefreshing(true);
                        onRefreshing();
                    }
                });
            }
        } else {
            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            mRefreshLayout.setVisibility(View.VISIBLE);
            mRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mRefreshLayout.setRefreshing(true);
                    onRefreshing();
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        onRefreshing();
    }


    @Override
    public void onItemClick(int position, long itemId) {

    }

    @Override
    public void onRefreshing() {
        isRefreshing = true;
        mAdapter.setState(BaseRecyclerAdapter.STATE_HIDE, true);
        requestData();
    }

    @Override
    public void onLoadMore() {
        mAdapter.setState(isRefreshing ? BaseRecyclerAdapter.STATE_HIDE : BaseRecyclerAdapter.STATE_LOADING, true);
        requestData();
    }

    protected void requestData() {
    }

    protected void onRequestSuccess(int code) {

    }

    protected void onRequestFinish() {
        onComplete();
    }

    protected void onRequestError() {
        onComplete();
        if (mAdapter.getItems().size() == 0) {
            if (isNeedEmptyView()) mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            mAdapter.setState(BaseRecyclerAdapter.STATE_LOAD_ERROR, true);
        }
    }

    protected void onComplete() {
        mRefreshLayout.onComplete();
        isRefreshing = false;
    }

    protected void setListData(ResultBean<PageBean<T>> resultBean) {
        mBean.setNextPageToken(resultBean.getResult().getNextPageToken());
        if (isRefreshing) {
            AppConfig.getAppConfig(getActivity()).set("system_time", resultBean.getTime());
            mBean.setItems(resultBean.getResult().getItems());
            mAdapter.clear();
            mAdapter.addAll(mBean.getItems());
            mBean.setPrevPageToken(resultBean.getResult().getPrevPageToken());
            mRefreshLayout.setCanLoadMore(true);
            if (isNeedCache()) {
                CacheManager.saveToJson(getActivity(), CACHE_NAME, mBean.getItems());
            }
        } else {
            mAdapter.addAll(resultBean.getResult().getItems());
        }

        if (resultBean.getResult().getItems() == null
                || resultBean.getResult().getItems().size() < 20)
            mAdapter.setState(BaseRecyclerAdapter.STATE_NO_MORE, true);
//        mAdapter.setState(resultBean.getResult().getItems() == null
//                || resultBean.getResult().getItems().size() < 20
//                ? BaseRecyclerAdapter.STATE_NO_MORE
//                : BaseRecyclerAdapter.STATE_LOADING, true);

        if (mAdapter.getItems().size() > 0) {
            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            mRefreshLayout.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mErrorLayout.setErrorType(
                    isNeedEmptyView()
                            ? EmptyLayout.NODATA
                            : EmptyLayout.HIDE_LAYOUT);
        }
    }

    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    protected abstract BaseRecyclerAdapter<T> getRecyclerAdapter();

    protected abstract Type getType();

    /**
     * 获取缓存bean的class
     */
    protected Class<T> getCacheClass() {
        return null;
    }

    @Override
    public Date getSystemTime() {
        return new Date();
    }

    /**
     * 需要缓存
     *
     * @return isNeedCache
     */
    protected boolean isNeedCache() {
        return true;
    }

    /**
     * 需要空的View
     *
     * @return isNeedEmptyView
     */
    protected boolean isNeedEmptyView() {
        return true;
    }

    @SuppressWarnings("ConstantConditions")
    private void log(String msg) {
        if (false)
            TLog.i(TAG, msg);
    }
}
