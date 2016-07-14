package net.oschina.app.improve.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.ApiHttpClient;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.Active;
import net.oschina.app.bean.Comment;
import net.oschina.app.bean.User;
import net.oschina.app.bean.UserInformation;
import net.oschina.app.improve.adapter.base.BaseRecyclerAdapter;
import net.oschina.app.improve.adapter.user.UserActiveAdapter;
import net.oschina.app.util.XmlUtils;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 别的用户的主页
 * Created by thanatos on 16/7/13.
 */
public class OtherUserHomeActivity extends BaseRecyclerViewActivity<Active>{

    public static final String KEY_BUNDLE = "KEY_BUNDLE_IN_OTHER_USER_HOME";

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.iv_portrait) CircleImageView mPortrait;
    @Bind(R.id.tv_nick) TextView mNick;
    @Bind(R.id.tv_summary) TextView mSummary;
    @Bind(R.id.tv_score) TextView mScore;
    @Bind(R.id.tv_count_follow) TextView mCountFollow;
    @Bind(R.id.tv_count_fans) TextView mCountFans;

    private User user;
    private int pageNum = 0;
    private TextHttpResponseHandler mUserInfoHandler;

    public static void show(Context context, User user){
//        if (user == null) return;
        Intent intent = new Intent(context, OtherUserHomeActivity.class);
        intent.putExtra(KEY_BUNDLE, user);
        context.startActivity(intent);
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        user = (User) bundle.getSerializable(KEY_BUNDLE);
        if (user == null || user.getId() <= 0) return false;
        return super.initBundle(bundle);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_other_user_home;
    }

    @Override
    protected void initWidget() {
        mToolbar.setTitle("");
        mToolbar.setSubtitle("");
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setHomeButtonEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        injectDataToView();
    }

    private void injectDataToView() {
        getImageLoader()
                .load(user.getPortrait())
                .asBitmap()
                .placeholder(R.drawable.widget_dface)
                .error(R.drawable.widget_dface)
                .into(mPortrait);
        mNick.setText(user.getName());
        // TODO summary
        mScore.setText(String.format("积分 %s", user.getScore()));
        mCountFans.setText(String.format("粉丝 %s", user.getFans()));
        mCountFollow.setText(String.format("关注 %s", user.getFollowers()));

    }

    @Override
    protected void initData() {
        super.initData();
        onRefreshing();
        // temporary usage, changing it util new api come up
        RequestParams params = new RequestParams();
        if (AppContext.getInstance().isLogin()){
            params.put("uid", AppContext.getInstance().getLoginUid());
        }
        params.put("hisuid", user.getId());
        if (!TextUtils.isEmpty(user.getName())){
            params.put("hisname", user.getName());
        }
        params.put("pageIndex", 0);
        params.put("pageSize", 0);
        ApiHttpClient.get("action/api/user_information", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    UserInformation information = XmlUtils.toBean(UserInformation.class, responseBody);
                    user = information.getUser();
                    injectDataToView();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(OtherUserHomeActivity.this, "获取用户数据失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onLoadMore() {
        requestData(pageNum);
    }

    @Override
    protected void requestData() {
        requestData(0);
    }

    @Override
    protected void onLoadingSuccess() {
        super.onLoadingSuccess();
        if (mIsRefresh) pageNum = 0;
        ++pageNum;
    }

    private void requestData(int pageNum) {
        OSChinaApi.getActiveList(user.getId(), 1, pageNum, mHandler);
    }

    @Override
    protected Type getType() {
        return new TypeToken<Active>(){}.getType();
    }

    @Override
    protected BaseRecyclerAdapter<Active> getRecyclerAdapter() {
        return new UserActiveAdapter(this, BaseRecyclerAdapter.ONLY_FOOTER);
    }
}
