package net.oschina.app.improve.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.EventApplyData;
import net.oschina.app.bean.Result;
import net.oschina.app.improve.bean.EventDetail;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.contract.EventDetailContract;
import net.oschina.app.improve.fragments.event.EventDetailFragment;
import net.oschina.app.ui.ShareDialog;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.HTMLUtil;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.URLsUtils;
import net.oschina.app.util.XmlUtils;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

/**
 * Created by huanghaibin
 * on 16-6-13.
 */
public class EventDetailActivity extends AppCompatActivity implements EventDetailContract.Operator {

    private EmptyLayout mEmptyLayout;
    private long mId;
    private EventDetail mDetail;
    private EventDetailContract.View mView;
    private ProgressDialog mDialog;
    private ShareDialog dialog;

    public static void show(Context context, long id) {
        Intent intent = new Intent(context, EventDetailActivity.class);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_improve_event_detail);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(false);
        }
        initView();
        initData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_event_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_event_share) {
            toShare();
        }
        return true;
    }


    public void toShare() {
        String content;
        String url;
        String title;
        if (mId != 0 && mDetail != null) {
            url = String.format(URLsUtils.URL_MOBILE + "event/%s", mId);
            if (mDetail.getBody().length() > 55) {
                content = HTMLUtil.delHTMLTag(mDetail.getBody().trim());
                if (content.length() > 55)
                    content = StringUtils.getSubString(0, 55, content);
            } else {
                content = HTMLUtil.delHTMLTag(mDetail.getBody().trim());
            }
            title = mDetail.getTitle();

            if (TextUtils.isEmpty(url) || TextUtils.isEmpty(content) || TextUtils.isEmpty(title)) {
                AppContext.showToast("内容加载失败...");
                return;
            }
        } else {
            AppContext.showToast("内容加载失败...");
            return;
        }

        if (dialog == null) {
            dialog = new ShareDialog(this);
        }
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setTitle(R.string.share_to);
        dialog.setShareInfo(title, content, url);
        dialog.show();
    }

    private void initView() {
        mEmptyLayout = (EmptyLayout) findViewById(R.id.lay_error);
        mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                getEventDetail();
            }
        });
    }

    private void initData() {
        mId = getIntent().getLongExtra("id", 0);
        getEventDetail();
    }

    @Override
    public void toFav() {
        OSChinaApi.getFavReverse(mId, 6, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                getWaitDialog().dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<EventDetail>>() {
                    }.getType();

                    ResultBean<EventDetail> resultBean = AppContext.createGson().fromJson(responseString, type);
                    if (resultBean != null && resultBean.isSuccess()) {
                        mDetail.setFavorite(!mDetail.isFavorite());
                        mView.toFavOk(mDetail);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
                getWaitDialog().dismiss();
            }
        });
    }

    @Override
    public void toSignUp(EventApplyData data) {
        getWaitDialog().show();
        OSChinaApi.eventApply(data, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Result rs = XmlUtils.toBean(net.oschina.app.bean.ResultBean.class,
                        new ByteArrayInputStream(responseBody)).getResult();
                if (rs.OK()) {
                    AppContext.showToast("报名成功");

                    mView.toSignUpOk(mDetail);
                } else {
                    AppContext.showToast(rs.getErrorMessage());
                }
                getWaitDialog().dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                getWaitDialog().dismiss();
            }
        });
    }

    private void getEventDetail() {
        OSChinaApi.getEventDetail(mId, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mEmptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<EventDetail>>() {
                    }.getType();

                    ResultBean<EventDetail> resultBean = AppContext.createGson().fromJson(responseString, type);
                    if (resultBean != null && resultBean.isSuccess()) {
                        mDetail = resultBean.getResult();
                        showEventDetail();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }

    private void showEventDetail() {
        EventDetailFragment fragment = EventDetailFragment.instantiate(this, mDetail);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.lay_container, fragment);
        transaction.commitAllowingStateLoss();
        mView = fragment;
    }

    public ProgressDialog getWaitDialog() {
        if (mDialog == null) {
            mDialog = new ProgressDialog(this);
            mDialog.setMessage(getResources().getString(R.string.progress_submit));
        }
        return mDialog;
    }
}
