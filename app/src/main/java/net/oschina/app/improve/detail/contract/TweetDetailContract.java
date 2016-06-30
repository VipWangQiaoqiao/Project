package net.oschina.app.improve.detail.contract;

import net.oschina.app.bean.Comment;
import net.oschina.app.bean.Tweet;
import net.oschina.app.bean.User;

/**
 * Created by thanatosx
 * on 16/5/28.
 */

public interface TweetDetailContract {

    interface Operator {

        Tweet getTweetDetail();

        void toReply(Comment comment);

        void onScroll();
    }

    interface ICmnView {
        void onCommentSuccess(Comment comment);
    }

    interface IThumbupView {
        void onLikeSuccess(boolean isUp, User user);
    }

    interface IAgencyView {
        void resetLikeCount(int count);

        void resetCmnCount(int count);
    }

}
