package net.oschina.app.improve.git.gist.detail;

import net.oschina.app.improve.base.BasePresenter;
import net.oschina.app.improve.base.BaseView;
import net.oschina.app.improve.git.bean.Gist;

/**
 * 代码片段详情
 * Created by haibin on 2017/5/10.
 */

interface GistDetailContract {

    interface EmptyView {
        void showGetDetailSuccess(int strId);

        void showGetDetailFailure(int strId);
    }

    interface View extends BaseView<Presenter> {
        void showGetDetailSuccess(Gist gist, int strId);

        void showGetCommentCountSuccess(int count);

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
        void getGistDetail(String id);

        void getCommentCount(String id);

        /**
         * 改变配置
         * @param isLandscape 是否是横屏
         */
        void changeConfig(boolean isLandscape);
    }
}
