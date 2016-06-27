package net.oschina.app.improve.detail.contract;

import net.oschina.app.improve.bean.NewsDetail;
import net.oschina.app.improve.bean.simple.Comment;

/**
 * Created by fei on 2016/6/13.
 * desc:
 */
public interface NewsDetailContract {
    interface Operator extends DetailContract.Operator<NewsDetail, View> {

        // 收藏
        void toFavorite();

        // 分享
        void toShare();

        // 提交评价
        void toSendComment(long id, long commentId, long commentAuthorId, String comment);
    }

    interface View extends DetailContract.View {
        void toFavoriteOk(NewsDetail newsDetail);

        //void toFollowOk(NewsDetail newsDetail);

        void toSendCommentOk(Comment comment);
    }
}
