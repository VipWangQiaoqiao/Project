package net.oschina.app.improve.git.comment;

import net.oschina.app.improve.base.BaseListPresenter;
import net.oschina.app.improve.base.BaseListView;
import net.oschina.app.improve.git.bean.Comment;

/**
 * Created by haibin
 * on 2017/3/14.
 */

interface CommentContract {

    interface Action {
        void showAddCommentSuccess(Comment comment, int strId);

        void showAddCommentFailure(int strId);
    }

    interface View extends BaseListView<Presenter, Comment> {
        void showAddCommentSuccess(Comment comment, int strId);

        void showAddCommentFailure(int strId);
    }

    interface Presenter extends BaseListPresenter {
        void addComment(String content);
    }
}
