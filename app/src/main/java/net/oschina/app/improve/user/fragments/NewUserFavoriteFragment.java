package net.oschina.app.improve.user.fragments;

import android.os.Bundle;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.cache.CacheManager;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.base.fragments.BaseRecyclerViewFragment;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.detail.activities.BlogDetailActivity;
import net.oschina.app.improve.detail.activities.EventDetailActivity;
import net.oschina.app.improve.detail.activities.NewsDetailActivity;
import net.oschina.app.improve.detail.activities.QuestionDetailActivity;
import net.oschina.app.improve.detail.activities.SoftwareDetailActivity;
import net.oschina.app.improve.detail.activities.TranslateDetailActivity;
import net.oschina.app.improve.user.adapter.UserFavoritesAdapter;
import net.oschina.app.improve.user.bean.UserFavorites;
import net.oschina.app.ui.empty.EmptyLayout;

import java.lang.reflect.Type;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by fei on 2016/8/30.
 * desc:
 */

public class NewUserFavoriteFragment extends BaseRecyclerViewFragment<UserFavorites> {

    public static final String CATALOG_TYPE = "catalog_type";
    private int catalog;

    @Override
    protected void initBundle(Bundle bundle) {
        super.initBundle(bundle);
        catalog = bundle.getInt(CATALOG_TYPE);
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_base_recycler_view;
    }

    @Override
    public void initData() {
        //super.initData();
        mBean = new PageBean<>();
        mAdapter = getRecyclerAdapter();
        mAdapter.setState(BaseRecyclerAdapter.STATE_HIDE, false);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        mErrorLayout.setOnLayoutClickListener(this);
        mRefreshLayout.setSuperRefreshLayoutListener(this);
        mAdapter.setState(BaseRecyclerAdapter.STATE_HIDE, false);
        mRecyclerView.setLayoutManager(getLayoutManager());
        mRefreshLayout.setColorSchemeResources(
                R.color.swiperefresh_color1, R.color.swiperefresh_color2,
                R.color.swiperefresh_color3, R.color.swiperefresh_color4);


        mHandler = new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                onRequestError(statusCode);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultBean<PageBean<UserFavorites>> resultBean = AppContext.createGson().fromJson(responseString, getType());
                    if (resultBean != null && resultBean.isSuccess() && resultBean.getResult().getItems() != null) {
                        setListData(resultBean);
                        onRequestSuccess(resultBean.getCode());
                    } else {
                        setListData(resultBean);
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
            }
        };

        boolean isNeedEmptyView = isNeedEmptyView();
        if (isNeedEmptyView) {
            mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
            mRefreshLayout.setVisibility(View.GONE);
            AppOperator.runOnThread(new Runnable() {
                @SuppressWarnings("unchecked")
                @Override
                public void run() {
                    mBean = isNeedCache() ? (PageBean<UserFavorites>) CacheManager.readObject(getActivity(), CACHE_NAME) : null;
                    //if is the first loading
                    if (mBean == null) {
                        mBean = new PageBean<>();
                        mBean.setItems(new ArrayList<UserFavorites>());
                        onRefreshing();
                    } else {
                        mRoot.post(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.addAll(mBean.getItems());
                                mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                                mRefreshLayout.setVisibility(View.VISIBLE);
                                mRefreshLayout.setRefreshing(true);
                                onRefreshing();
                            }
                        });
                    }
                }
            });
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
    protected void requestData() {
        super.requestData();
        switch (catalog) {
            case 0:
                CACHE_NAME = "user_favorite_all";
                break;
            case 1:
                CACHE_NAME = "user_favorite_software";
                break;
            case 2:
                CACHE_NAME = "user_favorite_question";
                break;
            case 3:
                CACHE_NAME = "user_favorite_blog";
                break;
            case 4:
                CACHE_NAME = "user_favorite_traslation";
                break;
            case 5:
                CACHE_NAME = "user_favorite_event";
                break;
            case 6:
                CACHE_NAME = "user_favorite_news";
                break;
            default:
                break;
        }
        OSChinaApi.getUserFavorites(catalog, mIsRefresh ? null : mBean.getNextPageToken(), mHandler);
    }


    @Override
    protected BaseRecyclerAdapter<UserFavorites> getRecyclerAdapter() {
        return new UserFavoritesAdapter(getActivity(), BaseRecyclerAdapter.ONLY_FOOTER);
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<UserFavorites>>>() {
        }.getType();
    }

    @Override
    public void onItemClick(int position, long itemId) {
        super.onItemClick(position, itemId);
        UserFavorites item = mAdapter.getItem(position);
        if (item == null) return;
        int type = item.getType();
        switch (type) {
            case 1:
                SoftwareDetailActivity.show(getActivity(), item.getId());
                break;
            case 2:
                QuestionDetailActivity.show(getActivity(), item.getId());
                break;
            case 3:
                BlogDetailActivity.show(getActivity(), item.getId());
                break;
            case 4:
                TranslateDetailActivity.show(getActivity(), item.getId());
                break;
            case 5:
                EventDetailActivity.show(getActivity(), item.getId());
                break;
            case 6:
                NewsDetailActivity.show(getActivity(), item.getId());
                break;
            default:
                break;
        }

    }
}
