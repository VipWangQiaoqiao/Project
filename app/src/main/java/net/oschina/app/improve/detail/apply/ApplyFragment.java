package net.oschina.app.improve.detail.apply;

import android.view.View;

import net.oschina.app.improve.base.BaseRecyclerFragment;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.ApplyUser;
import net.oschina.app.improve.widget.SimplexToast;

/**
 * Created by haibin
 * on 2016/12/27.
 */

public class ApplyFragment extends BaseRecyclerFragment<ApplyContract.Presenter, ApplyUser>
        implements ApplyContract.View {

    private View.OnClickListener mRelationListener;

    public static ApplyFragment newInstance() {
        return new ApplyFragment();
    }

    @Override
    protected void initWidget(View root) {
        mRelationListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                ApplyUser applyUser = mAdapter.getItem(position);
                assert applyUser != null;
                mPresenter.addRelation(applyUser.getId(), position);
            }
        };
        super.initWidget(root);
    }

    @Override
    protected void onItemClick(ApplyUser applyUser, int position) {

    }

    @Override
    public void showAddRelationSuccess(int relation, int position) {
        ApplyUser applyUser = mAdapter.getItem(position);
        applyUser.setRelation(relation);
        mAdapter.updateItem(position);
    }

    @Override
    public void showAddRelationError() {
        SimplexToast.show(mContext, "关注失败");
    }

    @Override
    protected BaseRecyclerAdapter<ApplyUser> getAdapter() {
        return new ApplyAdapter(this, mRelationListener);
    }
}
