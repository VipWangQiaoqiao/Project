package net.oschina.app.adapter;

import java.util.ArrayList;

import net.oschina.app.R;
import net.oschina.app.bean.NotebookData;
import net.oschina.app.fragment.NoteEditFragment;
import net.oschina.app.util.DensityUtils;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

/**
 * 便签列表适配器
 * 
 * @author kymjs
 * 
 */
public class NotebookAdapter extends BaseAdapter {
    private ArrayList<NotebookData> datas;
    private final Activity aty;

    public NotebookAdapter(Activity aty, ArrayList<NotebookData> datas) {
        super();
        this.datas = datas;
        this.aty = aty;
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
        TextView date;
        ImageView state;
        ImageView thumbtack;
        View titleBar;
        TextView content;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder holder;
        if (v == null) {
            holder = new ViewHolder();
            v = View.inflate(aty, R.layout.item_notebook, null);
            holder.titleBar = v.findViewById(R.id.item_note_titlebar);
            holder.date = (TextView) v.findViewById(R.id.item_note_tv_date);
            holder.state = (ImageView) v.findViewById(R.id.item_note_img_state);
            holder.thumbtack = (ImageView) v
                    .findViewById(R.id.item_note_img_thumbtack);
            holder.content = (TextView) v.findViewById(R.id.item_note_content);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        RelativeLayout.LayoutParams params = (LayoutParams) holder.content
                .getLayoutParams();
        params.width = DensityUtils.getScreenW(aty) / 2;
        params.height = (int) (params.width - aty.getResources().getDimension(
                R.dimen.space_35));
        holder.content.setLayoutParams(params);

        holder.titleBar.setBackgroundColor(NoteEditFragment.sTitleBackGrounds[datas.get(
                position).getColor()]);
        holder.date.setText(datas.get(position).getDate());
        // holder.state.setImageResource(resId);
        holder.thumbtack.setImageResource(NoteEditFragment.sThumbtackImgs[datas.get(
                position).getColor()]);
        holder.content.setText(datas.get(position).getContent());
        holder.content.setBackgroundColor(NoteEditFragment.sBackGrounds[datas.get(
                position).getColor()]);
        return v;
    }
}
