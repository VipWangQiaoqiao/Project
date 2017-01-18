package net.oschina.app.improve.user.event;

import net.oschina.app.improve.base.BaseListPresenter;
import net.oschina.app.improve.base.BaseListView;
import net.oschina.app.improve.bean.SubBean;

/**
 * Created by haibin
 * on 2017/1/18.
 */

interface UserEventContract {

    interface EmptyView {
        void hideEmptyLayout();

        void showErrorLayout(int errorType);
    }

    interface View extends BaseListView<Presenter, SubBean> {

    }

    interface Presenter extends BaseListPresenter {

    }
}
