package net.oschina.app.improve.contract;

import net.oschina.app.improve.detail.contract.DetailContract;
import net.oschina.app.improve.bean.SoftwareDetail;

/**
 * Created by fei
 * on 16/5/28.
 * desc:
 */

public interface SoftDetailContract {
    interface Operator extends DetailContract.Operator<SoftwareDetail, View> {
        SoftwareDetail getSoftwareDetail();

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
        void toFavoriteOk(SoftwareDetail softwareDetail);

        void toShareOk();

        void toFollowOk(SoftwareDetail softwareDetail);

        void toSendCommentOk();
    }
}
