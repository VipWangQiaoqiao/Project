package net.oschina.app.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class ServerTaskUtils {
	
	public static void publicBlogComment(Context context, PublicCommentTask task) {
		Intent intent = new Intent(ServerTaskService.ACTION_PUBLIC_BLOG_COMMENT);
		Bundle bundle = new Bundle();
		bundle.putParcelable(ServerTaskService.BUNDLE_PUBLIC_COMMENT_TASK, task);
		intent.putExtras(bundle);
		context.startService(intent);
	}

	public static void publicNewsComment(Context context, PublicCommentTask task) {
		Intent intent = new Intent(ServerTaskService.ACTION_PUBLIC_COMMENT);
		Bundle bundle = new Bundle();
		bundle.putParcelable(ServerTaskService.BUNDLE_PUBLIC_COMMENT_TASK, task);
		intent.putExtras(bundle);
		context.startService(intent);
	}
}
