package net.oschina.app.improve.comment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.RequestManager;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.account.activity.LoginActivity;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.comment.Comment;
import net.oschina.app.improve.bean.comment.Refer;
import net.oschina.app.improve.bean.comment.Vote;
import net.oschina.app.improve.behavior.CommentBar;
import net.oschina.app.improve.user.activities.OtherUserHomeActivity;
import net.oschina.app.improve.widget.IdentityView;
import net.oschina.app.improve.widget.PortraitView;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.TDevice;
import net.oschina.app.widget.TweetTextView;
import net.oschina.common.utils.CollectionUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by fei
 * on 2016/11/16.
 * desc:  资讯、问答、博客、翻译、活动、软件详情评论列表当中进行展示的子view.
 * 包括直接渲染出评论下的refer和reply
 */
public class CommentView extends LinearLayout implements View.OnClickListener {

    private long mId;
    private int mType;
    private TextView mTitle;
    private String mShareTitle;
    private TextView mSeeMore;
    private LinearLayout mLayComments;
    private ProgressDialog mDialog;
    private CommentBar commentBar;

    public CommentView(Context context) {
        super(context);
        init();
    }

    public CommentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CommentView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.lay_detail_comment_layout, this, true);
        mTitle = (TextView) findViewById(R.id.tv_blog_detail_comment);
        mLayComments = (LinearLayout) findViewById(R.id.lay_detail_comment);
        mSeeMore = (TextView) findViewById(R.id.tv_see_more_comment);
    }

    public void setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            mTitle.setText(title);
        }
    }

    public void setShareTitle(String shareTitle) {
        this.mShareTitle = shareTitle;
    }

    public void setCommentBar(CommentBar commentBar) {
        this.commentBar = commentBar;
    }

    /**
     * @return TypeToken
     */
    Type getCommentType() {
        return new TypeToken<ResultBean<PageBean<Comment>>>() {
        }.getType();
    }

    /**
     * @return TypeToken
     */
    Type getVoteType() {
        return new TypeToken<ResultBean<Vote>>() {
        }.getType();
    }

    public void init(long id, final int type, int order, final int commentCount, final RequestManager imageLoader,
                     final OnCommentClickListener onCommentClickListener) {
        this.mId = id;
        this.mType = type;

        mSeeMore.setVisibility(View.GONE);
        setVisibility(GONE);

        OSChinaApi.getComments(id, type, "refer,reply", order, null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (throwable != null)
                    throwable.printStackTrace();
            }

            @SuppressLint("DefaultLocale")
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {

                    ResultBean<PageBean<Comment>> resultBean = AppOperator.createGson().fromJson(responseString, getCommentType());
                    if (resultBean.isSuccess()) {

                        List<Comment> comments = resultBean.getResult().getItems();

                        int size = comments.size();
                        if (type == OSChinaApi.COMMENT_NEWS) {
                            List<Comment> hotComments = new ArrayList<>();
                            hotComments.clear();
                            //筛选出热门评论
                            for (int i = 0, len = comments.size(); i < (len > 5 ? 5 : len); i++) {
                                Comment comment = comments.get(i);
                                if (comment.getVote() > 0) {
                                    hotComments.add(comment);
                                }
                            }
                            comments = hotComments;
                            int len = comments.size();
                            if (len > 0) {
                                //表示热门评论数目
                                setTitle(String.format("%s", getResources().getString(R.string.hot_comment_hint)));
                                mSeeMore.setVisibility(VISIBLE);
                                mSeeMore.setOnClickListener(CommentView.this);
                            }
                        } else if (commentCount > size) {
                            mSeeMore.setVisibility(VISIBLE);
                            mSeeMore.setOnClickListener(CommentView.this);
                        }

                        Comment[] array = CollectionUtil.toArray(comments, Comment.class);
                        initComment(array, imageLoader, onCommentClickListener);
                    }

                } catch (Exception e) {
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }

    private void initComment(final Comment[] comments, final RequestManager imageLoader, final OnCommentClickListener onCommentClickListener) {
        if (mLayComments != null)
            mLayComments.removeAllViews();
        if (comments != null && comments.length > 0) {
            if (getVisibility() != VISIBLE) {
                setVisibility(VISIBLE);
            }

            for (int i = 0, len = comments.length; i < len; i++) {
                final Comment comment = comments[i];
                if (comment != null) {
                    final ViewGroup lay = insertComment(true, comment, imageLoader, onCommentClickListener);
                    lay.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mType == OSChinaApi.COMMENT_EVENT || mType == OSChinaApi.COMMENT_QUESTION) {
                                QuesAnswerDetailActivity.show(lay.getContext(), comment, mId, mType);
                            } else {
                                onCommentClickListener.onClick(v, comment);
                            }
                        }
                    });

                    mLayComments.addView(lay);
                    if (i == len - 1) {
                        lay.findViewById(R.id.line).setVisibility(GONE);
                    } else {
                        lay.findViewById(R.id.line).setVisibility(View.VISIBLE);
                    }
                }
            }
        } else {
            setVisibility(View.GONE);
        }
    }


    @SuppressLint("DefaultLocale")
    private ViewGroup insertComment(final boolean first, final Comment comment, final RequestManager imageLoader,
                                    final OnCommentClickListener onCommentClickListener) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        @SuppressLint("InflateParams") ViewGroup lay = (ViewGroup) inflater.inflate(R.layout.lay_comment_item, null, false);

        IdentityView identityView = (IdentityView) lay.findViewById(R.id.identityView);
        PortraitView ivAvatar = (PortraitView) lay.findViewById(R.id.iv_avatar);
        identityView.setup(comment.getAuthor());
        ivAvatar.setup(comment.getAuthor());
        ivAvatar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                OtherUserHomeActivity.show(getContext(), comment.getAuthor().getId());
            }
        });
        final TextView tvVoteCount = (TextView) lay.findViewById(R.id.tv_vote_count);
        tvVoteCount.setText(String.valueOf(comment.getVote()));
        final ImageView ivVoteStatus = (ImageView) lay.findViewById(R.id.btn_vote);

//        commentBar.getBottomSheet().show(String.format("%s %s",
//                ivComment.getResources().getString(R.string.reply_hint), comment.getAuthor().getName()));

        if (mType == OSChinaApi.COMMENT_QUESTION || mType == OSChinaApi.COMMENT_EVENT
                || mType == OSChinaApi.COMMENT_TRANSLATION || mType == OSChinaApi.COMMENT_BLOG) {

            tvVoteCount.setVisibility(View.GONE);
            ivVoteStatus.setVisibility(View.GONE);
            if (comment.isBest()) {
//                ivComment.setEnabled(false);
//                ivComment.setImageResource(R.mipmap.label_best_answer);
            } else {
//                ivComment.setEnabled(true);
//                ivComment.setImageResource(R.mipmap.ic_comment_30);
            }
        } else {
            //ivComment.setEnabled(true);
            tvVoteCount.setText(String.valueOf(comment.getVote()));
            tvVoteCount.setVisibility(View.VISIBLE);
            ivVoteStatus.setVisibility(View.VISIBLE);

            if (comment.getVoteState() == 1) {
                ivVoteStatus.setImageResource(R.mipmap.ic_thumbup_actived);
                ivVoteStatus.setTag(true);
            } else if (comment.getVoteState() == 0) {
                ivVoteStatus.setImageResource(R.mipmap.ic_thumb_normal);
                ivVoteStatus.setTag(null);
            }

            ivVoteStatus.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    handVote();
                }

                private void handVote() {
                    if (ivVoteStatus.getTag() != null || comment.getVoteState() == 1) {
                        return;
                    }
                    if (!AccountHelper.isLogin()) {
                        LoginActivity.show(getContext());
                        return;
                    }
                    if (!TDevice.hasInternet()) {
                        AppContext.showToast(getResources().getString(R.string.state_network_error), Toast.LENGTH_SHORT);
                        return;
                    }

                    OSChinaApi.voteComment(mType, comment.getId(), comment.getAuthor().getId(), 1, new TextHttpResponseHandler() {

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            requestFailureHint(throwable);
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseString) {

                            ResultBean<Vote> resultBean = AppOperator.createGson().fromJson(responseString, getVoteType());
                            if (resultBean.isSuccess()) {
                                Vote vote = resultBean.getResult();
                                if (vote != null) {
                                    if (vote.getVoteState() == 1) {
                                        comment.setVoteState(1);
                                        ivVoteStatus.setTag(true);
                                        ivVoteStatus.setImageResource(R.mipmap.ic_thumbup_actived);
                                    } else if (vote.getVoteState() == 0) {
                                        comment.setVoteState(0);
                                        ivVoteStatus.setTag(null);
                                        ivVoteStatus.setImageResource(R.mipmap.ic_thumb_normal);
                                    }
                                    long voteVoteCount = vote.getVote();
                                    comment.setVote(voteVoteCount);
                                    tvVoteCount.setText(String.valueOf(voteVoteCount));
                                }
                                AppContext.showToastShort("操作成功!!!");
                            } else {
                                AppContext.showToastShort(resultBean.getMessage());
                            }
                        }

                    });
                }
            });
        }

        String name = comment.getAuthor().getName();
        if (TextUtils.isEmpty(name)) {
            name = getResources().getString(R.string.martian_hint);
        }

        ((TextView) lay.findViewById(R.id.tv_name)).setText(name);

        ((TextView) lay.findViewById(R.id.tv_pub_date)).setText(
                String.format("%s", StringUtils.formatSomeAgo(comment.getPubDate())));

        TweetTextView content = ((TweetTextView) lay.findViewById(R.id.tv_content));
        CommentsUtil.formatHtml(getResources(), content, comment.getContent());
        Refer[] refers = comment.getRefer();

        if (refers != null && refers.length > 0) {
            View view = CommentsUtil.getReferLayout(inflater, refers, 0);
            lay.addView(view, lay.indexOfChild(content));
        }

        if (!first) {
            addView(lay, 0);
        }

        return lay;
    }

    @Override
    public void onClick(View v) {
        if (mId != 0 && mType != 0)
            CommentsActivity.show((AppCompatActivity) getContext(), mId, mType, OSChinaApi.COMMENT_NEW_ORDER, mShareTitle);
    }

    /**
     * request network error
     *
     * @param throwable throwable
     */
    protected void requestFailureHint(Throwable throwable) {
        AppContext.showToastShort(R.string.request_error_hint);
        if (throwable != null) {
            throwable.printStackTrace();
        }
    }
}



