package net.oschina.app.improve.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.umeng.socialize.sso.UMSsoHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.FavoriteList;
import net.oschina.app.bean.Report;
import net.oschina.app.bean.Result;
import net.oschina.app.improve.bean.NewsDetail;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.simple.Comment;
import net.oschina.app.improve.contract.NewsDetailContract;
import net.oschina.app.improve.fragments.news.NewsDetailFragment;
import net.oschina.app.ui.ReportDialog;
import net.oschina.app.ui.ShareDialog;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.DialogHelp;
import net.oschina.app.util.HTMLUtil;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;
import net.oschina.app.util.URLsUtils;
import net.oschina.app.util.XmlUtils;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by fei on 2016/6/13.
 * desc:   news detail  module
 */
public class NewsDetailActivity extends AppCompatActivity implements NewsDetailContract.Operator {

    private long mId;
    private EmptyLayout mEmptyLayout;
    private NewsDetail newsDetail;
    private NewsDetailContract.View mView;
    private ShareDialog dialog;
    private List<Comment> comments;

    public static void show(Context context, long id) {
        Intent intent = new Intent(context, NewsDetailActivity.class);
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
            initData();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_blog_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_report) {
            toReport();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showNews() {
        NewsDetailFragment fragment = NewsDetailFragment.instantiate(this, newsDetail);
        FragmentTransaction trans = getSupportFragmentManager()
                .beginTransaction();
        trans.replace(R.id.lay_container, fragment);
        trans.commitAllowingStateLoss();
        mView = fragment;
    }

    private void showError(int type) {
        EmptyLayout layout = mEmptyLayout;
        if (layout != null) {
            layout.setErrorType(type);
            layout.setVisibility(View.VISIBLE);
        }
    }

    private void initData() {
        OSChinaApi.getNewsDetail(mId, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                showError(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<NewsDetail>>() {
                    }.getType();

                    ResultBean<NewsDetail> resultBean = AppContext.createGson().fromJson(responseString, type);
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

    /**
     * 获取评论列表
     *
     * @param id 当前资讯的id
     */
    private void getComments(long id) {

        OSChinaApi.getComment(mId, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                showError(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<List<Comment>>>() {
                    }.getType();

                    ResultBean<List<Comment>> resultBean = AppContext.createGson().fromJson(responseString, type);
                    if (resultBean != null && resultBean.isSuccess()) {
                        List<Comment> commentList = resultBean.getResult();
                        if (commentList != null && !commentList.isEmpty()) {
                            comments = commentList;
                        }
                    }
                    showError(EmptyLayout.NODATA);
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }

    private void handleData(NewsDetail newsDetail) {
        showError(View.INVISIBLE);
        this.newsDetail = newsDetail;
        showNews();
    }

    private int check() {
        if (mId == 0 || newsDetail == null) {
            AppContext.showToast("数据加载中...");
            return 0;
        }
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.tip_no_internet);
            return 0;
        }
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.showLoginActivity(this);
            return 0;
        }
        // 返回当前登录用户ID
        return AppContext.getInstance().getLoginUid();
    }

    @Override
    public NewsDetail getNewsDetail() {
        return newsDetail;
    }

    @Override
    public void toFavorite() {
        int uid = check();
        if (uid == 0)
            return;

        AsyncHttpResponseHandler mFavoriteHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                try {
                    Result res = XmlUtils.toBean(net.oschina.app.bean.ResultBean.class,
                            new ByteArrayInputStream(arg2)).getResult();
                    if (res.OK()) {
                        NewsDetailContract.View view = mView;
                        if (view == null)
                            return;

                        newsDetail.setFavorite(!newsDetail.isFavorite());
                        view.toFavoriteOk(newsDetail);
                        if (newsDetail.isFavorite())
                            AppContext.showToastShort(R.string.add_favorite_success);
                        else
                            AppContext.showToastShort(R.string.del_favorite_success);
                    } else {
                        if (newsDetail.isFavorite())
                            AppContext.showToastShort(R.string.del_favorite_faile);
                        else
                            AppContext.showToastShort(R.string.add_favorite_faile);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(arg0, arg1, arg2, e);
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                  Throwable arg3) {
                NewsDetail detail = newsDetail;
                if (detail == null)
                    return;
                if (detail.isFavorite())
                    AppContext.showToastShort(R.string.del_favorite_faile);
                else
                    AppContext.showToastShort(R.string.add_favorite_faile);
            }

            @Override
            public void onStart() {
                showWaitDialog(R.string.progress_submit);
            }

            @Override
            public void onFinish() {
                hideWaitDialog();
            }
        };

        if (newsDetail.isFavorite()) {
            OSChinaApi.delFavorite(uid, mId,
                    FavoriteList.TYPE_BLOG, mFavoriteHandler);
        } else {
            OSChinaApi.addFavorite(uid, mId,
                    FavoriteList.TYPE_BLOG, mFavoriteHandler);
        }
    }

    @Override
    public void toShare() {
        String content;
        String url;
        String title;
        if (mId != 0 && newsDetail != null) {
            url = String.format(URLsUtils.URL_MOBILE + "blog/%s", mId);
            if (newsDetail.getBody().length() > 55) {
                content = HTMLUtil.delHTMLTag(newsDetail.getBody().trim());
                if (content.length() > 55)
                    content = StringUtils.getSubString(0, 55, content);
            } else {
                content = HTMLUtil.delHTMLTag(newsDetail.getBody().trim());
            }
            title = newsDetail.getTitle();

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

    @Override
    public void toFollow() {
        int uid = check();
        if (uid == 0)
            return;

        // 请求关注状态
        OSChinaApi.updateRelation(uid, newsDetail.getAuthorId(), 1,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                        try {
                            Result result = XmlUtils.toBean(net.oschina.app.bean.ResultBean.class,
                                    new ByteArrayInputStream(arg2)).getResult();
                            if (result.OK()) {
                                // 更改用户状态
                                NewsDetailContract.View view = mView;
                                if (view != null)
                                    view.toFollowOk(newsDetail);
                                return;
                            }
                            AppContext.showToast("关注失败!");
                        } catch (Exception e) {
                            e.printStackTrace();
                            onFailure(arg0, arg1, arg2, e);
                        }
                    }

                    @Override
                    public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                          Throwable arg3) {
                        AppContext.showToast("关注失败!");
                    }

                    @Override
                    public void onFinish() {
                        hideWaitDialog();
                    }

                    @Override
                    public void onStart() {
                        showWaitDialog(R.string.progress_submit);
                    }
                });
    }

    @Override
    public void toSendComment(long id, long authorId, String comment) {
        int uid = check();
        if (uid == 0)
            return;

        if (TextUtils.isEmpty(comment)) {
            AppContext.showToastShort(R.string.tip_comment_content_empty);
            return;
        }

        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                try {
                    ResultBean rsb = XmlUtils.toBean(ResultBean.class,
                            new ByteArrayInputStream(arg2));
                    Result res = (Result) rsb.getResult();
                    if (res.OK()) {
                        NewsDetailContract.View view = mView;
                        if (view != null)
                            view.toSendCommentOk();
                    } else {
                        AppContext.showToastShort(res.getErrorMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(arg0, arg1, arg2, e);
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                  Throwable arg3) {
                AppContext.showToastShort(R.string.comment_publish_faile);
            }

            @Override
            public void onStart() {
                showWaitDialog(R.string.progress_submit);
            }

            @Override
            public void onFinish() {
                hideWaitDialog();
            }
        };

        if (mId != id)
            OSChinaApi.replyBlogComment(mId, uid, comment, id, authorId, handler);
        else
            OSChinaApi.publicBlogComment(mId, uid, comment, handler);
    }

    @Override
    public void toReport() {
        int uid = check();
        if (uid == 0)
            return;


        final ReportDialog dialog = new ReportDialog(this,
                "测试", mId, Report.TYPE_QUESTION);
        dialog.setCancelable(true);
        dialog.setTitle(R.string.report);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setNegativeButton(R.string.cancle, null);
        final TextHttpResponseHandler handler = new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int arg0, Header[] arg1, String arg2) {
                if (TextUtils.isEmpty(arg2)) {
                    AppContext.showToastShort(R.string.tip_report_success);
                } else {
                    AppContext.showToastShort(arg2);
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, String arg2,
                                  Throwable arg3) {
                AppContext.showToastShort(R.string.tip_report_faile);
            }

            @Override
            public void onFinish() {
                hideWaitDialog();
            }

            @Override
            public void onStart() {
                showWaitDialog(R.string.progress_submit);
            }
        };
        dialog.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface d, int which) {
                        Report report = null;
                        if ((report = dialog.getReport()) != null) {
                            OSChinaApi.report(report, handler);
                        }
                        d.dismiss();
                    }
                });
        dialog.show();
    }


    private ProgressDialog mDialog;

    public ProgressDialog showWaitDialog(int messageId) {
        String message = getResources().getString(messageId);
        if (mDialog == null) {
            mDialog = DialogHelp.getWaitDialog(this, message);
        }

        mDialog.setMessage(message);
        mDialog.show();

        return mDialog;
    }

    public void hideWaitDialog() {
        ProgressDialog dialog = mDialog;
        if (dialog != null) {
            mDialog = null;
            try {
                dialog.dismiss();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMSsoHandler ssoHandler = dialog.getController().getConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideWaitDialog();
        mEmptyLayout = null;
        mView = null;
        newsDetail = null;
        dialog = null;
    }
}
