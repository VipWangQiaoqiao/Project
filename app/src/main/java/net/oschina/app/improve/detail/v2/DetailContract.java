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

        void showGetDetailSuccess(SubBean bean);

        void showFavReverseSuccess(boolean isFav, int strId);

        void showCommentSuccess(Comment comment);

        void showCommentError(String message);
    }

    interface View extends BaseView<Presenter> {
        void showGetDetailSuccess(SubBean bean);

        void showFavReverseSuccess(boolean isFav, int strId);

        void showFavError();

        void showCommentSuccess(Comment comment);

        void showCommentError(String message);

        void showAddRelationSuccess(boolean isRelation, int strId);

        void showAddRelationError();
    }

    interface Presenter extends BasePresenter {

        void getCache();

        void getDetail();//获得详情

        void favReverse();

        void addComment(
                long sourceId,
                int type,
                String content,
                long referId,
                long replyId,
                long reAuthorId
        );//添加评论

        void addUserRelation(long authorId);
    }
}
