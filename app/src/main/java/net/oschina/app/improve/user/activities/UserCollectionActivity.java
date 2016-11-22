package net.oschina.app.improve.user.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.base.activities.BaseRecyclerViewActivity;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.Collection;
import net.oschina.app.improve.bean.News;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.detail.activities.BlogDetailActivity;
import net.oschina.app.improve.detail.activities.EventDetailActivity;
import net.oschina.app.improve.detail.activities.NewsDetailActivity;
import net.oschina.app.improve.detail.activities.QuestionDetailActivity;
import net.oschina.app.improve.detail.activities.SoftwareDetailActivity;
import net.oschina.app.improve.detail.activities.TranslateDetailActivity;
import net.oschina.app.improve.user.adapter.CollectionAdapter;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.util.UIHelper;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

/**
 * Created by haibin
 * on 2016/10/18.
 */

public class UserCollectionActivity extends BaseRecyclerViewActivity<Collection> implements BaseRecyclerAdapter.OnItemLongClickListener {

    public static void show(Context context) {
        context.startActivity(new Intent(context, UserCollectionActivity.class));
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mAdapter.setOnItemLongClickListener(this);
    }

    @Override
    protected void onItemClick(Collection item, int position) {
        switch (item.getType()) {
            case News.TYPE_SOFTWARE:
                SoftwareDetailActivity.show(this, item.getId());
                break;
            case News.TYPE_QUESTION:
                QuestionDetailActivity.show(this, item.getId());
                break;
            case News.TYPE_BLOG:
                BlogDetailActivity.show(this, item.getId());
                break;
            case News.TYPE_TRANSLATE:
                TranslateDetailActivity.show(this, item.getId());
                break;
            case News.TYPE_EVENT:
                EventDetailActivity.show(this, item.getId());
                break;
            case News.TYPE_NEWS:
                NewsDetailActivity.show(this, item.getId());
                break;
            default:
                UIHelper.showUrlRedirect(this, item.getHref());
                break;
        }
    }

    @Override
    public void onLongClick(int position, long itemId) {
        final Collection collection = mAdapter.getItem(position);
        if (collection == null)
            return;
        DialogHelper.getConfirmDialog(this, "删除收藏", "是否确认删除该内容吗？", "确认", "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                OSChinaApi.getFavReverse(collection.getId(), collection.getType(), new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        if (collection.isFavorite()) {
                            mAdapter.removeItem(collection);
                        }
                    }
                });
            }
        }).show();
    }

    @Override
    protected void requestData() {
        OSChinaApi.getCollectionList(mIsRefresh ? null : mBean.getNextPageToken(), mHandler);
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<Collection>>>() {
        }.getType();
    }

    @Override
    protected BaseRecyclerAdapter<Collection> getRecyclerAdapter() {
        return new CollectionAdapter(this);
    }
}
