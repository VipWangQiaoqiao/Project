package net.oschina.app.improve.user.collection;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.bean.Collection;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.utils.CacheManager;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by haibin
 * on 2016/12/30.
 */

class UserCollectionPresenter implements UserCollectionContract.Presenter {
    private final UserCollectionContract.View mView;
    private String mNextPageToken;
    private Type mGsonType;
    static final String CACHE_NAME = "user_collection_cache";

    UserCollectionPresenter(UserCollectionContract.View mView) {
        this.mView = mView;
        mGsonType = new TypeToken<ResultBean<PageBean<Collection>>>() {
        }.getType();
        this.mView.setPresenter(this);
    }

    @Override
    public void getCache(Context context) {
        List<Collection> items = CacheManager.readListJson(context, CACHE_NAME, Collection.class);
        if (items == null || items.size() == 0)
            return;
        mView.onRefreshSuccess(items);
        mView.onComplete();
    }

    @Override
    public void onRefreshing() {
        OSChinaApi.getCollectionList(null, new TextHttpResponseHandler() {

            @Override
            public void onFinish() {
                super.onFinish();
                mView.onComplete();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showNetworkError(R.string.tip_network_error);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultBean<PageBean<Collection>> resultBean = new Gson().fromJson(responseString, mGsonType);
                    if (resultBean != null) {
                        List<Collection> items;
                        if (resultBean.isSuccess()) {
                            mNextPageToken = resultBean.getResult().getNextPageToken();
                            items = resultBean.getResult().getItems();
                            mView.onRefreshSuccess(items);
                            if (items.size() < 20)
                                mView.showMoreMore();
                        } else {
                            mView.showNetworkError(R.string.tip_network_error);
                        }
                    } else {
                        mView.showNetworkError(R.string.tip_network_error);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mView.showNetworkError(R.string.tip_network_error);
                }
            }
        });
    }

    @Override
    public void onLoadMore() {
        OSChinaApi.getCollectionList(mNextPageToken, new TextHttpResponseHandler() {
            @Override
            public void onFinish() {
                super.onFinish();
                mView.onComplete();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showNetworkError(R.string.tip_network_error);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultBean<PageBean<Collection>> resultBean = new Gson().fromJson(responseString, mGsonType);
                    if (resultBean != null) {
                        List<Collection> items;
                        if (resultBean.isSuccess()) {
                            mNextPageToken = resultBean.getResult().getNextPageToken();
                            items = resultBean.getResult().getItems();
                            mView.onLoadMoreSuccess(items);
                            if (items.size() < 20)
                                mView.showMoreMore();
                        } else {
                            mView.showNetworkError(R.string.tip_network_error);
                        }
                    } else {
                        mView.showNetworkError(R.string.tip_network_error);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mView.showNetworkError(R.string.tip_network_error);
                }
            }
        });
    }

    @Override
    public void getFavReverse(final Collection collection, final int position) {
        OSChinaApi.getFavReverse(collection.getId(), collection.getType(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showNetworkError(R.string.tip_network_error);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (collection.isFavorite()) {
                    mView.showGetFavSuccess(position);
                }
            }
        });
    }
}
