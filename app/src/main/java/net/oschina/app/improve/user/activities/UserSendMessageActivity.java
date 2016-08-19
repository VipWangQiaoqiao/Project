package net.oschina.app.improve.user.activities;


import android.content.Context;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.base.activities.BaseRecyclerViewActivity;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.Message;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.behavior.KeyboardInputDelegation;
import net.oschina.app.improve.user.adapter.UserSendMessageAdapter;
import net.oschina.app.util.UIHelper;

import java.lang.reflect.Type;

import butterknife.Bind;

/**
 * 发送消息界面
 * Created by huanghaibin_dev
 * on 2016/8/18.
 */
public class UserSendMessageActivity extends BaseRecyclerViewActivity<Message> {

    @Bind(R.id.root)
    CoordinatorLayout mCoordinatorLayout;
    private KeyboardInputDelegation mDelegation;

    EditText mViewInput;
    private long authorId, receiverId;

    public static void show(Context context, long receiverId) {
        Intent intent = new Intent(context, UserSendMessageActivity.class);
        intent.putExtra("receiverId", receiverId);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_user_send_message;
    }

    @Override
    protected void initData() {
        super.initData();
        receiverId = getIntent().getLongExtra("receiverId", 0);
        authorId = Long.parseLong(String.valueOf(AppContext.getInstance().getLoginUid()));
        init();
    }

    /**
     * 下拉刷新为加载更多
     */
    @Override
    public void onRefreshing() {
        OSChinaApi.getMessageList(receiverId, mBean.getNextPageToken(), mHandler);
    }

    /**
     * 去掉上拉加载
     */
    @Override
    public void onLoadMore() {

    }

    protected void init() {
        mDelegation = KeyboardInputDelegation.delegation(this, mCoordinatorLayout, null);
        mDelegation.showEmoji(getSupportFragmentManager());
        mDelegation.setAdapter(new KeyboardInputDelegation.KeyboardInputAdapter() {
            @Override
            public void onSubmit(TextView v, String content) {
                content = content.replaceAll("[ \\s\\n]+", "");
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(UserSendMessageActivity.this, "请输入文字", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!AppContext.getInstance().isLogin()) {
                    UIHelper.showLoginActivity(UserSendMessageActivity.this);
                    return;
                }
            }

            @Override
            public void onFinalBackSpace(View v) {

            }
        });
        mViewInput = mDelegation.getInputView();
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<Message>>>() {
        }.getType();
    }

    @Override
    protected BaseRecyclerAdapter<Message> getRecyclerAdapter() {
        return new UserSendMessageAdapter(this);
    }
}
