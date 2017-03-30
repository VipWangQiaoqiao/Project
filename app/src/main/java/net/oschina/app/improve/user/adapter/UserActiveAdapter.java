package net.oschina.app.improve.user.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.Active;
import net.oschina.app.improve.bean.simple.Author;
import net.oschina.app.improve.bean.simple.Origin;
import net.oschina.app.improve.utils.parser.TweetParser;
import net.oschina.app.improve.widget.IdentityView;
import net.oschina.app.improve.widget.PortraitView;
import net.oschina.app.util.StringUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by thanatos
 * on 16/7/14.
 */
public class UserActiveAdapter extends BaseRecyclerAdapter<Active> {

    /**
     * @param context Context
     */
    public UserActiveAdapter(Context context) {
        super(context, ONLY_FOOTER);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item_active, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder vh, Active item, int position) {
        ViewHolder holder = (ViewHolder) vh;

        Author author = item.getAuthor();
        holder.mIdentityView.setup(author);
        if (author == null) {
            holder.mViewNick.setText("匿名用户");
            holder.mViewPortrait.setup(0, "匿名用户", "");
        } else {
            holder.mViewPortrait.setup(author);
            holder.mViewNick.setText(item.getAuthor().getName());
        }

        holder.mViewTime.setText(StringUtils.formatSomeAgo(item.getPubDate()));

        Spannable spannable = TweetParser.getInstance().parse(mContext, item.getContent());
        holder.mViewContent.setText(spannable);

        holder.mViewTitle.setText(getWhichTitle(item.getOrigin()));

        if (item.getOrigin().getType() == Origin.ORIGIN_TYPE_TWEETS) {
            holder.mViewReply.setVisibility(View.VISIBLE);
            holder.mViewReply.setText(item.getOrigin().getDesc());
        } else {
            holder.mViewReply.setVisibility(View.GONE);
        }

    }

    private CharSequence getWhichTitle(Origin origin) {
        if (origin == null) return "更新了动态";
        String desc = "评论了%s%s:";
        String which;
        String title = "“" + origin.getDesc() + "”";
        switch (origin.getType()) {
            case Origin.ORIGIN_TYPE_LINK:
                which = "新闻";
                break;
            case Origin.ORIGIN_TYPE_SOFTWARE:
                which = "软件推荐";
                break;
            case Origin.ORIGIN_TYPE_DISCUSS:
                which = "帖子";
                break;
            case Origin.ORIGIN_TYPE_BLOG:
                which = "博客";
                break;
            case Origin.ORIGIN_TYPE_TRANSLATION:
                which = "翻译文章";
                break;
            case Origin.ORIGIN_TYPE_ACTIVE:
                which = "活动";
                break;
            case Origin.ORIGIN_TYPE_NEWS:
                which = "资讯";
                break;
            case Origin.ORIGIN_TYPE_TWEETS:
                which = "动弹";
                title = "";
                break;
            default:
                which = "文章";
                title = "";
        }
        desc = String.format(desc, which, title);
        if (title.length() == 0) return desc;
        SpannableStringBuilder builder = new SpannableStringBuilder(desc);
        int start = which.length() + 4;
        int end = start + title.length() - 2;
        ForegroundColorSpan cs = new ForegroundColorSpan(mContext.getResources().getColor(R.color.day_colorPrimary));
        builder.setSpan(cs, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return builder;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_nick)
        TextView mViewNick;
        @Bind(R.id.tv_time)
        TextView mViewTime;
        @Bind(R.id.tv_title)
        TextView mViewTitle;
        @Bind(R.id.tv_reply)
        TextView mViewReply;
        @Bind(R.id.tv_content)
        TextView mViewContent;
        @Bind(R.id.iv_portrait)
        PortraitView mViewPortrait;
        @Bind(R.id.identityView)
        IdentityView mIdentityView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
