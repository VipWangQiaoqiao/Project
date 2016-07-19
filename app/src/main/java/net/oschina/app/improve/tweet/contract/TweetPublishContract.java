package net.oschina.app.improve.tweet.contract;

import net.oschina.app.bean.Comment;
import net.oschina.app.bean.Tweet;

import java.util.List;

/**
 * Created by JuQiu
 * on 16/7/14.
 */

public interface TweetPublishContract {
    interface Operator {

    }

    interface View {
        String getContent();

        List<String> getImagePath();
    }
}
