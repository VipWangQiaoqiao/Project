package net.oschina.app.improve.detail.activities;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.bean.BlogDetail;
import net.oschina.app.improve.bean.Collection;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.comment.Comment;
import net.oschina.app.improve.bean.simple.UserRelation;
import net.oschina.app.improve.comment.CommentsActivity;
import net.oschina.app.improve.detail.contract.BlogDetailContract;
import net.oschina.app.improve.detail.fragments.BlogDetailFragment;
import net.oschina.app.improve.detail.fragments.DetailFragment;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

public class BlogDetailActivity extends DetailActivity<BlogDetail, BlogDetailContract.View>
        implements BlogDetailContract.Operator {

    public static void show(Context context, long id) {
        Intent intent = new Intent(context, BlogDetailActivity.class);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }

    @Override
    void requestData() {
        OSChinaApi.getBlogDetail(getDataId(), getRequestHandler());
    }

    @Override
    Class<? extends DetailFragment> getDataViewFragment() {
        return BlogDetailFragment.class;
    }

    @Override
    Type getDataType() {
        return new TypeToken<ResultBean<BlogDetail>>() {
        }.getType();
    }

    @Override
    public void toFavorite() {
        long uid = requestCheck();
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
                    Type type = new TypeToken<ResultBean<Collection>>() {
                    }.getType();

                    ResultBean<Collection> resultBean = AppOperator.createGson().fromJson(responseString, type);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean createOptionsMenu = super.onCreateOptionsMenu(menu);
        if (createOptionsMenu) {
            mCommentCountView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommentsActivity.show(BlogDetailActivity.this, mDataId, OSChinaApi.COMMENT_BLOG, OSChinaApi.COMMENT_NEW_ORDER);
                }
            });
        }
        return createOptionsMenu;
    }

    @Override
    public void toShare() {
        if (getData() != null) {
            final BlogDetail detail = getData();
            String title = detail.getTitle();
            String content = detail.getBody();
            String url = detail.getHref();
            if (!toShare(title, content, url, 3))
                AppContext.showToast("抱歉，内容无法分享！");
        } else {
            AppContext.showToast("内容加载失败！");
        }
    }

    @Override
    public void toFollow() {
        long uid = requestCheck();
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

                    ResultBean<UserRelation> resultBean = AppOperator.createGson().fromJson(responseString, type);
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
    public void toSendComment(long id, long commentId, long commentAuthorId, String comment) {
        long uid = requestCheck();
        if (uid == 0)
            return;

        if (TextUtils.isEmpty(comment)) {
            AppContext.showToastShort(R.string.tip_comment_content_empty);
            return;
        }

        OSChinaApi.pubBlogComment(id, commentId, commentAuthorId, comment, new TextHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showWaitDialog(R.string.progress_submit);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                AppContext.showToast(getResources().getString(R.string.pub_comment_failed));
                hideWaitDialog();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<Comment>>() {
                    }.getType();

                    ResultBean<Comment> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        Comment respComment = resultBean.getResult();
                        if (respComment != null) {
                            BlogDetailContract.View view = mView;
                            if (view != null) {
                                view.toSendCommentOk(respComment);
                            }
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

}
