package net.oschina.app.improve.tweet.service;

import java.io.Serializable;

/**
 * Created by JuQiu
 * on 16/7/21.
 */

public class TweetPublishModel implements Serializable {
    private String id;
    private String content;
    private String[] images;
    private String imageToken;
    private int imageIndex;

}
