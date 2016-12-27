package net.oschina.app.improve.detail.apply;

import net.oschina.app.improve.base.BaseRecyclerFragment;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.ApplyUser;

/**
 * Created by haibin
 * on 2016/12/27.
 */

public class ApplyFragment extends BaseRecyclerFragment<ApplyContract.Presenter, ApplyUser>
        implements ApplyContract.View {

    public static ApplyFragment newInstance() {
        return new ApplyFragment();
    }

    @Override
    protected void onItemClick(ApplyUser applyUser, int position) {

    }

    @Override
    public void showAddRelationSuccess(boolean isRelation, int strId) {

    }

    @Override
    public void showAddRelationError() {

    }

    @Override
    protected BaseRecyclerAdapter<ApplyUser> getAdapter() {
        return new ApplyAdapter(this);
    }
}
