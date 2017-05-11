package net.oschina.app.improve.git.gist.comment;

import net.oschina.app.improve.base.BaseListPresenter;
import net.oschina.app.improve.base.BaseListView;
import net.oschina.app.improve.git.bean.Comment;

/**
 * 代码片段评论
 * Created by haibin on 2017/5/11.
 */

interface GistCommentContract {
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
