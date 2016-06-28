package net.oschina.app.improve.detail.contract;

import net.oschina.app.improve.bean.SoftwareDetail;
import net.oschina.app.improve.bean.simple.Comment;

/**
 * Created by fei
 * on 16/5/28.
 * desc:
 */

public interface SoftDetailContract {
    interface Operator extends DetailContract.Operator<SoftwareDetail, View> {

        // 收藏
        void toFavorite();

        // 分享
        void toShare();

        // 提交评价
        void toSendComment(long id, long commentId, long commentAuthorId, String comment);
    }

    interface View extends DetailContract.View {
        void toFavoriteOk(SoftwareDetail softwareDetail);


        void toSendCommentOk(Comment comment);
    }
}
