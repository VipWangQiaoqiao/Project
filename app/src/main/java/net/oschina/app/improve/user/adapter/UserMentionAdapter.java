package net.oschina.app.improve.user.adapter;

import net.oschina.app.adapter.ViewHolder;
import net.oschina.app.improve.base.adapter.BaseListAdapter;
import net.oschina.app.improve.bean.Mention;

/**
 * Created by huanghaibin_dev
 * on 2016/8/16.
 */

public class UserMentionAdapter extends BaseListAdapter<Mention> {
    public UserMentionAdapter(Callback callback) {
        super(callback);
    }

    @Override
    protected void convert(ViewHolder vh, Mention item, int position) {

    }

    @Override
    protected int getLayoutId(int position, Mention item) {
        return 0;
    }
}
