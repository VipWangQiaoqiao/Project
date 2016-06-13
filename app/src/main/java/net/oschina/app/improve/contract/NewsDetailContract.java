package net.oschina.app.improve.contract;

import net.oschina.app.improve.bean.NewsDetail;

/**
 * Created by fei on 2016/6/13.
 * desc:
 */
public interface NewsDetailContract {
    interface Operator {
        NewsDetail getNewsDetail();

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

    interface View {
        void toFavoriteOk(NewsDetail newsDetail);

        void toShareOk();

        void toFollowOk(NewsDetail newsDetail);

        void toSendCommentOk();
    }
}
