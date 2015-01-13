package net.oschina.app.team.adapter;

import net.oschina.app.R;
import net.oschina.app.base.ListBaseAdapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class DynamicAdapter extends ListBaseAdapter {
    private final Context context;

    public DynamicAdapter(Context cxt) {
        this.context = cxt;
    }

    @Override
    protected View getRealView(int position, View v, ViewGroup parent) {
        super.getRealView(position, v, parent);

        if (v == null) {
            v = View.inflate(context, R.layout.myselfevent_listitem, null);
        }
        return v;
    }
}
