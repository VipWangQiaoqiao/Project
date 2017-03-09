package net.oschina.app.improve.git.feature;

import net.oschina.app.improve.base.BaseListPresenter;
import net.oschina.app.improve.base.BaseListView;
import net.oschina.app.improve.git.bean.Project;

/**
 * Created by haibin
 * on 2017/3/9.
 */
interface FeatureContract {

    interface View extends BaseListView<Presenter, Project> {

    }

    interface Presenter extends BaseListPresenter {

    }
}
