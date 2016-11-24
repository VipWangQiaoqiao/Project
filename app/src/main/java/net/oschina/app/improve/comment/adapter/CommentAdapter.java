package net.oschina.app.improve.comment.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.comment.Comment;
import net.oschina.app.improve.comment.CommentReferView;
import net.oschina.app.improve.comment.CommentsUtil;
import net.oschina.app.util.StringUtils;
import net.oschina.app.widget.TweetTextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by fei
 * on 2016/11/21.
 * desc:
 */

public class CommentAdapter extends BaseRecyclerAdapter<Comment> {


    private RequestManager mRequestManager;

    public CommentAdapter(final Context context, RequestManager requestManager, int mode) {
        super(context, mode);
        this.mRequestManager = requestManager;
    }

    @Override
    protected CommentHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.lay_comment_refer_item, parent, false);
        return new CommentHolder(view);
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Comment item, int position) {
        if (holder instanceof CommentHolder) {
            ((CommentHolder) holder).addComment(item, mRequestManager, mItems.size(), position);
        }
    }


    protected static class CommentHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.iv_avatar)
        CircleImageView mIvAvatar;

        @Bind(R.id.tv_name)
        TextView mName;
        @Bind(R.id.tv_pub_date)
        TextView mPubDate;
        @Bind(R.id.tv_vote_count)
        TextView mVoteCount;
        @Bind(R.id.btn_vote)
        ImageView mVote;
        @Bind(R.id.btn_comment)
        ImageView mComment;

        @Bind(R.id.lay_refer)
        CommentReferView mCommentReferView;

        @Bind(R.id.tv_content)
        TweetTextView mTweetTextView;
        @Bind(R.id.line)
        View mLine;

        CommentHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        /**
         * add comment
         *
         * @param comment comment
         */

        public void addComment(final Comment comment, RequestManager requestManager, int length, int position) {
            requestManager.load(comment.getAuthor().getPortrait()).error(R.mipmap.widget_dface).into(mIvAvatar);
            mName.setText(comment.getAuthor().getName());
            mPubDate.setText(StringUtils.formatSomeAgo(comment.getPubDate()));
            mVoteCount.setText(String.valueOf(comment.getVote()));
            if (comment.getVoteState() == 1) {
                mVote.setImageResource(R.mipmap.ic_thumbup_actived);
            } else if (comment.getVoteState() == 0) {
                mVote.setImageResource(R.mipmap.ic_thumb_normal);
            }
            mVote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int voteStatus = 0;
                    if (comment.getVoteState() == 1) {
                        mVote.setImageResource(R.mipmap.ic_thumb_normal);
                        voteStatus = 0;
                    } else {
                        mVote.setImageResource(R.mipmap.ic_thumbup_actived);
                        voteStatus = 1;
                    }

                    OSChinaApi.voteComment(0, comment.getId(), voteStatus, new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseString) {

                        }
                    });

                }
            });
            mComment.setImageResource(R.mipmap.ic_comment_30);
            mComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            mVoteCount.setText(String.valueOf(comment.getVote()));

            mCommentReferView.addComment(comment);

            CommentsUtil.formatHtml(mTweetTextView.getResources(), mTweetTextView, comment.getContent());
            if (position == length - 1) {
                mLine.setVisibility(View.GONE);
            }

        }

    }
}
