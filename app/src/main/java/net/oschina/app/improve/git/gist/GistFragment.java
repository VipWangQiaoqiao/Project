package net.oschina.app.improve.git.gist;

import net.oschina.app.improve.base.BaseRecyclerFragment;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.git.bean.Gist;

/**
 * 代码片段
 * Created by haibin on 2017/5/10.
 */

public class GistFragment extends BaseRecyclerFragment<GistContract.Presenter, Gist> implements
        GistContract.View {

    @Override
    protected void onItemClick(Gist gist, int position) {

    }

    @Override
    protected BaseRecyclerAdapter<Gist> getAdapter() {
        return new GistAdapter(mContext);
    }
}
