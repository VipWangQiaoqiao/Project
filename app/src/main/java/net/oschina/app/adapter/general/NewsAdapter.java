package net.oschina.app.adapter.general;

import net.oschina.app.R;
import net.oschina.app.adapter.ViewHolder;
import net.oschina.app.adapter.base.BaseListAdapter;
import net.oschina.app.bean.news.News;
import net.oschina.app.util.StringUtils;

/**
 * Created by huanghaibin
 * on 16-5-23.
 */
public class NewsAdapter extends BaseListAdapter<News> {
    public NewsAdapter(Callback callback) {
        super(callback);
    }

    @Override
    protected void convert(ViewHolder vh, News item, int position) {
        vh.setText(R.id.tv_title, item.getTitle());
        vh.setText(R.id.tv_description, item.getBody());
        vh.setText(R.id.tv_time, StringUtils.friendly_time(item.getPubDate()));
        vh.setText(R.id.tv_comment_count, String.valueOf(item.getCommentCount()));
        if (item.isRecommend()) {
            vh.setImage(R.id.iv_tip, R.drawable.ic_label_recommend);
            vh.setVisibility(R.id.iv_tip);
        } else {
            vh.setGone(R.id.iv_tip);
        }
    }

    @Override
    protected int getLayoutId(int position, News item) {
        return R.layout.item_list_news;
    }
}
