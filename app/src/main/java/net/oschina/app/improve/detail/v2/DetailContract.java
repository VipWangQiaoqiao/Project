package net.oschina.app.improve.detail.v2;

import net.oschina.app.improve.base.BasePresenter;
import net.oschina.app.improve.base.BaseView;
import net.oschina.app.improve.bean.SubBean;

/**
 * Created by haibin
 * on 2016/11/30.
 */

public interface DetailContract {

    interface EmptyView {
        void hideEmptyLayout();

        void showErrorLayout(int errorType);
    }

    interface View extends BaseView<Presenter> {
        void showGetDetailSuccess(SubBean bean);

        void showGetDetailError(String message);

        void showFavReverseSuccess(boolean isFav,int strId);

        void showFavError();
    }

    interface Presenter extends BasePresenter {
        void getDetail();

        void favReverse();
    }
}
