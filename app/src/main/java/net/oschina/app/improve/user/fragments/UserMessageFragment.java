package net.oschina.app.improve.user.fragments;

import android.view.View;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.AppContext;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.base.fragments.BaseRecyclerViewFragment;
import net.oschina.app.improve.bean.Message;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.simple.Author;
import net.oschina.app.improve.user.adapter.UserMessageAdapter;
import net.oschina.app.ui.empty.EmptyLayout;

import java.lang.reflect.Type;

/**
 * Created by huanghaibin_dev
 * on 2016/8/16.
 */

public class UserMessageFragment extends BaseRecyclerViewFragment<Message> {
    public long authorId;

    @Override
    public void initData() {
        super.initData();
        authorId = Long.parseLong(String.valueOf(AppContext.getInstance().getLoginUid()));
    }

    @Override
    protected void requestData() {
        super.requestData();
        OSChinaApi.getMessageList(authorId, mIsRefresh ? null : mBean.getNextPageToken(), mHandler);
    }

    @Override
    protected BaseRecyclerAdapter<Message> getRecyclerAdapter() {
        return new UserMessageAdapter(this);
    }

    @Override
    protected void onRequestError(int code) {
        for (int i = 0; i < 20; i++) {
            Message message = new Message();
            message.setId(20);
            message.setContent("今天开会啦");
            message.setPubDate("2016-08-18 14:03:50");
            message.setSender(new Author());
            message.getSender().setName("天神哥");
            message.getSender().setPortrait("https://static.oschina.net/uploads/user/1222/2445220_100.jpg?t=1468425830000");

            mAdapter.addItem(message);
        }mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        mRefreshLayout.setVisibility(View.VISIBLE);
        super.onRequestError(code);
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<Message>>>() {
        }.getType();
    }
}
