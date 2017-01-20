package net.oschina.app.improve.detail.apply;

import android.app.ProgressDialog;
import android.view.View;

import net.oschina.app.improve.base.BaseRecyclerFragment;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.ApplyUser;
import net.oschina.app.improve.user.activities.OtherUserHomeActivity;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.improve.widget.SimplexToast;
import net.oschina.app.util.TDevice;

/**
 * Created by haibin
 * on 2016/12/27.
 */

public class ApplyFragment extends BaseRecyclerFragment<ApplyContract.Presenter, ApplyUser>
        implements ApplyContract.View {

    private View.OnClickListener mRelationListener;
    private ProgressDialog mProgressDialog;

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
                if (applyUser.getId() == 0) {
                    SimplexToast.show(mContext, "不能关注匿名用户");
                    return;
                }
                showDialog("正在添加关注...");
                mPresenter.addRelation(applyUser.getId(), position);
            }
        };
        super.initWidget(root);
    }

    @Override
    protected void onItemClick(ApplyUser applyUser, int position) {
        ApplyUser user = mAdapter.getItem(position);
        if (user == null || user.getId() == 0) {
            SimplexToast.show(mContext, "用户不存在");
            return;
        }
        OtherUserHomeActivity.show(mContext, user.getId());
    }

    @Override
    public void showAddRelationSuccess(int relation, int position) {
        ApplyUser applyUser = mAdapter.getItem(position);
        applyUser.setRelation(relation);
        mAdapter.updateItem(position);
        hideDialog();
        TDevice.hideSoftKeyboard(mRoot);
    }

    @Override
    public void showAddRelationError() {
        SimplexToast.show(mContext, "关注失败");
        hideDialog();
    }

    @Override
    protected BaseRecyclerAdapter<ApplyUser> getAdapter() {
        return new ApplyAdapter(this, mRelationListener);
    }

    private void showDialog(String message) {
        if (mProgressDialog == null)
            mProgressDialog = DialogHelper.getProgressDialog(mContext);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    private void hideDialog() {
        if (mProgressDialog == null)
            return;
        mProgressDialog.dismiss();
    }
}
