package net.oschina.app.improve.git.code;

import net.oschina.app.improve.base.BasePresenter;
import net.oschina.app.improve.base.BaseView;
import net.oschina.app.improve.git.bean.CodeDetail;

/**
 * Created by haibin
 * on 2017/3/13.
 */

 interface CodeDetaiContract {

    interface View extends BaseView<Presenter> {
        void showGetCodeSuccess(CodeDetail detail);

        void showGetCodeFailure(int strId);
    }

    interface Presenter extends BasePresenter {
        void getCodeDetail();

        String getShareUrl();
    }
}
