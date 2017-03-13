package net.oschina.app.improve.git.tree;

import net.oschina.app.improve.base.BaseRecyclerFragment;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.git.bean.Branch;
import net.oschina.app.improve.git.bean.Tree;
import net.oschina.app.improve.git.code.CodeDetailActivity;

import java.util.List;

/**
 * Created by haibin
 * on 2017/3/13.
 */

public class TreeFragment extends BaseRecyclerFragment<TreeContract.Presenter, Tree> implements
        TreeContract.View {

    static TreeFragment newInstance() {
        return new TreeFragment();
    }

    @Override
    protected void onItemClick(Tree tree, int position) {
        if (tree.isFile()) {
            CodeDetailActivity.show(mContext, mPresenter.getProject(), tree.getName(), "master");
        }
    }

    @Override
    public void showGetBranchSuccess(List<Branch> branches) {

    }

    @Override
    public void showGetBranchFailure(int strId) {

    }

    @Override
    protected BaseRecyclerAdapter<Tree> getAdapter() {
        return new TreeAdapter(mContext);
    }
}
