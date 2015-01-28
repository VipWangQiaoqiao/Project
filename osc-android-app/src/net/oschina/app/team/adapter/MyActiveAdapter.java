package net.oschina.app.team.adapter;

import net.oschina.app.R;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.team.bean.TeamActive;
import net.oschina.app.widget.AvatarView;
import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Team动态界面ListView适配器 (kymjs123@gmail.com)
 * 
 * @author kymjs
 * 
 */
public class MyActiveAdapter extends ListBaseAdapter<TeamActive> {
    private final Context context;

    public MyActiveAdapter(Context cxt) {
        this.context = cxt;
    }

    static class ViewHolder {
        AvatarView img_head;
        TextView tv_name;
        TextView tv_active;
        TextView tv_content;
        LinearLayout ll_event_list;
        TextView tv_client;
        TextView tv_date;
    }

    @Override
    protected View getRealView(int position, View v, ViewGroup parent) {
        super.getRealView(position, v, parent);
        ViewHolder holder = null;
        TeamActive data = (TeamActive) mDatas.get(position);
        if (v == null || v.getTag() == null) {
            v = View.inflate(context, R.layout.item_team_dynamic, null);
            holder = new ViewHolder();
            holder.img_head = (AvatarView) v
                    .findViewById(R.id.event_listitem_userface);
            holder.tv_name = (TextView) v
                    .findViewById(R.id.event_listitem_username);
            holder.tv_active = (TextView) v
                    .findViewById(R.id.event_listitem_active);
            holder.tv_content = (TextView) v
                    .findViewById(R.id.event_listitem_content);
            holder.ll_event_list = (LinearLayout) v
                    .findViewById(R.id.event_listitem_commits_list);
            holder.tv_client = (TextView) v
                    .findViewById(R.id.event_listitem_client);
            holder.tv_date = (TextView) v
                    .findViewById(R.id.event_listitem_date);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        // 接口返回图片地址问题，需要客户端额外处理
        String imgUrl = data.getAuthor().getPortrait();
        int end = imgUrl.indexOf('?');
        if (end > 0) {
            imgUrl = imgUrl.substring(0, end);
        }
        holder.img_head.setAvatarUrl(imgUrl);
        holder.tv_name.setText(data.getAuthor().getName());
        // holder.tv_active.setText(data.getBody().getDetail());
        holder.tv_content.setText(Html.fromHtml(data.getBody().getDetail()));
        holder.tv_date.setText(data.getCreateTime());
        // holder.tv_client.setText("");
        return v;
    }
}
