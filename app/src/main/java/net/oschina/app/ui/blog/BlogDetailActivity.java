package net.oschina.app.ui.blog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.Result;
import net.oschina.app.bean.base.ResultBean;
import net.oschina.app.bean.blog.BlogDetail;
import net.oschina.app.contract.BlogDetailContract;
import net.oschina.app.fragment.general.BlogDetailFragment;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.XmlUtils;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

public class BlogDetailActivity extends AppCompatActivity implements BlogDetailContract.Operator {
    private long mId;
    private EmptyLayout mEmptyLayout;
    private BlogDetail mBlog;
    private BlogDetailContract.View mView;

    public static void show(Context context, long id) {
        Intent intent = new Intent(context, BlogDetailActivity.class);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_detail);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(false);
        }

        mId = getIntent().getLongExtra("id", 0);
        if (mId == 0)
            finish();
        else {
            mEmptyLayout = (EmptyLayout) findViewById(R.id.lay_error);
            mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                    initData();
                }
            });
            //initData();
            showBlog();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.menu_blog_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void showBlog() {
        BlogDetailFragment fragment = BlogDetailFragment.instantiate(this, mBlog);
        FragmentTransaction trans = getSupportFragmentManager()
                .beginTransaction();
        trans.replace(R.id.lay_container, fragment);
        trans.commitAllowingStateLoss();
        mView = fragment;
    }

    private void showError(int type) {
        mEmptyLayout.setErrorType(type);
        mEmptyLayout.setVisibility(View.VISIBLE);
    }

    private void initData() {
        OSChinaApi.getBlogDetail(mId, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                showError(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<BlogDetail>>() {
                    }.getType();

                    ResultBean<BlogDetail> resultBean = AppContext.createGson().fromJson(responseString, type);
                    if (resultBean != null && resultBean.isSuccess()) {
                        handleData(resultBean.getResult());
                        return;
                    }
                    showError(EmptyLayout.NODATA);
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }


    private void handleData(BlogDetail blog) {
        mEmptyLayout.setVisibility(View.INVISIBLE);
        mBlog = blog;
        showBlog();
    }


    @Override
    public void toFavorite() {

    }

    @Override
    public void toShare() {

    }

    @Override
    public void toFollow() {
        int mId = AppContext.getInstance().getLoginUid();
        OSChinaApi.updateRelation(mId, mBlog.getAuthorId(), 1,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                        try {
                            Result result = XmlUtils.toBean(net.oschina.app.bean.ResultBean.class,
                                    new ByteArrayInputStream(arg2)).getResult();
                            if (result.OK()) {
                                mView.toFollowOk();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            onFailure(arg0, arg1, arg2, e);
                        }
                    }

                    @Override
                    public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                          Throwable arg3) {

                    }
                });
    }

    @Override
    public void toSendComment(long id, String comment) {

    }
}
