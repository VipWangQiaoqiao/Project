package net.oschina.app.team.adapter;

import java.util.ArrayList;
import java.util.List;

import net.oschina.app.R;
import net.oschina.app.team.bean.Team;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 选择团队列表的适配器
 * 
 * @author kymjs (kymjs123@gmail.com)
 * 
 */
public class SelectTeamAdapter extends BaseAdapter {

    private final Context cxt;
    private final List<Team> datas;

    public SelectTeamAdapter(Context context, List<Team> datas) {
        this.cxt = context;
        this.datas = datas;
        if (datas == null) {
            datas = new ArrayList<Team>(1);
        }
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private static class ViewHolder {
        TextView tv_name;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder holder = null;
        if (v == null) {
            holder = new ViewHolder();
            v = View.inflate(cxt, R.layout.item_team_select, null);
            holder.tv_name = (TextView) v
                    .findViewById(R.id.item_select_team_name);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        holder.tv_name.setText(datas.get(position).getName());
        return v;
    }
}
