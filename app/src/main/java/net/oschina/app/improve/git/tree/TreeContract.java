package net.oschina.app.improve.git.tree;

import net.oschina.app.improve.base.BaseListPresenter;
import net.oschina.app.improve.base.BaseListView;
import net.oschina.app.improve.git.bean.Branch;
import net.oschina.app.improve.git.bean.Project;
import net.oschina.app.improve.git.bean.Tree;

import java.util.List;

/**
 * 代码仓库
 * Created by haibin
 * on 2017/3/13.
 */

interface TreeContract {
    interface View extends BaseListView<Presenter, Tree> {
        void showGetBranchSuccess(List<Branch> branches);

        void showGetBranchFailure(int strId);
    }

    interface Presenter extends BaseListPresenter {
        void getBranch();

        Project getProject();
    }
}
