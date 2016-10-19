package net.oschina.app.improve.user.activities;

import android.content.Context;
import android.content.Intent;

import com.google.gson.reflect.TypeToken;

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
import net.oschina.app.util.UIHelper;

import java.lang.reflect.Type;

/**
 * Created by haibin
 * on 2016/10/18.
 */

public class UserCollectionActivity extends BaseRecyclerViewActivity<Collection> {

    public static void show(Context context) {
        context.startActivity(new Intent(context, UserCollectionActivity.class));
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
            case News.TYPE_TRNSLATE:
                TranslateDetailActivity.show(this, item.getId());
                break;
            case News.TYPE_EVENT:
                EventDetailActivity.show(this, item.getId());
                break;
            case News.TYPE_NEWS:
                NewsDetailActivity.show(this, item.getId());
                break;
            default:
                UIHelper.showUrlRedirect(this,item.getHref());
                break;
        }
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
