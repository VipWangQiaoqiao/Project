package net.oschina.app.improve.main.subscription;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseGeneralRecyclerAdapter;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.Event;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.general.fragments.EventFragment;
import net.oschina.app.util.StringUtils;

import java.util.Map;

/**
 * 新版活动栏目
 * Created by haibin
 * on 2016/10/27.
 */

public class EventSubAdapter extends BaseGeneralRecyclerAdapter<SubBean> implements BaseRecyclerAdapter.OnLoadingHeaderCallBack {
    public EventSubAdapter(Callback callback, int mode) {
        super(callback, mode);
        setOnLoadingHeaderCallBack(this);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new EventViewHolder(mInflater.inflate(R.layout.item_list_sub_event, parent, false));
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderHolder(ViewGroup parent) {
        return new HeaderViewHolder(mHeaderView);
    }

    @Override
    public void onBindHeaderHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, SubBean item, int position) {
        EventViewHolder vh = (EventViewHolder) holder;
        vh.tv_event_title.setText(item.getTitle());
        SubBean.Image image = item.getImage();
        if (image != null)
            mCallBack.getImgLoader().load(image.getHref()).into(vh.iv_event);


        vh.tv_event_title.setTextColor(
                AppContext.isOnReadedPostList(EventFragment.HISTORY_EVENT, item.getId() + "") ?
                        (mContext.getResources().getColor(R.color.count_text_color_light)) : (mContext.getResources().getColor(R.color.day_textColor)));

        Map<String, Object> extra = item.getExtra();
        if (extra != null) {
            vh.tv_event_pub_date.setText(StringUtils.getDateString(extra.get("eventStartDate").toString()));
            vh.tv_event_member.setText(extra.get("eventApplyCount") + "人参与");
        }
        switch (extra != null ? (int) extra.get("eventStatus") : 0) {//不要问我为什么这么干
            case Event.STATUS_END:
                setText(vh.tv_event_state, R.string.event_status_end, R.drawable.bg_event_end, 0x1a000000);
                setTextColor(vh.tv_event_title, mContext.getResources().getColor(R.color.light_gray));
                break;
            case Event.STATUS_ING:
                setText(vh.tv_event_state, R.string.event_status_ing, R.drawable.bg_event_ing, 0xFF24cf5f);
                break;
            case Event.STATUS_SING_UP:
                setText(vh.tv_event_state, R.string.event_status_sing_up, R.drawable.bg_event_end, 0x1a000000);
                setTextColor(vh.tv_event_title, mContext.getResources().getColor(R.color.light_gray));
                break;
        }
        int typeStr = R.string.oscsite;
        switch (extra != null ? (int) extra.get("eventType") : 0) {
            case Event.EVENT_TYPE_OSC:
                typeStr = R.string.event_type_osc;
                break;
            case Event.EVENT_TYPE_TEC:
                typeStr = R.string.event_type_tec;
                break;
            case Event.EVENT_TYPE_OTHER:
                typeStr = R.string.event_type_other;
                break;
            case Event.EVENT_TYPE_OUTSIDE:
                typeStr = R.string.event_type_outside;
                break;
        }
        vh.tv_event_type.setText(typeStr);
    }

    private void setText(TextView tv, int textRes, int bgRes, int textColor) {
        tv.setText(textRes);
        tv.setVisibility(View.VISIBLE);
        tv.setBackgroundResource(bgRes);
        tv.setTextColor(textColor);
    }

    private void setTextColor(TextView tv, int textColor) {
        tv.setTextColor(textColor);
        tv.setVisibility(View.VISIBLE);
    }

    private static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tv_event_title, tv_description, tv_event_pub_date, tv_event_member, tv_event_state, tv_event_type;
        ImageView iv_event;

        public EventViewHolder(View itemView) {
            super(itemView);
            tv_event_title = (TextView) itemView.findViewById(R.id.tv_event_title);
            tv_event_state = (TextView) itemView.findViewById(R.id.tv_event_state);
            tv_event_type = (TextView) itemView.findViewById(R.id.tv_event_type);
            tv_description = (TextView) itemView.findViewById(R.id.tv_description);
            tv_event_pub_date = (TextView) itemView.findViewById(R.id.tv_event_pub_date);
            tv_event_member = (TextView) itemView.findViewById(R.id.tv_event_member);
            iv_event = (ImageView) itemView.findViewById(R.id.iv_event);
        }
    }
}
