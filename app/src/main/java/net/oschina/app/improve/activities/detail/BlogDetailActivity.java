package net.oschina.app.improve.activities.detail;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.Report;
import net.oschina.app.bean.Result;
import net.oschina.app.improve.bean.BlogDetail;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.simple.UserRelation;
import net.oschina.app.improve.activities.detail.contract.BlogDetailContract;
import net.oschina.app.improve.fragments.blog.BlogDetailFragment;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.HTMLUtil;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.URLsUtils;
import net.oschina.app.util.XmlUtils;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

public class BlogDetailActivity extends DetailActivity<BlogDetail,BlogDetailContract.View> implements BlogDetailContract.Operator {
    public static void show(Context context, long id) {
        Intent intent = new Intent(context, BlogDetailActivity.class);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_blog_detail;
    }

    protected void requestData() {
        OSChinaApi.getBlogDetail(getDataId(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
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
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }

    @Override
    protected void showView() {
        BlogDetailFragment fragment = BlogDetailFragment.instantiate(this, mData);
        FragmentTransaction trans = getSupportFragmentManager()
                .beginTransaction();
        trans.replace(R.id.lay_container, fragment);
        trans.commitAllowingStateLoss();
        mView = fragment;
    }


    @Override
    public BlogDetail getBlogDetail() {
        return getData();
    }

    @Override
    public void toFavorite() {
        int uid = requestCheck();
        if (uid == 0)
            return;
        showWaitDialog(R.string.progress_submit);
        final BlogDetail blogDetail = getData();
        OSChinaApi.getFavReverse(getDataId(), 3, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                hideWaitDialog();
                if (blogDetail == null)
                    return;
                if (blogDetail.isFavorite())
                    AppContext.showToastShort(R.string.del_favorite_faile);
                else
                    AppContext.showToastShort(R.string.add_favorite_faile);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<BlogDetail>>() {
                    }.getType();

                    ResultBean<BlogDetail> resultBean = AppContext.createGson().fromJson(responseString, type);
                    if (resultBean != null && resultBean.isSuccess()) {
                        blogDetail.setFavorite(!blogDetail.isFavorite());
                        mView.toFavoriteOk(blogDetail);
                        if (blogDetail.isFavorite())
                            AppContext.showToastShort(R.string.add_favorite_success);
                        else
                            AppContext.showToastShort(R.string.del_favorite_success);
                    }
                    hideWaitDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }

    @Override
    public void toShare() {
        if (getDataId() != 0 && getData() != null) {
            String content;

            String url = String.format(URLsUtils.URL_MOBILE + "blog/%s", getDataId());
            final BlogDetail blogDetail = getData();
            if (blogDetail.getBody().length() > 55) {
                content = HTMLUtil.delHTMLTag(blogDetail.getBody().trim());
                if (content.length() > 55)
                    content = StringUtils.getSubString(0, 55, content);
            } else {
                content = HTMLUtil.delHTMLTag(blogDetail.getBody().trim());
            }
            String title = blogDetail.getTitle();

            if (TextUtils.isEmpty(url) || TextUtils.isEmpty(content) || TextUtils.isEmpty(title)) {
                AppContext.showToast("内容加载失败...");
                return;
            }
            toShare(title, content, url);
        } else {
            AppContext.showToast("内容加载失败...");
        }
    }

    @Override
    public void toFollow() {
        int uid = requestCheck();
        if (uid == 0)
            return;
        showWaitDialog(R.string.progress_submit);
        final BlogDetail blogDetail = getData();
        OSChinaApi.addUserRelationReverse(blogDetail.getAuthorId(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                AppContext.showToast("关注失败!");
                hideWaitDialog();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<UserRelation>>() {
                    }.getType();

                    ResultBean<UserRelation> resultBean = AppContext.createGson().fromJson(responseString, type);
                    if (resultBean != null && resultBean.isSuccess()) {
                        blogDetail.setAuthorRelation(resultBean.getResult().getRelation());
                        mView.toFollowOk(blogDetail);
                        if (blogDetail.getAuthorRelation() >= 3) {
                            AppContext.showToast("取消关注成功");
                        } else {
                            AppContext.showToast("关注成功");
                        }
                    }
                    hideWaitDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
                hideWaitDialog();
            }
        });
    }

    @Override
    public void toSendComment(long id, long authorId, String comment) {
        int uid = requestCheck();
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
                    net.oschina.app.bean.ResultBean rsb = XmlUtils.toBean(net.oschina.app.bean.ResultBean.class,
                            new ByteArrayInputStream(arg2));
                    Result res = rsb.getResult();
                    if (res.OK()) {
                        BlogDetailContract.View view = mView;
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

        long dataId = getDataId();

        if (dataId != id)
            OSChinaApi.replyBlogComment(dataId, uid, comment, id, authorId, handler);
        else
            OSChinaApi.publicBlogComment(dataId, uid, comment, handler);
    }

    @Override
    public void toReport() {
        int uid = requestCheck();
        if (uid == 0)
            return;
        toReport(getDataId(),getData().getHref(),Report.TYPE_QUESTION);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mView = null;
    }
}
