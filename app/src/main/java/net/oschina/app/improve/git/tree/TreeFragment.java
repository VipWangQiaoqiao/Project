package net.oschina.app.improve.git.tree;

import android.view.View;

import net.oschina.app.improve.base.BaseRecyclerFragment;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.git.bean.Tree;
import net.oschina.app.improve.git.code.CodeDetailActivity;
import net.oschina.app.improve.git.utils.CodeFileUtil;
import net.oschina.app.improve.media.ImageGalleryActivity;
import net.oschina.app.improve.utils.DialogHelper;

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
    protected void initWidget(View root) {
        super.initWidget(root);
        mRefreshLayout.setEnabled(false);
    }

    @Override
    protected void onItemClick(Tree tree, int position) {
        if (tree.isFile()) {
            if (CodeFileUtil.isCodeTextFile(tree.getName())) {
                CodeDetailActivity.show(mContext,
                        mPresenter.getProject(),
                        mPresenter.getPath() + tree.getName(),
                        mPresenter.getBranch());
            } else if (CodeFileUtil.isImage(tree.getName())) {
                ImageGalleryActivity.show(mContext, mPresenter.getImageUrl(tree.getName()));
            } else {
                DialogHelper.getMessageDialog(mContext, "温馨提醒", "该文件不支持在线预览", "取消").show();
            }
        } else {
            mPresenter.nextLoad(tree.getName());
        }
    }

    @Override
    protected BaseRecyclerAdapter<Tree> getAdapter() {
        return new TreeAdapter(mContext);
    }
}
