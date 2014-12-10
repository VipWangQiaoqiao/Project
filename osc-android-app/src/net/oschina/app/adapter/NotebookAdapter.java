package net.oschina.app.adapter;

import java.util.ArrayList;

import net.oschina.app.R;
import net.oschina.app.bean.NotebookData;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 便签列表适配器
 * 
 * @author kymjs
 * 
 */
public class NotebookAdapter extends BaseAdapter {
    private ArrayList<NotebookData> datas;
    private final Context context;

    public NotebookAdapter(Context context, ArrayList<NotebookData> datas) {
        super();
        this.datas = datas;
        this.context = context;
    }

    public void refurbishData(ArrayList<NotebookData> data) {
        this.datas = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolder {
        TextView title;
        TextView date;
        TextView time;
        TextView how_long;
        View root;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder holder;
        if (v == null) {
            holder = new ViewHolder();
            v = View.inflate(context, R.layout.item_notebook, null);
            holder.root = v.findViewById(R.id.item_root_layout);
            holder.title = (TextView) v.findViewById(R.id.item_note_tv_title);
            holder.date = (TextView) v.findViewById(R.id.item_note_tv_date);
            holder.time = (TextView) v.findViewById(R.id.item_note_tv_time);
            holder.how_long = (TextView) v
                    .findViewById(R.id.item_note_tv_howlong);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        holder.root.setBackgroundColor(datas.get(position).getColor());
        holder.time.setText(datas.get(position).getTime());
        holder.title.setText(datas.get(position).getContent());
        holder.date.setText(datas.get(position).getDate());
        holder.how_long.setText("13" + "天前");
        return v;
    }
}
