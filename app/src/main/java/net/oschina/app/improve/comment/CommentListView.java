package net.oschina.app.improve.comment;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.comment.Comment;

import java.lang.reflect.Type;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by fei
 * on 2016/11/16.
 * desc:
 */

public class CommentListView extends LinearLayout {

    private long mSourceId;
    private int mType;

    private TextView mTitle;
    private LinearLayout mLayComments;
    private TextView mSeeMore;


    public CommentListView(Context context) {
        super(context);
        init();
    }

    public CommentListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CommentListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.lay_detail_comment_layout, this, true);

        mTitle = (TextView) findViewById(R.id.tv_blog_detail_comment);
        mLayComments = (LinearLayout) findViewById(R.id.lay_blog_detail_comment);
        mSeeMore = (TextView) findViewById(R.id.tv_see_more_comment);
    }

    public void setTitle(String title) {
        if (!android.text.TextUtils.isEmpty(title)) {
            mTitle.setText(title);
        }
    }

    /**
     * @return TypeToken
     */
    Type getDataType() {
        return new TypeToken<ResultBean<PageBean<Comment>>>() {
        }.getType();
    }


    public void initComment(long sourceId, int type, String parts, int order, String pageToken, int commentTotal, RequestManager imageLoader,
                            OnCommentClickListener onCommentClickListener) {
        setVisibility(GONE);
        OSChinaApi.getComments(sourceId, type, parts, order, pageToken, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                ResultBean<PageBean<Comment>> resultBean = AppOperator.createGson().fromJson(responseString, getDataType());
                if (resultBean.isSuccess()) {
                    ArrayList<Comment> items = (ArrayList<Comment>) resultBean.getResult().getItems();
                    items.trimToSize();
                    for (Comment item : items) {


                    }

                }

            }
        });

        this.mSourceId = sourceId;
        this.mType = type;
    }


    /**
     * 添加一条评论
     *
     * @param comment comment
     */
    public void addComment(Comment comment) {

    }
}
