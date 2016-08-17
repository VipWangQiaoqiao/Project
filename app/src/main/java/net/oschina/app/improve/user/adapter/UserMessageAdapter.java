package net.oschina.app.improve.user.adapter;

import net.oschina.app.adapter.ViewHolder;
import net.oschina.app.improve.base.adapter.BaseListAdapter;
import net.oschina.app.improve.bean.Message;

/**
 * Created by huanghaibin_dev on 2016/8/16.
 */

public class UserMessageAdapter extends BaseListAdapter<Message> {
    public UserMessageAdapter(Callback callback) {
        super(callback);
    }

    @Override
    protected void convert(ViewHolder vh, Message item, int position) {

    }

    @Override
    protected int getLayoutId(int position, Message item) {
        return 0;
    }
}
