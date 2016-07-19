package net.oschina.app.improve.tweet.contract;

import net.oschina.app.bean.Comment;
import net.oschina.app.bean.User;
import net.oschina.app.improve.bean.Tweet;

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
