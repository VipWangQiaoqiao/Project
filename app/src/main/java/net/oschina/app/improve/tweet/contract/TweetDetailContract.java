package net.oschina.app.improve.tweet.contract;

import net.oschina.app.bean.User;
import net.oschina.app.improve.bean.Tweet;
import net.oschina.app.improve.bean.simple.TweetComment;

/**
 * Created by thanatosx
 * on 16/5/28.
 */

public interface TweetDetailContract {

    interface Operator {

        Tweet getTweetDetail();

        void toReply(TweetComment comment);

        void onScroll();
    }

    interface ICmnView {
        void onCommentSuccess(TweetComment comment);
    }

    interface IThumbupView {
        void onLikeSuccess(boolean isUp, User user);
    }

    interface IAgencyView {
        void resetLikeCount(int count);

        void resetCmnCount(int count);
    }

}
