package net.oschina.app.improve.comment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import net.oschina.app.improve.activities.BaseBackActivity;
import net.oschina.app.improve.adapter.base.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.simple.CommentEX;
import net.oschina.app.improve.widget.RecyclerRefreshLayout;
import net.oschina.app.widget.TweetTextView;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;

public class CommentExsActivity extends BaseBackActivity {
    private long mId;
    private int mType;

    private PageBean<CommentEX> mPageBean;

    @Bind(R.id.lay_refreshLayout)
    RecyclerRefreshLayout mRefreshLayout;

    @Bind(R.id.lay_blog_detail_comment)
    RecyclerView mLayComments;

    private Adapter mAdapter;

    public static void show(Context context, long id, int type) {
        Intent intent = new Intent(context, CommentExsActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_comments;
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        mId = bundle.getLong("id");
        mType = bundle.getInt("type");
        return super.initBundle(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mLayComments.setLayoutManager(manager);

        mAdapter = new Adapter(this);
        mLayComments.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        super.initData();
        mRefreshLayout.setSuperRefreshLayoutListener(new RecyclerRefreshLayout.SuperRefreshLayoutListener() {
            @Override
            public void onRefreshing() {
                getData(true, null);
            }

            @Override
            public void onLoadMore() {
                String token = null;
                if (mPageBean != null)
                    token = mPageBean.getNextPageToken();
                getData(false, token);
            }
        });

        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
                mRefreshLayout.onRefresh();
            }
        });
    }

    private void getData(final boolean clearData, String token) {
        OSChinaApi.getComments(mId, mType, "refer", token, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onFinish() {
                super.onFinish();
                mRefreshLayout.onComplete();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<PageBean<CommentEX>>>() {
                    }.getType();

                    ResultBean<PageBean<CommentEX>> resultBean = AppContext.createGson().fromJson(responseString, type);
                    if (resultBean != null && resultBean.isSuccess()) {
                        if (resultBean.getResult() != null
                                && resultBean.getResult().getItems() != null
                                && resultBean.getResult().getItems().size() > 0) {
                            mPageBean = resultBean.getResult();
                            handleData(mPageBean.getItems(), clearData);
                            return;
                        }
                    }
                    mAdapter.setState(BaseRecyclerAdapter.STATE_NO_MORE, true);
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }

    private void handleData(List<CommentEX> comments, boolean clearData) {
        if (clearData)
            mAdapter.clear();

        mAdapter.setState(BaseRecyclerAdapter.STATE_LOADING, false);
        mAdapter.addAll(comments);
        mAdapter.notifyDataSetChanged();
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

        void setData(CommentEX comment, RequestManager imageLoader) {
            itemView.setTag(comment);

            if (comment.getAuthorPortrait() != null)
                imageLoader.load(comment.getAuthorPortrait()).error(R.drawable.widget_dface)
                        .into((mAvatar));
            else
                mAvatar.setImageResource(R.drawable.widget_dface);

            mName.setText(comment.getAuthor());
            mDate.setText(comment.getPubDate());
            CommentsUtil.formatHtml(mContent.getResources(), mContent, comment.getContent());

            mRefers.removeAllViews();
            if (comment.getRefer() != null) {
                // 最多5层
                View view = CommentsUtil.getReferLayout(LayoutInflater.from(mRefers.getContext()), comment.getRefer(), 5);
                mRefers.addView(view);
            }
        }

        CommentEX getData() {
            Object o = itemView.getTag();
            if (o != null)
                return (CommentEX) o;
            else
                return null;
        }
    }

    private class Adapter extends BaseRecyclerAdapter<CommentEX> {

        Adapter(Context context) {
            super(context, ONLY_FOOTER);
            mState = STATE_LOADING;
            setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(int position, long itemId) {
                    CommentExsActivity.this.onItemClick(getItem(position));
                }
            });
        }

        @Override
        protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.lay_blog_detail_comment, parent, false);

            final CommentHolder holder = new CommentHolder(view);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommentEX comment = holder.getData();
                    if (comment != null) {
                        onItemClick(holder.getData());
                    }
                }
            });

            return holder;
        }

        @Override
        protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, CommentEX item, int position) {
            if (holder instanceof CommentHolder) {
                CommentHolder commentHolder = (CommentHolder) holder;
                RequestManager requestManager = getImageLoader();
                if (requestManager != null)
                    commentHolder.setData(item, requestManager);
            }
        }
    }

    private void onItemClick(CommentEX comment) {
        QuestionAnswerDetailActivity.show(this, comment);
    }
}
