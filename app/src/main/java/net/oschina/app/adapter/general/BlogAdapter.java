package net.oschina.app.adapter.general;

import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.adapter.ViewHolder;
import net.oschina.app.adapter.base.BaseListAdapter;
import net.oschina.app.bean.blog.Blog;
import net.oschina.app.util.StringUtils;


/**
 * Created by fei on 2016/5/24.
 * desc:
 */
public class BlogAdapter extends BaseListAdapter<Blog> {

    String[] blogBanner = null;

    public BlogAdapter(Callback callback) {
        super(callback);
        this.blogBanner = callback.getContext().getResources().getStringArray(R.array.blog_item);
    }

    @Override
    protected void convert(ViewHolder vh, Blog item, int position) {
        if (item.getViewType() == Blog.VIEW_TYPE_DATA) {
            TextView banner = vh.getView(R.id.tv_item_blog_banner);
            TextView title = vh.getView(R.id.tv_item_blog_title);
            TextView content = vh.getView(R.id.tv_item_blog_body);
            TextView history = vh.getView(R.id.tv_item_blog_history);
            TextView see = vh.getView(R.id.tv_item_blog_see);
            TextView answer = vh.getView(R.id.tv_item_blog_answer);

            if (position == 0) {
                banner.setText(item.getType() == 1 ? blogBanner[0] : blogBanner[1]);
            } else {
                banner.setText("");
            }
            title.setText(item.getTitle());
            content.setText(item.getBody());
            history.setText(item.getAuthor() + "\t" + StringUtils.friendly_time(item.getPubDate()));
            see.setText(item.getViewCount() + "");
            answer.setText(item.getCommentCount() + "");
        } else if (item.getViewType() == Blog.VIEW_TYPE_TITLE_HEAT) {
            vh.setText(R.id.tv_item_blog_title, R.string.blog_list_title_heat);
        } else {
            vh.setText(R.id.tv_item_blog_title, R.string.blog_list_title_normal);
        }
    }

    @Override
    protected int getLayoutId(int position, Blog item) {
        if (item.getViewType() == Blog.VIEW_TYPE_DATA)
            return R.layout.fragment_item_blog;
        else
            return R.layout.item_list_title;
    }
}
