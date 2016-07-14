package net.oschina.app.improve.tweet.contract;

import net.oschina.app.bean.Comment;
import net.oschina.app.bean.Tweet;
import net.oschina.app.bean.User;

/**
 * Created by JuQiu
 * on 16/7/14.
 */

public interface TweetPublishContract {
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
