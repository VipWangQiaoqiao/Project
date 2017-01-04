package net.oschina.app.improve.user.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.base.activities.BaseRecyclerViewActivity;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.user.adapter.UserFansOrFollowAdapter;
import net.oschina.app.improve.user.bean.UserFansOrFollows;

import java.lang.reflect.Type;

/**
 * Created by fei on 2016/8/24.
 * desc: user follows
 */

public class UserFollowsActivity extends BaseRecyclerViewActivity<UserFansOrFollows> {

    public static final String BUNDLE_KEY_ID = "bundle_key_id";
    // private static final String TAG = "UserFollowsActivity";
    private long userId;


    private int getRequestType() {
        return OSChinaApi.TYPE_USER_FLOWS;
    }

    /**
     * show activity
     *
     * @param context context
     * @param userId  uid
     */
    public static void show(Context context, long userId) {
        Intent intent = new Intent(context, UserFollowsActivity.class);
        intent.putExtra(BUNDLE_KEY_ID, userId);
        context.startActivity(intent);
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        userId = bundle.getLong(BUNDLE_KEY_ID, 0);
        // TLog.e(TAG, "initBundle: ---------->userId=" + userId);
        return super.initBundle(bundle);
    }

    @Override
    protected void requestData() {
        super.requestData();
        OSChinaApi.getUserFansOrFlows(getRequestType(), userId, mIsRefresh ? null : mBean.getNextPageToken(), mHandler);
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<UserFansOrFollows>>>() {
        }.getType();
    }

    @Override
    protected BaseRecyclerAdapter<UserFansOrFollows> getRecyclerAdapter() {
        return new UserFansOrFollowAdapter(this, BaseRecyclerAdapter.ONLY_FOOTER);
    }

    @Override
    protected void onItemClick(UserFansOrFollows item, int position) {
        super.onItemClick(item, position);
        if (item.getId() <= 0) return;
        OtherUserHomeActivity.show(this, item.getId());
    }
}
