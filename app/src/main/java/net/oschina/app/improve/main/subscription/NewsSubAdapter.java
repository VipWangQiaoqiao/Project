package net.oschina.app.improve.main.subscription;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.oschina.app.OSCApplication;
import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.bean.SubTab;
import net.oschina.app.improve.bean.simple.Author;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.TDevice;

/**
 * 新版新闻订阅栏目
 * Created by haibin
 * on 2016/10/26.
 */

public class NewsSubAdapter extends BaseRecyclerAdapter<SubBean> implements BaseRecyclerAdapter.OnLoadingHeaderCallBack {
    private OSCApplication.ReadState mReadState;
    private SubTab mTab;

    public NewsSubAdapter(Context context, int mode) {
        super(context, mode);
        mReadState = OSCApplication.getReadState("sub_list");
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

        if (mReadState.already(item.getKey())) {
            vh.tv_title.setTextColor(TDevice.getColor(resources, R.color.text_desc_color));
            vh.tv_description.setTextColor(TDevice.getColor(resources, R.color.text_secondary_color));
        } else {
            vh.tv_title.setTextColor(TDevice.getColor(resources, R.color.text_title_color));
            vh.tv_description.setTextColor(TDevice.getColor(resources, R.color.text_desc_color));
        }

        vh.tv_description.setText(item.getBody());

        Author author = item.getAuthor();
        String authorName;
        if (author != null && !TextUtils.isEmpty(authorName = author.getName())) {
            authorName = authorName.trim();
            vh.tv_time.setText(String.format("@%s %s",
                    (authorName.length() > 9 ? authorName.substring(0, 9) : authorName),
                    StringUtils.formatSomeAgo(item.getPubDate().trim())));
        } else {
            vh.tv_time.setText(StringUtils.formatSomeAgo(item.getPubDate().trim()));
        }

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
        if (item.getType() == 0) {
            vh.ll_info.setVisibility(View.GONE);
        } else {
            vh.ll_info.setVisibility(View.VISIBLE);
            vh.tv_comment_count.setText(String.valueOf(item.getStatistics().getComment()));
            vh.tv_view_count.setText(String.valueOf(item.getStatistics().getView()));
        }
    }

    private static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title, tv_description, tv_time, tv_comment_count, tv_view_count;
        LinearLayout ll_title, ll_info;

        public NewsViewHolder(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_description = (TextView) itemView.findViewById(R.id.tv_description);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            ll_title = (LinearLayout) itemView.findViewById(R.id.ll_title);

            ll_info = (LinearLayout) itemView.findViewById(R.id.lay_info);
            tv_comment_count = (TextView) ll_info.findViewById(R.id.tv_info_comment);
            tv_view_count = (TextView) ll_info.findViewById(R.id.tv_info_view);
            tv_view_count.setVisibility(View.GONE);
            ll_info.findViewById(R.id.iv_info_view).setVisibility(View.GONE);
        }
    }
}
