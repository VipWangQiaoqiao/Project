package net.oschina.app.team.adapter;

import java.util.ArrayList;
import java.util.List;

import net.oschina.app.R;
import net.oschina.app.team.bean.Detail;
import net.oschina.app.team.bean.Detail.DayData;
import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DiaryDetailAdapter extends BaseAdapter {

    private final Detail data;
    private final Context cxt;
    private final List<String> datas;

    public DiaryDetailAdapter(Context cxt, Detail datas) {
        this.cxt = cxt;
        this.data = datas;
        this.datas = new ArrayList<String>(20);
    }

    @Override
    public int getCount() {
        if (data == null) {
            return 0;
        }
        int count = 0;
        count += isNull(data.getSun());
        count += isNull(data.getMon());
        count += isNull(data.getTue());
        count += isNull(data.getWed());
        count += isNull(data.getThu());
        count += isNull(data.getFri());
        count += isNull(data.getSat());
        return count;
    }

    private int isNull(DayData data) {
        if (data == null) {
            return 0;
        }
        datas.addAll(data.getList());
        return data.getList().size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolder {
        TextView week;
        TextView content;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(cxt, R.layout.list_cell_diary_detail,
                    null);
            holder.content = (TextView) convertView
                    .findViewById(R.id.item_diary_detail_content);
            holder.week = (TextView) convertView
                    .findViewById(R.id.item_diary_detail_week);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.content.setText(Html.fromHtml(datas.get(position).toString()));
        return convertView;
    }
}
