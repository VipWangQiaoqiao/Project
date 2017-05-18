package net.oschina.app.improve.git.code;

import net.oschina.app.improve.base.BasePresenter;
import net.oschina.app.improve.base.BaseView;
import net.oschina.app.improve.git.bean.CodeDetail;

/**
 * Created by haibin
 * on 2017/3/13.
 */

 interface CodeDetailContract {

    interface View extends BaseView<Presenter> {
        void showGetCodeSuccess(CodeDetail detail);

        void showGetCodeFailure(int strId);

        /**
         * 显示横屏
         */
        void showLandscape();

        /**
         * 显示竖屏
         */
        void showPortrait();
    }

    interface Presenter extends BasePresenter {
        void getCodeDetail();

        String getShareUrl();

        /**
         * 改变配置
         * @param isLandscape 是否是横屏
         */
        void changeConfig(boolean isLandscape);
    }
}
