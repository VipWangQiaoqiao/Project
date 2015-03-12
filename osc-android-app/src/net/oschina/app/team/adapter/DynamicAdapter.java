package net.oschina.app.team.adapter;

import net.oschina.app.R;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.team.bean.TeamActive;
import net.oschina.app.util.StringUtils;
import net.oschina.app.widget.AvatarView;
import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Team动态界面ListView适配器 (kymjs123@gmail.com)
 * 
 * @author kymjs (https://github.com/kymjs)
 * 
 */
public class DynamicAdapter extends ListBaseAdapter<TeamActive> {
    private final Context context;

    public DynamicAdapter(Context cxt) {
        this.context = cxt;
    }

    static class ViewHolder {
        AvatarView img_head;
        TextView tv_name;
        TextView tv_content;
        TextView tv_client;
        TextView tv_date;
    }

    @Override
    protected View getRealView(int position, View v, ViewGroup parent) {
        super.getRealView(position, v, parent);
        ViewHolder holder = null;
        TeamActive data = mDatas.get(position);
        if (v == null || v.getTag() == null) {
            v = View.inflate(context, R.layout.item_team_dynamic, null);
            holder = new ViewHolder();
            holder.img_head = (AvatarView) v
                    .findViewById(R.id.event_listitem_userface);
            holder.tv_name = (TextView) v
                    .findViewById(R.id.event_listitem_username);
            holder.tv_content = (TextView) v
                    .findViewById(R.id.event_listitem_content);
            holder.tv_client = (TextView) v
                    .findViewById(R.id.event_listitem_client);
            holder.tv_date = (TextView) v
                    .findViewById(R.id.event_listitem_date);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        holder.img_head.setAvatarUrl(data.getAuthor().getPortrait());
        holder.tv_name.setText(data.getAuthor().getName());
        holder.tv_content.setText(stripTags(data.getBody().getTitle()));
        holder.tv_content.setMaxLines(3);
        holder.tv_date.setText(StringUtils.friendly_time(data.getCreateTime()));
        // holder.tv_client.setText("");
        return v;
    }

    /**
     * 移除字符串中的Html标签
     * 
     * @author kymjs (https://github.com/kymjs)
     * @param pHTMLString
     * @return
     */
    public static Spanned stripTags(final String pHTMLString) {
        // String str = pHTMLString.replaceAll("\\<.*?>", "");
        String str = pHTMLString.replaceAll("\\t*", "");
        str = str.replaceAll("<\\s*img\\s+([^>]*)\\s*>", "[表情]");
        return Html.fromHtml(str);
    }

    @Override
    public TeamActive getItem(int arg0) {
        super.getItem(arg0);
        return mDatas.get(arg0);
    }
}
