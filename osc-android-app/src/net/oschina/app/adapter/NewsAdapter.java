package net.oschina.app.adapter;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.bean.News;
import net.oschina.app.bean.NewsList;
import net.oschina.app.util.StringUtils;
import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class NewsAdapter extends ListBaseAdapter<News> {

    @SuppressLint("InflateParams")
    @Override
    protected View getRealView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    R.layout.list_cell_news, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        News news = mDatas.get(position);
        vh.title.setText(news.getTitle());

        if (AppContext.isOnReadedPostList(NewsList.PREF_READED_NEWS_LIST,
                news.getId() + "")) {
            vh.title.setTextColor(parent.getContext().getResources()
                    .getColor(R.color.main_gray));
        } else {
            vh.title.setTextColor(parent.getContext().getResources()
                    .getColor(R.color.main_black));
        }

        String description = news.getBody();
        vh.description.setVisibility(View.GONE);
        if (description != null && !StringUtils.isEmpty(description)) {
            vh.description.setVisibility(View.VISIBLE);
            vh.description.setText(description.trim());
        }

        vh.source.setText(news.getAuthor());
        vh.time.setText(StringUtils.friendly_time(news.getPubDate()));
        if (StringUtils.isToday(news.getPubDate())) {
            vh.tip.setVisibility(View.VISIBLE);
        } else {
            vh.tip.setVisibility(View.GONE);
        }
        //
        // if (hasExternalLink(news)) {
        // vh.link.setVisibility(View.VISIBLE);
        // } else {
        // vh.link.setVisibility(View.GONE);
        // }

        vh.comment_count.setText(news.getCommentCount() + "");

        return convertView;
    }

    static class ViewHolder {
        @InjectView(R.id.tv_title)
        TextView title;
        @InjectView(R.id.tv_description)
        TextView description;
        @InjectView(R.id.tv_source)
        TextView source;
        @InjectView(R.id.tv_time)
        TextView time;
        @InjectView(R.id.tv_comment_count)
        TextView comment_count;
        @InjectView(R.id.iv_tip)
        ImageView tip;
        @InjectView(R.id.iv_link)
        ImageView link;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
