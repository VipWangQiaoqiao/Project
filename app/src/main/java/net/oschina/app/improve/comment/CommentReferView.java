package net.oschina.app.improve.comment;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;

import net.oschina.app.R;
import net.oschina.app.improve.bean.comment.Comment;
import net.oschina.app.improve.bean.comment.Refer;
import net.oschina.app.improve.user.activities.OtherUserHomeActivity;
import net.oschina.app.widget.TweetTextView;

/**
 * Created by fei
 * on 2016/11/21.
 * desc:
 */

public class CommentReferView extends LinearLayout {

    private static final String TAG = "CommentReferView";
    private LinearLayout mLayRoot;
    private ImageView mIvAvatar;
    private TextView mTvVoteCount;
    private ImageView mIvVoteStatus;
    private TextView mTvName;
    private TextView mTvPubDate;
    private TweetTextView mContent;
    private ImageView mComment;
    private LayoutInflater mInflate;

    public CommentReferView(Context context) {
        super(context);
        Log.e(TAG, "CommentReferView: ---------->1");
        initView();
    }

    public CommentReferView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
        Log.e(TAG, "CommentReferView: ---------->2");
    }

    public CommentReferView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        Log.e(TAG, "CommentReferView: ---------->3");
    }

    private void initView() {


        LayoutInflater inflater = LayoutInflater.from(getContext());
        LinearLayout rootView = (LinearLayout) inflater.inflate(R.layout.lay_comment_item, this, false);
        this.mIvAvatar = (ImageView) rootView.findViewById(R.id.iv_avatar);
        this.mTvVoteCount = (TextView) rootView.findViewById(R.id.tv_vote_count);
        this.mIvVoteStatus = (ImageView) rootView.findViewById(R.id.btn_vote);
        this.mTvName = (TextView) rootView.findViewById(R.id.tv_name);
        this.mTvPubDate = (TextView) rootView.findViewById(R.id.tv_pub_date);
        this.mContent = (TweetTextView) rootView.findViewById(R.id.tv_content);
        this.mComment = (ImageView) rootView.findViewById(R.id.btn_comment);

        this.mInflate = inflater;

        this.mLayRoot = rootView;
    }


    public void addComment(final Comment comment, final RequestManager imageLoader, final OnCommentClickListener onCommentClickListener) {

        ImageView ivAvatar = this.mIvAvatar;
        TextView tvVoteCount = this.mTvVoteCount;
        ImageView ivVoteStatus = this.mIvVoteStatus;
        TextView tvName = this.mTvName;
        TextView tvPubDate = this.mTvPubDate;
        TweetTextView content = this.mContent;
        ImageView ivComment = this.mComment;

        ivVoteStatus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                handVote();
            }

            private void handVote() {

            }
        });

        ivComment.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        tvName.setText(comment.getAuthor().getName());
        tvPubDate.setText(comment.getPubDate());

        CommentsUtil.formatHtml(getResources(), content, comment.getContent());

        imageLoader.load(comment.getAuthor().getPortrait()).error(R.mipmap.widget_dface)
                .into(ivAvatar);
        ivAvatar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                OtherUserHomeActivity.show(getContext(), comment.getAuthor().getId());
            }
        });

        tvVoteCount.setText(String.valueOf(comment.getVote()));

        if (comment.getVoteState() == 1) {
            ivVoteStatus.setImageResource(R.mipmap.ic_thumbup_actived);
        } else if (comment.getVoteState() == 0) {
            ivVoteStatus.setImageResource(R.mipmap.ic_thumb_normal);
        }

        Refer[] refers = comment.getRefer();

        if (refers != null && refers.length > 0) {
            int len = refers.length;
            Log.e(TAG, "addComment: ------------------>len=" + len);
            View view = CommentsUtil.getReferLayout(mInflate, refers, 0);
            addView(view, indexOfChild(content));

        }


    }
}
