package net.oschina.app.adapter.general;

import net.oschina.app.R;
import net.oschina.app.adapter.ViewHolder;
import net.oschina.app.adapter.base.BaseListAdapter;
import net.oschina.app.bean.Event;

/**
 * Created by huanghaibin
 * on 16-5-25.
 */
public class EventAdapter extends BaseListAdapter<Event> {
    public EventAdapter(Callback callback) {
        super(callback);
    }

    @Override
    protected void convert(ViewHolder vh, Event item, int position) {
        vh.setText(R.id.tv_event_title, item.getTitle());
    }

    @Override
    protected int getLayoutId(int position, Event item) {
        return R.layout.list_cell_event;
    }
}
