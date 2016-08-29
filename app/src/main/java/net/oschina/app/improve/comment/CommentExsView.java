package net.oschina.app.improve.comment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.simple.CommentEX;
import net.oschina.app.improve.user.activities.OtherUserHomeActivity;
import net.oschina.app.util.StringUtils;
import net.oschina.app.widget.TweetTextView;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.util.TextUtils;

/**
 * Created by JuQiu
 * on 16/6/12.
 */

public class CommentExsView extends LinearLayout implements View.OnClickListener {
    private long mId;
    private int mType;
    private TextView mTitle;
    private TextView mSeeMore;
    private LinearLayout mLayComments;

    public CommentExsView(Context context) {
        super(context);
        init();
    }

    public CommentExsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CommentExsView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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

    public void init(long id, int type, final int commentTotal, final RequestManager imageLoader, final OnCommentClickListener onCommentClickListener) {
        this.mId = id;
        this.mType = type;

        mSeeMore.setVisibility(View.GONE);
        setVisibility(GONE);

        OSChinaApi.getComments(id, type, "refer,reply", null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (throwable != null)
                    throwable.printStackTrace();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<PageBean<CommentEX>>>() {
                    }.getType();

                    ResultBean<PageBean<CommentEX>> resultBean = AppContext.createGson().fromJson(responseString, type);
                    if (resultBean != null && resultBean.isSuccess()) {
                        addComment(resultBean.getResult().getItems(), commentTotal, imageLoader, onCommentClickListener);
                        return;
                    }
                    onFailure(statusCode, headers, responseString, null);
                } catch (Exception e) {
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }

    private void addComment(List<CommentEX> comments, int commentTotal, RequestManager imageLoader, final OnCommentClickListener onCommentClickListener) {
        if (comments != null && comments.size() > 0) {
            if (comments.size() < commentTotal) {
                mSeeMore.setVisibility(VISIBLE);
                mSeeMore.setOnClickListener(this);
            }

            if (getVisibility() != VISIBLE) {
                setVisibility(VISIBLE);
            }

            int clearLine = comments.size() - 1;
            for (final CommentEX comment : comments) {
                if (comment == null || comment.getId() == 0 || TextUtils.isEmpty(comment.getAuthor()))
                    continue;
                ViewGroup lay = addComment(false, comment, imageLoader, onCommentClickListener);
                if (clearLine <= 0) {
                    lay.findViewById(R.id.line).setVisibility(View.INVISIBLE);
                } else {
                    clearLine--;
                }
            }
        } else {
            setVisibility(View.GONE);
        }
    }

    public ViewGroup addComment(final CommentEX comment, RequestManager imageLoader, final OnCommentClickListener onCommentClickListener) {

        if (getVisibility() != VISIBLE) {
            setVisibility(VISIBLE);
        }

        return addComment(true, comment, imageLoader, onCommentClickListener);
    }

    private ViewGroup addComment(boolean first, final CommentEX comment, RequestManager imageLoader, final OnCommentClickListener onCommentClickListener) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        @SuppressLint("InflateParams") ViewGroup lay = (ViewGroup) inflater.inflate(R.layout.lay_comment_item_ex, null, false);

        ImageView ivAvatar = (ImageView) lay.findViewById(R.id.iv_avatar);
        imageLoader.load(comment.getAuthorPortrait()).error(R.mipmap.widget_dface)
                .into(ivAvatar);
        ivAvatar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                OtherUserHomeActivity.show(getContext(), comment.getAuthorId());
            }
        });

        ((TextView) lay.findViewById(R.id.tv_name)).setText(comment.getAuthor());

        TweetTextView content = ((TweetTextView) lay.findViewById(R.id.tv_content));
        CommentsUtil.formatHtml(getResources(), content, comment.getContent());

        if (comment.getRefer() != null) {
            // 最多5层
            View view = CommentsUtil.getReferLayout(inflater, comment.getRefer(), 5);
            lay.addView(view, lay.indexOfChild(content));
        }

        ((TextView) lay.findViewById(R.id.tv_pub_date)).setText(
                StringUtils.formatSomeAgo(comment.getPubDate()));

        ImageView btn_comment = (ImageView) lay.findViewById(R.id.btn_comment);
        if (comment.isBest()) {
            lay.findViewById(R.id.iv_best_answer).setVisibility(VISIBLE);
            btn_comment.setVisibility(GONE);
        } else {
            if (onCommentClickListener != null) {
                btn_comment.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCommentClickListener.onClick(v, comment);
                    }
                });
            }
        }
        if (first)
            mLayComments.addView(lay, 0);
        else
            mLayComments.addView(lay);

        lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(v, comment);
            }
        });
        return lay;
    }

    void onItemClick(View view, CommentEX comment) {
        QuestionAnswerDetailActivity.show(getContext(), comment, mId);
    }

    @Override
    public void onClick(View v) {
        if (mId != 0 && mType != 0)
            CommentExsActivity.show(getContext(), mId, mType);
    }
}
