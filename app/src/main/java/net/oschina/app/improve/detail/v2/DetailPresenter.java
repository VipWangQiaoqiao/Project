package net.oschina.app.improve.detail.v2;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.bean.Collection;
import net.oschina.app.improve.bean.News;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.comment.Comment;
import net.oschina.app.improve.bean.simple.UserRelation;
import net.oschina.app.ui.empty.EmptyLayout;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

/**
 * Created by haibin
 * on 2016/11/30.
 */

public class DetailPresenter implements DetailContract.Presenter {
    private final DetailContract.View mView;
    private final DetailContract.EmptyView mEmptyView;
    private SubBean mBean;
    private SubBean mCacheBean;
    private String mIdent;

    DetailPresenter(DetailContract.View mView, DetailContract.EmptyView mEmptyView, SubBean bean, String ident) {
        this.mView = mView;
        this.mBean = bean;
        this.mIdent = ident;
        this.mEmptyView = mEmptyView;
        this.mView.setPresenter(this);
    }

    @Override
    public void getCache() {
        mCacheBean = DetailCache.readCache(mBean);
        if (mCacheBean == null)
            return;
        mView.showGetDetailSuccess(mCacheBean);
        mEmptyView.showGetDetailSuccess(mCacheBean);
    }

    @Override
    public void getDetail() {
        OSChinaApi.getDetail(mBean.getType(), mIdent, mBean.getId(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //mView.showNetworkError(R.string.tip_network_error);
                if (mCacheBean != null)
                    return;
                mEmptyView.showErrorLayout(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<SubBean>>() {
                    }.getType();
                    ResultBean<SubBean> bean = AppOperator.createGson().fromJson(responseString, type);
                    if (bean.isSuccess()) {
                        mBean = bean.getResult();
                        mView.showGetDetailSuccess(mBean);
                        mEmptyView.showGetDetailSuccess(mBean);
                    } else {
                        if (mCacheBean != null)
                            return;
                        mEmptyView.showErrorLayout(EmptyLayout.NODATA);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void favReverse() {
        OSChinaApi.getFavReverse(mBean.getId(), mBean.getType(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showFavError();
                mView.showNetworkError(R.string.tip_network_error);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<Collection>>() {
                    }.getType();
                    ResultBean<Collection> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean != null && resultBean.isSuccess()) {
                        Collection collection = resultBean.getResult();
                        mBean.setFavorite(collection.isFavorite());
                        mView.showFavReverseSuccess(collection.isFavorite(), collection.isFavorite() ? R.string.add_favorite_success : R.string.del_favorite_success);
                        mEmptyView.showFavReverseSuccess(collection.isFavorite(), collection.isFavorite() ? R.string.add_favorite_success : R.string.del_favorite_success);
                    } else {
                        mView.showFavError();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }

    @Override
    public void addComment(long sourceId, int type, String content, long referId, long replyId, long reAuthorId) {
        OSChinaApi.pubComment(sourceId, type, content, referId, replyId, reAuthorId, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showNetworkError(R.string.tip_network_error);
                mEmptyView.showCommentError("");
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
                            mView.showCommentSuccess(respComment);
                            mEmptyView.showCommentSuccess(respComment);
                        }
                    } else {
                        mView.showCommentError(resultBean.getMessage());
                        mEmptyView.showCommentError(resultBean.getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                    mView.showCommentError("评论失败");
                    mEmptyView.showCommentError("评论失败");
                }
            }
        });
    }

    @Override
    public void addUserRelation(long authorId) {
        OSChinaApi.addUserRelationReverse(authorId, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showAddRelationError();
                mView.showNetworkError(R.string.tip_network_error);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<UserRelation>>() {
                    }.getType();

                    ResultBean<UserRelation> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean != null && resultBean.isSuccess()) {
                        int relation = resultBean.getResult().getRelation();
                        mBean.getAuthor().setRelation(relation);
                        boolean isRelation = relation == UserRelation.RELATION_ALL
                                || relation == UserRelation.RELATION_ONLY_YOU;
                        mView.showAddRelationSuccess(isRelation,
                                isRelation ? R.string.add_relation_success : R.string.cancel_relation_success);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mView.showAddRelationError();
                }
            }
        });
    }

    boolean isHideCommentBar() {
        return mBean.getType() == News.TYPE_SOFTWARE || mBean.getType() == News.TYPE_EVENT;
    }
}
