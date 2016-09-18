package net.oschina.app.improve.user.fragments;

import android.os.Bundle;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.api.remote.OSChinaApi;
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

import java.lang.reflect.Type;

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
