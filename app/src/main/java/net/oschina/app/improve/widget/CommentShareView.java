package net.oschina.app.improve.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.comment.Comment;
import net.oschina.app.improve.comment.CommentReferView;
import net.oschina.app.improve.comment.CommentsUtil;
import net.oschina.app.improve.dialog.ShareDialog;
import net.oschina.app.util.PlatfromUtil;
import net.oschina.app.util.StringUtils;
import net.oschina.app.widget.TweetTextView;
import net.oschina.common.utils.BitmapUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 评论分享的View
 * Created by haibin on 2017/4/17.
 */
@SuppressWarnings("unused")
public class CommentShareView extends NestedScrollView {
    private CommentShareAdapter mAdapter;
    private ShareDialog mShareDialog;
    private Bitmap mBitmap;

    public CommentShareView(Context context) {
        this(context, null);
    }

    public CommentShareView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.lay_comment_share_view, this, true);
        RecyclerView mRecyclerComment = (RecyclerView) findViewById(R.id.rv_comment);
        mRecyclerComment.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new CommentShareAdapter(context);
        mRecyclerComment.setAdapter(mAdapter);
        mShareDialog = new ShareDialog((Activity) context,-1);
    }

    public void init(String title, Comment comment) {
        if (comment == null)
            return;
        setText(R.id.tv_title, title);
        setText(R.id.tv_author, "——" + comment.getAuthor().getName());
        setText(R.id.tv_pub_date, StringUtils.formatSomeAgo(comment.getPubDate()));
        PlatfromUtil.setPlatFromString((TextView) findViewById(R.id.tv_platform), comment.getAppClient());
        mAdapter.clear();
        mAdapter.addItem(comment);
    }

    public void share() {
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
        }
        postDelayed(new Runnable() {
            @Override
            public void run() {
                mBitmap = getBitmap();
                mShareDialog.bitmap(mBitmap);
                mShareDialog.show();
            }
        }, 1000);
    }

    private void setText(int viewId, String text) {
        ((TextView) findViewById(viewId)).setText(text);
    }

    private Bitmap getBitmap() {
        return create(getChildAt(0));
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
        }
    }

    private static Bitmap create(View v) {
        int w = v.getWidth();
        int h = v.getHeight();
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
        Canvas c = new Canvas(bmp);
        c.drawColor(Color.WHITE);
        v.layout(0, 0, w, h);
        v.draw(c);
        return BitmapUtil.scaleBitmap(bmp, 1440, 2560, true);
    }

    static class CommentShareAdapter extends BaseRecyclerAdapter<Comment> {
        private RequestManager mLoader;

        CommentShareAdapter(Context context) {
            super(context, NEITHER);
            mLoader = Glide.with(context);
        }

        @Override
        protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_list_comment_share, parent, false);
            return new CommentHolder(view);
        }

        @Override
        protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Comment item, int position) {
            ((CommentHolder) holder).addComment(item);
        }

        static class CommentHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.iv_avatar)
            PortraitView mIvAvatar;

            @Bind(R.id.identityView)
            IdentityView mIdentityView;

            @Bind(R.id.tv_name)
            TextView mName;
            @Bind(R.id.tv_pub_date)
            TextView mPubDate;

            @Bind(R.id.lay_refer)
            CommentReferView mCommentReferView;

            @Bind(R.id.tv_content)
            TweetTextView mTweetTextView;

            CommentHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            @SuppressLint("DefaultLocale")
            void addComment(final Comment comment) {
                mIdentityView.setup(comment.getAuthor());
                mIvAvatar.setup(comment.getAuthor());
                String name = comment.getAuthor().getName();
                if (TextUtils.isEmpty(name))
                    name = mName.getResources().getString(R.string.martian_hint);
                mName.setText(name);
                mPubDate.setText(String.format("%s", StringUtils.formatSomeAgo(comment.getPubDate())));

                mCommentReferView.addComment(comment);

                CommentsUtil.formatHtml(mTweetTextView.getResources(), mTweetTextView, comment.getContent());
            }
        }
    }
}
