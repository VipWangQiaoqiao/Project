package net.oschina.app.adapter.general;

import net.oschina.app.R;
import net.oschina.app.adapter.ViewHolder;
import net.oschina.app.adapter.base.BaseListAdapter;
import net.oschina.app.bean.Active;

/**
 * Created by huanghaibin
 * on 16-5-23.
 */
public class ActiveAdapter extends BaseListAdapter<Active> {
    public ActiveAdapter(Callback callback) {
        super(callback);
    }

    @Override
    protected void convert(ViewHolder vh, Active item, int position) {
    }

    @Override
    protected int getLayoutId(int position, Active item) {
        return R.layout.item_list_active;
    }
}
