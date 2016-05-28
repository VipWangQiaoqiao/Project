package net.oschina.app.contract;

/**
 * Created by qiujuer
 * on 16/5/28.
 */

public interface BlogDetailContract {
    interface Operator {
        void toFavorite();

        void toShare();

        void toFollow();

        void toSendComment(long id, String comment);
    }

    interface View {
        void toFavoriteOk();

        void toShareOk();

        void toFollowOk();

        void toSendCommentOk();
    }
}
