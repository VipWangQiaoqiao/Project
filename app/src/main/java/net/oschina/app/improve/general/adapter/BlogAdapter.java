package net.oschina.app.improve.general.adapter;

import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.adapter.ViewHolder;
import net.oschina.app.improve.base.adapter.BaseListAdapter;
import net.oschina.app.improve.bean.Blog;
import net.oschina.app.improve.general.fragments.BlogFragment;
import net.oschina.app.improve.user.fragments.UserBlogFragment;
import net.oschina.app.util.StringUtils;


/**
 * Created by fei on 2016/5/24.
 * desc:
 */
public class BlogAdapter extends BaseListAdapter<Blog> {

    private boolean isUserBlog;
    private int actionPosition = 0;

    public void setActionPosition(int actionPosition) {
        this.actionPosition = actionPosition;
    }

    public BlogAdapter(Callback callback) {
        super(callback);
    }

    public void setUserBlog(boolean userBlog) {
        isUserBlog = userBlog;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void convert(ViewHolder vh, Blog item, int position) {

        TextView title = vh.getView(R.id.tv_item_blog_title);
        TextView content = vh.getView(R.id.tv_item_blog_body);
        TextView history = vh.getView(R.id.tv_item_blog_history);
        TextView see = vh.getView(R.id.tv_info_view);
        TextView answer = vh.getView(R.id.tv_info_comment);

        String text = "";

        SpannableStringBuilder spannable = new SpannableStringBuilder(text);

        if (item.isOriginal()) {
            spannable.append("[icon] ");
            Drawable originate = mCallback.getContext().getResources().getDrawable(R.mipmap.ic_label_originate);
            if (originate != null) {
                originate.setBounds(0, 0, originate.getIntrinsicWidth(), originate.getIntrinsicHeight());
            }
            ImageSpan imageSpan = new ImageSpan(originate, ImageSpan.ALIGN_BOTTOM);
            spannable.setSpan(imageSpan, 0, 6, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        if (item.isRecommend()) {
            spannable.append("[icon] ");
            Drawable recommend = mCallback.getContext().getResources().getDrawable(R.mipmap.ic_label_recommend);
            if (recommend != null) {
                recommend.setBounds(0, 0, recommend.getIntrinsicWidth(), recommend.getIntrinsicHeight());
            }
            ImageSpan imageSpan = new ImageSpan(recommend, ImageSpan.ALIGN_BOTTOM);
            if (item.isOriginal()) {
                spannable.setSpan(imageSpan, 7, 13, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            } else {
                spannable.setSpan(imageSpan, 0, 6, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }
        }

        title.setText(spannable.append(item.getTitle()));

        String body = item.getBody();
        if (!TextUtils.isEmpty(body)) {
            body = body.trim();
            if (!TextUtils.isEmpty(body)) {
                content.setText(body);
                content.setVisibility(View.VISIBLE);
            } else {
                content.setVisibility(View.GONE);
            }
        }

        String cacheName = verifyFileName();

        if (isUserBlog) {
            cacheName = UserBlogFragment.HISTORY_BLOG;
        }

        if (AppContext.isOnReadedPostList(cacheName, item.getId() + "")) {
            title.setTextColor(mCallback.getContext().getResources().getColor(R.color.count_text_color_light));
            content.setTextColor(mCallback.getContext().getResources().getColor(R.color.count_text_color_light));
        } else {
            title.setTextColor(mCallback.getContext().getResources().getColor(R.color.blog_title_text_color_light));
            content.setTextColor(mCallback.getContext().getResources().getColor(R.color.ques_bt_text_color_dark));
        }

        String author = item.getAuthor();
        if (!TextUtils.isEmpty(author)) {
            author = author.trim();
            history.setText((author.length() > 9 ? author.substring(0, 9) : author) +
                    "  " + StringUtils.formatSomeAgo(item.getPubDate().trim()));
        }

        see.setText(String.valueOf(item.getViewCount()));
        answer.setText(String.valueOf(item.getCommentCount()));
    }

    @Override
    protected int getLayoutId(int position, Blog item) {
        return /**item.getViewType() == Blog.VIEW_TYPE_DATA ? **/R.layout.fragment_item_blog; //: R.layout.fragment_item_blog_line;
    }


    private String verifyFileName() {
        switch (actionPosition) {
            case 0:
                return BlogFragment.BLOG_RECOMMEND;
            case 1:
                return BlogFragment.BLOG_HEAT;
            case 2:
                return BlogFragment.BLOG_NORMAL;
            default:
                return BlogFragment.BLOG_RECOMMEND;
        }
    }

//    @Override
//    public int getItemViewType(int position) {
//        List<Blog> datas = getDatas();
//        return datas.get(position).getViewType();
//    }

   /* @Override
    public int getViewTypeCount() {
        return 3;
    }*/
}
