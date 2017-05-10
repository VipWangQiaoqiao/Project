package net.oschina.app.improve.git.gist;

import net.oschina.app.improve.base.BaseListPresenter;
import net.oschina.app.improve.base.BaseListView;
import net.oschina.app.improve.git.bean.Gist;

/**
 * 代码片段
 * Created by haibin on 2017/5/10.
 */
interface GistContract {
    interface View extends BaseListView<Presenter, Gist> {

    }

    interface Presenter extends BaseListPresenter {

    }
}
