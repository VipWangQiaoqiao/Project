package net.oschina.app.improve.general.adapter;

import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.widget.TextView;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.adapter.ViewHolder;
import net.oschina.app.improve.base.adapter.BaseListAdapter;
import net.oschina.app.improve.bean.News;
import net.oschina.app.improve.general.fragments.NewsFragment;
import net.oschina.app.util.StringUtils;

/**
 * Created by huanghaibin
 * on 16-5-23.
 */
public class NewsAdapter extends BaseListAdapter<News> {
    private String systemTime;

    public NewsAdapter(Callback callback) {
        super(callback);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void convert(ViewHolder vh, News item, int position) {
        //    vh.setText(R.id.tv_title, item.getTitle());
        if (AppContext.isOnReadedPostList(NewsFragment.HISTORY_NEWS, String.valueOf(item.getId()))) {
            vh.setTextColor(R.id.tv_title, mCallback.getContext().getResources().getColor(R.color.count_text_color_light));
            vh.setTextColor(R.id.tv_description, mCallback.getContext().getResources().getColor(R.color.count_text_color_light));
        } else {
            vh.setTextColor(R.id.tv_title, mCallback.getContext().getResources().getColor(R.color.blog_title_text_color_light));
            vh.setTextColor(R.id.tv_description, mCallback.getContext().getResources().getColor(R.color.ques_bt_text_color_dark));
        }

        vh.setText(R.id.tv_description, item.getBody());
        vh.setText(R.id.tv_time, StringUtils.formatSomeAgo(item.getPubDate()));
        vh.setText(R.id.tv_comment_count, String.valueOf(item.getCommentCount()));
        //vh.setText(R.id.tv_view_count, String.valueOf(item.getViewCount()));

        TextView title = vh.getView(R.id.tv_title);
        if (StringUtils.isSameDay(systemTime, item.getPubDate())) {

            String text = "[icon] " + item.getTitle();
            Drawable drawable = mCallback.getContext().getResources().getDrawable(R.mipmap.ic_label_today);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);

            SpannableString spannable = new SpannableString(text);
            spannable.setSpan(imageSpan, 0, 6, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            title.setText(spannable);
            title.setTextSize(16.0f);
        } else {
            title.setText(item.getTitle());
        }
    }

    @Override
    protected int getLayoutId(int position, News item) {
        return R.layout.item_list_news;
    }

    public void setSystemTime(String systemTime) {
        this.systemTime = systemTime;
    }
}
