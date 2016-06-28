package net.oschina.app.improve.detail.contract;

import net.oschina.app.improve.bean.TranslationDetail;
import net.oschina.app.improve.bean.simple.Comment;

/**
 * Created by fei on 2016/6/28.
 * desc:
 */
public interface TranslateDetailContract {
    interface Operator extends DetailContract.Operator<TranslationDetail, View> {

        // 收藏
        void toFavorite();

        // 分享
        void toShare();

        // 提交评价
        void toSendComment(long id, long commentId, long commentAuthorId, String comment);
    }

    interface View extends DetailContract.View {
        void toFavoriteOk(TranslationDetail translationDetail);

        //void toFollowOk(NewsDetail newsDetail);

        void toSendCommentOk(Comment comment);
    }
}
