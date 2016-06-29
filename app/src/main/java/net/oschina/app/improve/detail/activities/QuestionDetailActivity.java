package net.oschina.app.improve.detail.activities;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.MenuItem;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.Report;
import net.oschina.app.improve.bean.QuestionDetail;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.simple.CommentEX;
import net.oschina.app.improve.detail.contract.QuestionDetailContract;
import net.oschina.app.improve.detail.fragments.DetailFragment;
import net.oschina.app.improve.detail.fragments.QuestionDetailFragment;
import net.oschina.app.util.HTMLUtil;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.URLsUtils;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

/**
 * Created by fei on 2016/6/13.
 * desc:   question detail  module
 */
public class QuestionDetailActivity extends DetailActivity<QuestionDetail, QuestionDetailContract.View> implements QuestionDetailContract.Operator {

    public static void show(Context context, long id) {
        Intent intent = new Intent(context, QuestionDetailActivity.class);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }

    @Override
    int getOptionsMenuId() {
        return R.menu.menu_detail_report;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_report) {
            toReport();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    void requestData() {
        OSChinaApi.getQuestionDetail(getDataId(), getRequestHandler());
    }

    @Override
    Class<? extends DetailFragment> getDataViewFragment() {
        return QuestionDetailFragment.class;
    }

    @Override
    Type getDataType() {
        return new TypeToken<ResultBean<QuestionDetail>>() {
        }.getType();
    }


    @Override
    public void toFavorite() {
        int uid = requestCheck();
        if (uid == 0)
            return;
        showWaitDialog(R.string.progress_submit);
        final QuestionDetail questionDetail = getData();
        OSChinaApi.getFavReverse(getDataId(), 2, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                hideWaitDialog();
                if (questionDetail == null)
                    return;
                if (questionDetail.isFavorite())
                    AppContext.showToastShort(R.string.del_favorite_faile);
                else
                    AppContext.showToastShort(R.string.add_favorite_faile);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<QuestionDetail>>() {
                    }.getType();

                    ResultBean<QuestionDetail> resultBean = AppContext.createGson().fromJson(responseString, type);
                    if (resultBean != null && resultBean.isSuccess()) {
                        questionDetail.setFavorite(!questionDetail.isFavorite());
                        mView.toFavoriteOk(questionDetail);
                        if (questionDetail.isFavorite())
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

            String url = String.format(URLsUtils.URL_MOBILE + "question/%s", getDataId());
            final QuestionDetail blogDetail = getData();
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
    public void toSendComment(long id, long commentId, long commentAuthorId, String comment) {
        int uid = requestCheck();
        if (uid == 0)
            return;

        if (TextUtils.isEmpty(comment)) {
            AppContext.showToastShort(R.string.tip_comment_content_empty);
            return;
        }

        OSChinaApi.pubQuestionComment(id, commentId, commentAuthorId, comment, new TextHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showWaitDialog(R.string.progress_submit);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                AppContext.showToast("评论失败!");
                hideWaitDialog();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<CommentEX>>() {
                    }.getType();

                    ResultBean<CommentEX> resultBean = AppContext.createGson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        CommentEX respComment = resultBean.getResult();
                        if (respComment != null) {
                            QuestionDetailContract.View view = mView;
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

    @Override
    public void toReport() {
        int uid = requestCheck();
        if (uid == 0)
            return;
        toReport(getDataId(), getData().getHref(), Report.TYPE_QUESTION);
    }

}
