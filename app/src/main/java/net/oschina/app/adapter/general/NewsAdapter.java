package net.oschina.app.adapter.general;

import net.oschina.app.R;
import net.oschina.app.adapter.ViewHolder;
import net.oschina.app.adapter.base.BaseListAdapter;
import net.oschina.app.bean.News;

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
        vh.setText(R.id.tv_time, item.getPubDate());
        vh.setText(R.id.tv_comment_count, String.valueOf(item.getCommentCount()));
    }

    @Override
    protected int getLayoutId(int position, News item) {
        return R.layout.item_list_news;
    }
}
