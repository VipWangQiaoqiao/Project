package net.oschina.app.improve.git.gist;

import net.oschina.app.improve.base.BaseRecyclerFragment;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.git.bean.Gist;
import net.oschina.app.improve.git.gist.detail.GistDetailActivity;

/**
 * 代码片段
 * Created by haibin on 2017/5/10.
 */

public class GistFragment extends BaseRecyclerFragment<GistContract.Presenter, Gist> implements
        GistContract.View {

    static GistFragment newInstance() {
        return new GistFragment();
    }

    @Override
    protected void onItemClick(Gist gist, int position) {
        GistDetailActivity.show(mContext, gist);
    }

    @Override
    protected BaseRecyclerAdapter<Gist> getAdapter() {
        return new GistAdapter(mContext);
    }
}
