package net.oschina.app.team.adapter;

import net.oschina.app.R;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 团队成员GridView适配器
 * 
 * @author kymjs (kymjs123@gmail.com)
 * 
 */
public class TeamMemberAdapter extends BaseAdapter {
    private final Context cxt;

    public TeamMemberAdapter(Context context) {
        this.cxt = context;
    }

    @Override
    public int getCount() {
        return 10;
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
    public View getView(int position, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(cxt, R.layout.item_team_member, null);
        }
        return v;
    }
}
