package net.oschina.app.improve.detail.contract;

import net.oschina.app.improve.bean.QuestionDetail;
import net.oschina.app.improve.bean.simple.CommentEX;

/**
 * Created by fei on 2016/6/13.
 * desc:
 */
public interface QuestionDetailContract {
    interface Operator extends DetailContract.Operator<QuestionDetail, View> {

        // 收藏
        void toFavorite();

        // 分享
        void toShare();

        // 举报
        void toReport();

        // 提交评价
        void toSendComment(long id, long commentId, long commentAuthorId, String comment);
    }

    interface View extends DetailContract.View {
        void toFavoriteOk(QuestionDetail questionDetail);

        void toSendCommentOk(CommentEX commentEX);
    }
}
