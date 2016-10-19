package net.oschina.app.improve.user.activities;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.improve.base.activities.BaseRecyclerViewActivity;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.Collection;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.user.adapter.CollectionAdapter;

import java.lang.reflect.Type;

/**
 * Created by haibin
 * on 2016/10/18.
 */

public class UserCollectionActivity extends BaseRecyclerViewActivity<Collection> {

    @Override
    protected void requestData() {

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
