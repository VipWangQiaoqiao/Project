package net.oschina.app.improve.user.fragments;

import android.content.Context;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.base.fragments.BaseRecyclerViewFragment;
import net.oschina.app.improve.bean.Message;
import net.oschina.app.improve.bean.User;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.user.activities.UserMessageActivity;
import net.oschina.app.improve.user.activities.UserSendMessageActivity;
import net.oschina.app.improve.user.adapter.UserMessageAdapter;

import java.lang.reflect.Type;

/**
 * Created by huanghaibin_dev
 * on 2016/8/16.
 */

public class UserMessageFragment extends BaseRecyclerViewFragment<Message> {

    public long authorId;

    private UserMessageActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context != null && context instanceof UserMessageActivity) {
            activity = (UserMessageActivity) context;
        }
    }

    @Override
    protected void onRequestSuccess(int code) {
        super.onRequestSuccess(code);
        if (activity != null && isRefreshing) activity.onRequestSuccess(2);
    }

    @Override
    public void initData() {
        super.initData();
        authorId = AccountHelper.getUserId();
    }

    @Override
    protected void requestData() {
        super.requestData();
        OSChinaApi.getUserMessageList(isRefreshing ? null : mBean.getNextPageToken(), mHandler);
    }

    @Override
    public void onItemClick(int position, long itemId) {
        Message message = mAdapter.getItem(position);
        if (message == null)
            return;
        User sender = message.getSender();
        if (sender != null)
            UserSendMessageActivity.show(getContext(), message.getSender());
    }

    @Override
    protected BaseRecyclerAdapter<Message> getRecyclerAdapter() {
        return new UserMessageAdapter(this);
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<Message>>>() {
        }.getType();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
