package net.oschina.app.improve.detail.contract;

import net.oschina.app.improve.bean.BlogDetail;

/**
 * Created by qiujuer
 * on 16/5/28.
 */

public interface BlogDetailContract {
    interface Operator extends DetailContract.Operator<BlogDetail, View> {
        BlogDetail getBlogDetail();

        // 收藏
        void toFavorite();

        // 分享
        void toShare();

        // 关注
        void toFollow();

        // 举报
        void toReport();

        // 提交评价
        void toSendComment(long id, long authorId, String comment);
    }

    interface View extends DetailContract.View {
        void toFavoriteOk(BlogDetail blogDetail);

        void toFollowOk(BlogDetail blogDetail);

        void toSendCommentOk();
    }
}
