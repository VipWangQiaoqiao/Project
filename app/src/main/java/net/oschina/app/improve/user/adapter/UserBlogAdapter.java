package net.oschina.app.improve.user.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.Blog;
import net.oschina.app.util.StringUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by thanatos on 16/8/17.
 */
public class UserBlogAdapter extends BaseRecyclerAdapter<Blog> {

    public UserBlogAdapter(Context context, int mode) {
        super(context, mode);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.fragment_item_blog, parent, false));
    }

    @SuppressWarnings("all")
    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder h, Blog item, int position) {
        ViewHolder holder = (ViewHolder) h;

        SpannableStringBuilder builder = new SpannableStringBuilder();
        if (item.isOriginal()) {
            builder.append("[icon] ");
            Drawable originate = mContext.getResources().getDrawable(R.mipmap.ic_label_originate);
            originate.setBounds(0, 0, originate.getIntrinsicWidth(), originate.getIntrinsicHeight());
            ImageSpan imageSpan = new ImageSpan(originate, ImageSpan.ALIGN_BOTTOM);
            builder.setSpan(imageSpan, 0, 6, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        if (item.isRecommend()) {
            int start = builder.length();
            builder.append("[icon] ");
            Drawable originate = mContext.getResources().getDrawable(R.mipmap.ic_label_recommend);
            originate.setBounds(0, 0, originate.getIntrinsicWidth(), originate.getIntrinsicHeight());
            ImageSpan imageSpan = new ImageSpan(originate, ImageSpan.ALIGN_BOTTOM);
            builder.setSpan(imageSpan, start, start + 6, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        builder.append(item.getTitle());
        holder.mViewTitle.setText(builder);

        holder.mViewContent.setText(item.getBody());
        String nick = item.getAuthor();
        holder.mViewHistory.setText(nick.length() > 9 ? nick.substring(0, 9) : nick + " " + StringUtils.formatSomeAgo(item.getPubDate()));
        holder.mViewInfoCmm.setText(String.valueOf(item.getCommentCount()));
        holder.mViewInfoVisual.setText(String.valueOf(item.getViewCount()));

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_item_blog_title)
        TextView mViewTitle;
        @Bind(R.id.tv_item_blog_body)
        TextView mViewContent;
        @Bind(R.id.tv_item_blog_history)
        TextView mViewHistory;
        @Bind(R.id.tv_info_view)
        TextView mViewInfoVisual;
        @Bind(R.id.tv_info_comment)
        TextView mViewInfoCmm;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
