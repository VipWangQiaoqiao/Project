package net.oschina.app.adapter;

import java.util.ArrayList;

import net.oschina.app.bean.NotebookData;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NotebookAdapter extends BaseAdapter {
    private ArrayList<NotebookData> data;
    private final Context context;

    public NotebookAdapter(ArrayList<NotebookData> data, Context context) {
        super();
        this.data = data;
        this.context = context;
    }

    public void refurbishData(ArrayList<NotebookData> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolder {
        TextView title;
        TextView date;
        TextView time;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // ViewHolder viewHolder;
        // if (convertView == null) {
        // viewHolder = new ViewHolder();
        // convertView = LayoutInflater.from(context).inflate(
        // R.layout.notebook_item, null);
        // viewHolder.title = (TextView) convertView
        // .findViewById(R.id.text_note_title);
        // viewHolder.date = (TextView) convertView
        // .findViewById(R.id.text_note_date);
        // viewHolder.time = (TextView) convertView
        // .findViewById(R.id.text_note_time);
        // convertView.setTag(viewHolder);
        // } else {
        // viewHolder = (ViewHolder) convertView.getTag();
        // }
        //
        // viewHolder.title.setText(data.get(position).getTitle());
        // viewHolder.date.setText(data.get(position).getDate());
        // viewHolder.time.setText(data.get(position).getTime());
        return convertView;
    }

}
