package net.oschina.app.improve.user.fragments;

import android.view.View;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.base.fragments.BaseRecyclerViewFragment;
import net.oschina.app.improve.bean.Mention;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.simple.Comment;
import net.oschina.app.improve.user.adapter.UserMentionAdapter;
import net.oschina.app.ui.empty.EmptyLayout;

import java.lang.reflect.Type;

/**
 * Created by huanghaibin_dev
 * on 2016/8/16.
 */

public class UserMentionFragment extends BaseRecyclerViewFragment<Mention> {

    @Override
    protected void requestData() {
        super.requestData();
        OSChinaApi.getMsgMentionList(mIsRefresh ? null : mBean.getNextPageToken(), mHandler);
    }
    @Override
    protected void onRequestError(int code) {
        for (int i = 0; i < 20; i++) {
            mAdapter.addItem(new Mention());
        }mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        mRefreshLayout.setVisibility(View.VISIBLE);
        super.onRequestError(code);
    }
    @Override
    protected BaseRecyclerAdapter<Mention> getRecyclerAdapter() {
        return new UserMentionAdapter(this);
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<Mention>>>() {
        }.getType();
    }
}
