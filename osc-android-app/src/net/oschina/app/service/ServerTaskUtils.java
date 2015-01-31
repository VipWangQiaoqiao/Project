package net.oschina.app.service;

import net.oschina.app.bean.Tweet;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class ServerTaskUtils {

    public static void publicBlogComment(Context context, PublicCommentTask task) {
	Intent intent = new Intent(ServerTaskService.ACTION_PUB_BLOG_COMMENT);
	Bundle bundle = new Bundle();
	bundle.putParcelable(ServerTaskService.BUNDLE_PUB_COMMENT_TASK, task);
	intent.putExtras(bundle);
	context.startService(intent);
    }

    public static void publicNewsComment(Context context, PublicCommentTask task) {
	Intent intent = new Intent(ServerTaskService.ACTION_PUB_COMMENT);
	Bundle bundle = new Bundle();
	bundle.putParcelable(ServerTaskService.BUNDLE_PUB_COMMENT_TASK, task);
	intent.putExtras(bundle);
	context.startService(intent);
    }

    public static void pubTweet(Context context, Tweet tweet) {
	Intent intent = new Intent(ServerTaskService.ACTION_PUB_TWEET);
	Bundle bundle = new Bundle();
	bundle.putParcelable(ServerTaskService.BUNDLE_PUB_TWEET_TASK, tweet);
	intent.putExtras(bundle);
	context.startService(intent);
    }

    public static void pubSoftWareTweet(Context context, Tweet tweet, int softid) {
	Intent intent = new Intent(ServerTaskService.ACTION_PUB_SOFTWARE_TWEET);
	Bundle bundle = new Bundle();
	bundle.putParcelable(ServerTaskService.BUNDLE_PUB_SOFTWARE_TWEET_TASK,
		tweet);
	bundle.putInt(ServerTaskService.KEY_SOFTID, softid);
	intent.putExtras(bundle);
	context.startService(intent);
    }

    public static void pubTweetComment(Context context, PublicCommentTask task) {
	Intent intent = new Intent(ServerTaskService.ACTION_PUB_COMMENT);
	Bundle bundle = new Bundle();
	bundle.putParcelable(ServerTaskService.BUNDLE_PUB_COMMENT_TASK, task);
	intent.putExtras(bundle);
	context.startService(intent);
    }
}
