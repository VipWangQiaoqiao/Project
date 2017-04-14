package net.oschina.app.improve.user.sign.up;

import net.oschina.app.improve.base.BasePresenter;
import net.oschina.app.improve.base.BaseView;
import net.oschina.app.improve.bean.EventDetail;
import net.oschina.app.improve.bean.EventSignIn;

import java.util.Map;

/**
 * 活动报名签到，包括报名信息
 * Created by haibin on 2017/4/12.
 */

interface SignUpContract {
    interface EmptyView {
        void hideEmptyLayout();

        void showErrorLayout(int errorType);
    }

    interface View extends BaseView<Presenter> {
        void showGetDetailSuccess(EventDetail detail);

        void showGetApplyInfoSuccess(Map<String, String> map);

        void showSignInSuccess(EventSignIn sign);

        void showSignInFailure(int strId);
    }

    interface Presenter extends BasePresenter {
        void getEventDetail(long id);

        void getApplyInfo(long id);

        /**
         * 签到
         *
         * @param id 活动id
         */
        void signUp(long id);
    }
}
