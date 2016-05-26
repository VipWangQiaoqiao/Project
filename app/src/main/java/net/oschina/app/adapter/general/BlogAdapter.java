package net.oschina.app.adapter.general;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
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

        TextView banner = vh.getView(R.id.tv_item_blog_banner);
        TextView title = vh.getView(R.id.tv_item_blog_title);
        TextView content = vh.getView(R.id.tv_item_blog_body);
        TextView history = vh.getView(R.id.tv_item_blog_history);
        TextView see = vh.getView(R.id.tv_item_blog_see);
        TextView answer = vh.getView(R.id.tv_item_blog_answer);
        View line = vh.getView(R.id.item_blog_line);

        if (position == 0) {
            banner.setText(item.getType() == 1 ? blogBanner[0] : blogBanner[1]);
            banner.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.bottomMargin = 43;
            params.leftMargin = 43;
            banner.setLayoutParams(params);
        } else {
            banner.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);
            params.leftMargin = 43;
            line.setLayoutParams(params);
        }
        title.setText(item.getTitle());
        content.setText(item.getBody());
        history.setText(item.getAuthor().length() > 9 ? item.getAuthor().substring(0, 9) : item.getAuthor() + "\t" + StringUtils.friendly_time(item.getPubDate()));
        see.setText(item.getViewCount() + "");
        answer.setText(item.getCommentCount() + "");

    }

    @Override
    protected int getLayoutId(int position, Blog item) {
        return R.layout.fragment_item_blog;
    }
}
