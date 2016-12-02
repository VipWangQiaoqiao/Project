package net.oschina.app.improve.detail.v2;

import net.oschina.app.improve.base.BasePresenter;
import net.oschina.app.improve.base.BaseView;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.bean.comment.Comment;

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

        void showFavReverseSuccess(boolean isFav, int strId);

        void showFavError();

        void showCommentSuccess(Comment comment);

        void showCommentError(String message);
    }

    interface Presenter extends BasePresenter {
        void getDetail();//获得详情

        void favReverse();

        void addComment(String content, long sid, int type, long referId, long replyId, long oid);//添加评论
    }
}
