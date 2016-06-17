package net.oschina.app.improve.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
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
import net.oschina.app.emoji.InputHelper;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.simple.Comment;
import net.oschina.app.widget.MyLinkMovementMethod;
import net.oschina.app.widget.MyURLSpan;
import net.oschina.app.widget.TweetTextView;
import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.drawable.shape.BorderShape;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by JuQiu
 * on 16/6/12.
 */

public class DetailCommentViewNew extends LinearLayout {
    private RecyclerView mRecyclerView;
    private Adapter mAdapter;
    private OnCommentClickListener mListener;
    private RequestManager mImageLoader;
    //private RecyclerRefreshLayout mRefreshLayout;

    public DetailCommentViewNew(Context context) {
        super(context);
        init();
    }

    public DetailCommentViewNew(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DetailCommentViewNew(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.lay_detail_comment_layout_new, this, true);

        mRecyclerView = (RecyclerView) findViewById(R.id.lay_blog_detail_comment);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(manager);

        mAdapter = new Adapter();
        mRecyclerView.setAdapter(mAdapter);

        //mRefreshLayout = (RecyclerRefreshLayout) findViewById(R.id.refreshLayout);
    }

    private static class CommentHolder extends RecyclerView.ViewHolder {
        private ImageView mAvatar;
        private TextView mName;
        private TextView mDate;
        private TweetTextView mContent;
        private LinearLayout mRefers;

        CommentHolder(View itemView) {
            super(itemView);

            mAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
            mName = (TextView) itemView.findViewById(R.id.tv_name);
            mDate = (TextView) itemView.findViewById(R.id.tv_pub_date);

            mContent = ((TweetTextView) itemView.findViewById(R.id.tv_content));
            mRefers = ((LinearLayout) itemView.findViewById(R.id.lay_refer));
        }

        void setData(Comment comment, RequestManager imageLoader) {
            itemView.setTag(comment);

            if (comment.getAuthorPortrait() != null)
                imageLoader.load(comment.getAuthorPortrait()).error(R.drawable.widget_dface)
                        .into((mAvatar));
            else
                mAvatar.setImageResource(R.drawable.widget_dface);

            mName.setText(comment.getAuthor());
            mDate.setText(comment.getPubDate());
            formatHtml(mContent.getResources(), mContent, comment.getContent());

            //lay.findViewById(R.id.line).setVisibility(View.INVISIBLE);

            mRefers.removeAllViews();
            if (comment.getRefer() != null) {
                // 最多5层
                View view = getReferLayout(mRefers.getContext(), comment.getRefer(), LayoutInflater.from(mRefers.getContext()), 5);
                mRefers.addView(view);
            }
        }

        Comment getData() {
            Object o = itemView.getTag();
            if (o != null)
                return (Comment) o;
            else
                return null;
        }
    }


    private class Adapter extends RecyclerView.Adapter<CommentHolder> {
        List<Comment> mData = new ArrayList<>();

        @Override
        public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.lay_blog_detail_comment, parent, false);

            final CommentHolder holder = new CommentHolder(view);
            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Comment t = holder.getData();
                    if (t != null) {
                        onItemClick(holder.getData());
                    }
                }
            });

            return holder;
        }

        @Override
        public void onBindViewHolder(CommentHolder holder, int position) {
            RequestManager requestManager = mImageLoader;
            if (requestManager != null)
                holder.setData(mData.get(position), requestManager);
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }

    public void show(long id, int type, RequestManager imageLoader) {
        mImageLoader = imageLoader;
        OSChinaApi.getComments(id, type, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<PageBean<Comment>>>() {
                    }.getType();

                    ResultBean<PageBean<Comment>> resultBean = AppContext.createGson().fromJson(responseString, type);
                    if (resultBean != null && resultBean.isSuccess()) {
                        handleData(resultBean.getResult());
                        return;
                    }
                    //showError(EmptyLayout.NODATA);
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }

    private void handleData(PageBean<Comment> bean) {
        if (bean != null && bean.getItems() != null) {
            mAdapter.mData.addAll(bean.getItems());
            mAdapter.notifyDataSetChanged();
            //mRefreshLayout.onComplete();
        }
    }


    @SuppressWarnings("deprecation")
    private static View getReferLayout(Context context, Comment.Refer refer, LayoutInflater inflater, int count) {
        @SuppressLint("InflateParams") ViewGroup lay = (ViewGroup) inflater.inflate(R.layout.lay_blog_detail_comment_refer, null, false);
        ShapeDrawable drawable = new ShapeDrawable(new BorderShape(new RectF(Ui.dipToPx(context, 1), 0, 0, 0)));
        drawable.getPaint().setColor(0xffd7d6da);
        lay.findViewById(R.id.lay_blog_detail_comment_refer).setBackgroundDrawable(drawable);

        TextView textView = ((TextView) lay.findViewById(R.id.tv_blog_detail_comment_refer));
        drawable = new ShapeDrawable(new BorderShape(new RectF(0, 0, 0, 1)));
        drawable.getPaint().setColor(0xffd7d6da);
        textView.setBackgroundDrawable(drawable);

        formatHtml(context.getResources(), textView, refer.author + ":<br>" + refer.content);


        if (refer.refer != null && (--count) > 0) {
            View view = getReferLayout(context, refer.refer, inflater, count);
            lay.addView(view, lay.indexOfChild(textView));
        }

        return lay;
    }

    private static void formatHtml(Resources resources, TextView textView, String str) {
        textView.setMovementMethod(MyLinkMovementMethod.a());
        textView.setFocusable(false);
        textView.setLongClickable(false);

        if (textView instanceof TweetTextView) {
            ((TweetTextView) textView).setDispatchToParent(true);
        }

        str = TweetTextView.modifyPath(str);
        Spanned span = Html.fromHtml(str);
        span = InputHelper.displayEmoji(resources, span.toString());
        textView.setText(span);
        MyURLSpan.parseLinkText(textView, span);
    }

    public void setItemClickListener(OnCommentClickListener listener) {
        mListener = listener;
    }

    private void onItemClick(Comment t) {
        OnCommentClickListener listener = mListener;
        if (listener != null)
            listener.onClick(t);
    }

    public interface OnCommentClickListener {
        void onClick(Comment about);
    }
}
