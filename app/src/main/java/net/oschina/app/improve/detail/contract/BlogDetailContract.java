package net.oschina.app.improve.detail.contract;

import net.oschina.app.improve.bean.BlogDetail;
import net.oschina.app.improve.bean.simple.Comment;

/**
 * Created by qiujuer
 * on 16/5/28.
 */

public interface BlogDetailContract {
    interface Operator extends DetailContract.Operator<BlogDetail, View> {
        // 收藏
        void toFavorite();

        // 分享
        void toShare();

        // 关注
        void toFollow();

        // 提交评价
        void toSendComment(long id, long commentId, long commentAuthorId, String comment);
    }

    interface View extends DetailContract.View {
        void toFavoriteOk(BlogDetail blogDetail);

        void toFollowOk(BlogDetail blogDetail);

        void toSendCommentOk(Comment comment);
    }
}
