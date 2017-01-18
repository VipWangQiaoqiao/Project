package net.oschina.app.improve.user.collection;

import android.content.Context;

import net.oschina.app.improve.base.BaseListPresenter;
import net.oschina.app.improve.base.BaseListView;
import net.oschina.app.improve.bean.Collection;

/**
 * Created by haibin
 * on 2016/12/30.
 */

 interface UserCollectionContract {

    interface View extends BaseListView<Presenter, Collection> {
        void showGetFavSuccess(int position);
    }

    interface Presenter extends BaseListPresenter {
        void getCache(Context context);

        void getFavReverse(Collection collection, int position);
    }
}
