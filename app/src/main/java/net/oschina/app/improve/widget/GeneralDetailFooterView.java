package net.oschina.app.improve.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.bean.blog.BlogDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JuQiu
 * on 16/6/12.
 */

public class GeneralDetailFooterView extends RecyclerView {
    private List<Object> objects = new ArrayList<>();
    private int mIndexOfAbout = -1;
    private int mIndexOfComments = -1;

    public GeneralDetailFooterView(Context context) {
        super(context);
    }

    public GeneralDetailFooterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GeneralDetailFooterView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void addAbout(List<BlogDetail.About> abouts) {
        if (mIndexOfAbout == -1) {
            objects.add("相关推荐");
            objects.addAll(abouts);
            mIndexOfAbout = abouts.size();
        } else {
            objects.addAll(mIndexOfAbout, abouts);
            mIndexOfAbout += abouts.size();
        }
        if (mIndexOfComments != -1) {
            mIndexOfComments = objects.size() - 1;
        }
    }

    public void addComment(List<BlogDetail.Comment> comments) {
        if (mIndexOfComments == -1) {
            objects.add("评论");
            objects.addAll(comments);
        } else {
            objects.addAll(mIndexOfComments, comments);
        }
        mIndexOfComments = objects.size() - 1;
    }

    public void init() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        setLayoutManager(layoutManager);

        setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private static class TitleViewHolder extends ViewHolder {
        private TextView mTitle;

        TitleViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.tv_blog_detail_about);
        }

        static TitleViewHolder create(ViewGroup parent) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.lay_detail_footer_title, parent, false);
            return new TitleViewHolder(v);
        }

        static void setData(ViewHolder holder, String title) {
            if (holder instanceof TitleViewHolder) {
                ((TitleViewHolder) holder).mTitle.setText(title);
            }
        }
    }

    private static class AboutViewHolder extends ViewHolder {

        AboutViewHolder(View itemView) {
            super(itemView);
        }

        static AboutViewHolder create(ViewGroup parent) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.lay_detail_footer_about, parent, false);
            return new AboutViewHolder(v);
        }

        static void setData(ViewHolder holder, BlogDetail.About title) {
            if (holder instanceof AboutViewHolder) {
                //((AboutViewHolder) holder)
            }
        }
    }

    private static class CommentViewHolder extends ViewHolder {

        CommentViewHolder(View itemView) {
            super(itemView);
        }

        static CommentViewHolder create(ViewGroup parent) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.lay_detail_footer_comment, parent, false);
            return new CommentViewHolder(v);
        }

        static void setData(ViewHolder holder, BlogDetail.Comment title) {
            if (holder instanceof CommentViewHolder) {
                //((TitleViewHolder) holder).mTitle.setText(title);
            }
        }
    }

    private Adapter<ViewHolder> mAdapter = new Adapter<ViewHolder>() {
        final static int VIEW_TYPE_TITLE = 1;
        final static int VIEW_TYPE_ABOUT = 2;
        final static int VIEW_TYPE_COMMENT = 3;

        @Override
        public int getItemViewType(int position) {
            Object object = objects.get(position);
            if (object instanceof String)
                return VIEW_TYPE_TITLE;
            else if (object instanceof BlogDetail.About)
                return VIEW_TYPE_ABOUT;
            else
                return VIEW_TYPE_COMMENT;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_TITLE)
                return TitleViewHolder.create(parent);
            else if (viewType == VIEW_TYPE_ABOUT)
                return AboutViewHolder.create(parent);
            else
                return CommentViewHolder.create(parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Object object = objects.get(position);
            if (object instanceof String) {
                TitleViewHolder.setData(holder, (String) object);
            } else if (object instanceof BlogDetail.About) {
                AboutViewHolder.setData(holder, (BlogDetail.About) object);
            } else {
                CommentViewHolder.setData(holder, (BlogDetail.Comment) object);
            }
        }

        @Override
        public int getItemCount() {
            return objects.size();
        }
    };
}
