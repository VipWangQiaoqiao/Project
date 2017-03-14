package net.oschina.app.improve.git.tree;

import net.oschina.app.improve.base.BaseListPresenter;
import net.oschina.app.improve.base.BaseListView;
import net.oschina.app.improve.git.bean.Project;
import net.oschina.app.improve.git.bean.Tree;

/**
 * 代码仓库
 * Created by haibin
 * on 2017/3/13.
 */

interface TreeContract {
    interface View extends BaseListView<Presenter, Tree> {
    }

    interface Presenter extends BaseListPresenter {
        Project getProject();

        void setBranch(String branch);

        String getBranch();

        String getPath();

        void preLoad(int position);

        void preLoad();

        void nextLoad(String path);

        String getImageUrl(String fileName);
    }
}
