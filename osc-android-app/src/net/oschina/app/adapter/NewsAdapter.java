package net.oschina.app.adapter;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.bean.News;
import net.oschina.app.bean.NewsList;
import net.oschina.app.util.StringUtil;
import net.oschina.app.util.URLsUtils;

import org.kymjs.kjframe.utils.StringUtils;

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
        if (description != null && !StringUtil.isEmpty(description)) {
            vh.description.setVisibility(View.VISIBLE);
            vh.description.setText(description.trim());
        }

        vh.source.setText(news.getAuthor());
        vh.time.setText(StringUtil.friendly_time(news.getPubDate()));
        if (StringUtil.isToday(news.getPubDate())) {
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

    private boolean hasExternalLink(News data) {
        // if (!StringUtils.isEmpty(data.getNewType().getEventUrl())) {
        // return false;
        // }
        //
        // if (StringUtils.isEmpty(data.getUrl())) {
        // switch (data.getNewType().getType()) {
        // case News.NEWSTYPE_NEWS:
        // case News.NEWSTYPE_SOFTWARE:
        // case News.NEWSTYPE_POST:
        // case News.NEWSTYPE_BLOG:
        // return false;
        // default:
        // return true;
        // }
        // }
        // return true;
        if (!StringUtils.isEmpty(data.getUrl())) {
            return URLsUtils.parseURL(data.getUrl()) == null;
        } else {
            return false;
        }
    }
}
