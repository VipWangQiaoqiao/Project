package net.oschina.app.improve.detail.sign;

import net.oschina.app.improve.base.BasePresenter;
import net.oschina.app.improve.base.BaseView;
import net.oschina.app.improve.bean.EventDetail;
import net.oschina.app.improve.bean.SignUpEventOptions;

import java.util.List;

/**
 * Created by haibin
 * on 2016/12/5.
 */
interface SignUpContract {

    interface EmptyView {
        void hideEmptyLayout();

        void showErrorLayout(int errorType);
    }

    interface View extends BaseView<Presenter> {
        void showGetSignUpOptionsSuccess(List<SignUpEventOptions> options);

        void showSignUpSuccess(EventDetail detail);

        void showSignUpError(String message);

        void showInputEmpty(String message);
    }

    interface Presenter extends BasePresenter {
        /**
         * 获得报名参数
         */
        void getSignUpOptions(long sourceId);

        /**
         * 报名
         */
        void signUpEvent(long sourceId);
    }
}
