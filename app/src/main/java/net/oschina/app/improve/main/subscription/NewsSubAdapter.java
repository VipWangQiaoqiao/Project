package net.oschina.app.improve.main.subscription;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.bean.SubTab;
import net.oschina.app.util.StringUtils;
import net.qiujuer.genius.ui.compat.UiCompat;

/**
 * 新版新闻订阅栏目
 * Created by haibin
 * on 2016/10/26.
 */

public class NewsSubAdapter extends BaseRecyclerAdapter<SubBean> implements BaseRecyclerAdapter.OnLoadingHeaderCallBack {

    private SubTab mTab;

    public NewsSubAdapter(Context context, int mode) {
        super(context, mode);
        setOnLoadingHeaderCallBack(this);
    }

    public void setTab(SubTab tab) {
        this.mTab = tab;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderHolder(ViewGroup parent) {
        return new HeaderViewHolder(mHeaderView);
    }

    @Override
    public void onBindHeaderHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new NewsViewHolder(mInflater.inflate(R.layout.item_list_sub_news, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, SubBean item, int position) {
        NewsViewHolder vh = (NewsViewHolder) holder;

        Resources resources = mContext.getResources();

        if (AppContext.isOnReadedPostList("sub_list", String.valueOf(item.getId()))) {
            vh.tv_title.setTextColor(UiCompat.getColor(resources, R.color.text_desc_color));
            vh.tv_description.setTextColor(UiCompat.getColor(resources, R.color.text_secondary_color));
        } else {
            vh.tv_title.setTextColor(UiCompat.getColor(resources, R.color.text_title_color));
            vh.tv_description.setTextColor(UiCompat.getColor(resources, R.color.text_desc_color));
        }


        vh.tv_description.setText(item.getBody());
        vh.tv_time.setText(StringUtils.formatSomeAgo(item.getPubDate()));
        vh.tv_comment_count.setText(String.valueOf(item.getStatistics().getComment()));

        if (StringUtils.isSameDay(mSystemTime, item.getPubDate()) && mTab.getSubtype() != 2 && item.getType() != 7) {

            String text = "[icon] " + item.getTitle();
            Drawable drawable = resources.getDrawable(R.mipmap.ic_label_today);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);

            SpannableString spannable = new SpannableString(text);
            spannable.setSpan(imageSpan, 0, 6, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            vh.tv_title.setText(spannable);
            vh.tv_title.setTextSize(16.0f);
        } else {
            vh.tv_title.setText(item.getTitle());
        }
        if (item.getType() == 7 || item.getType() == 4 || item.getType() == 1 || (mTab.getSubtype() == 1 && item.getType() == 6)) {
            vh.iv_comment.setVisibility(View.GONE);
            vh.tv_comment_count.setVisibility(View.GONE);
        }
    }

    private static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title, tv_description, tv_time, tv_comment_count;
        LinearLayout ll_title;
        ImageView iv_comment;

        public NewsViewHolder(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_description = (TextView) itemView.findViewById(R.id.tv_description);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_comment_count = (TextView) itemView.findViewById(R.id.tv_comment_count);
            ll_title = (LinearLayout) itemView.findViewById(R.id.ll_title);
            iv_comment = (ImageView) itemView.findViewById(R.id.iv_info_comment);
        }
    }
}
