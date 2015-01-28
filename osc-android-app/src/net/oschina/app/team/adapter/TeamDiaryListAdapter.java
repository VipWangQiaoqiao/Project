package net.oschina.app.team.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 周报的ListView适配器
 * 
 * @author kymjs
 * 
 */
public class TeamDiaryListAdapter extends BaseAdapter {
    private final Context cxt;

    public TeamDiaryListAdapter(Context cxt) {
        this.cxt = cxt;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // list_cell_team_diary
        return null;
    }
}
