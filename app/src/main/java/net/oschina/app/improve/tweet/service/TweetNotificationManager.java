package net.oschina.app.improve.tweet.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import net.oschina.app.AppContext;
import net.oschina.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jzz
 * on 2017/2/20.
 * desc:
 */
public class TweetNotificationManager {
    private static TweetNotificationManager INSTANCE;
    private PubTweetReceiver mPubTweetReceiver;
    private List<TweetPubNotify> mNotifies = new ArrayList<>();

    private TweetNotificationManager() {
    }

    private static TweetNotificationManager instance() {
        if (INSTANCE == null) {
            synchronized (TweetNotificationManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TweetNotificationManager();
                }
            }
        }
        return INSTANCE;
    }

    public static synchronized void setup(Context context) {
        if (context == null)
            return;
        Context appContext = context.getApplicationContext();
        if (appContext == null)
            return;
        TweetNotificationManager manager = instance();
        if (manager.mPubTweetReceiver == null) {
            // 注册广播
            PubTweetReceiver pubTweetReceiver = new PubTweetReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(TweetPublishService.ACTION_SUCCESS);
            filter.addAction(TweetPublishService.ACTION_FAILED);
            filter.addAction(TweetPublishService.ACTION_PROGRESS);
            filter.addAction(TweetPublishService.ACTION_PUBLISH);
            filter.addAction(TweetPublishService.ACTION_CONTINUE);
            filter.addAction(TweetPublishService.ACTION_DELETE);
            filter.addAction(TweetPublishService.ACTION_RECEIVER_SEARCH_FAILED);
            appContext.registerReceiver(pubTweetReceiver, filter);
            manager.mPubTweetReceiver = pubTweetReceiver;
            // 添加全局通知Toast
            manager.mNotifies.add(new ToastNotify());
        }
    }

    public static synchronized void destroy(Context context) {
        if (context == null)
            return;
        Context appContext = context.getApplicationContext();
        if (appContext == null)
            return;
        PubTweetReceiver pubTweetReceiver = instance().mPubTweetReceiver;
        if (pubTweetReceiver != null) {
            appContext.unregisterReceiver(pubTweetReceiver);
            instance().mPubTweetReceiver = null;
        }
    }

    public static void bindNotify(Context context, TweetPubNotify tweetPubNotify) {
        // 绑定前进行初始化操作
        setup(context);

        //检查是否已经存在
        TweetNotificationManager manager = instance();
        boolean contains = manager.mNotifies.contains(tweetPubNotify);
        if (!contains)
            manager.mNotifies.add(tweetPubNotify);
    }

    public static void unBoundNotify(TweetPubNotify tweetPubNotify) {
        instance().mNotifies.remove(tweetPubNotify);
    }

    private static class PubTweetReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                List<TweetPubNotify> notifies = instance().mNotifies;
                switch (intent.getAction()) {
                    case TweetPublishService.ACTION_SUCCESS:
                        //动弹发送成功
                        for (TweetPubNotify notify : notifies) {
                            notify.onTweetPubSuccess();
                        }
                        break;
                    case TweetPublishService.ACTION_FAILED:
                        //发送动弹失败
                        for (TweetPubNotify notify : notifies) {
                            notify.onTweetPubFailed();
                        }
                        break;
                    case TweetPublishService.ACTION_PROGRESS:
                        //更新动弹发送进度

                        String progressContent = intent.getStringExtra(TweetPublishService.EXTRA_PROGRESS);

                        for (TweetPubNotify notify : notifies) {
                            notify.onTweetPubProgress(progressContent);
                        }
                        break;
                    case TweetPublishService.ACTION_PUBLISH:
                        //开始发送动弹,监听发布进度起点
                        break;
                    case TweetPublishService.ACTION_CONTINUE:
                        //重新尝试发送该条动弹
                        for (TweetPubNotify notify : notifies) {
                            notify.onTweetPubContinue();
                        }
                        break;
                    case TweetPublishService.ACTION_DELETE:
                        //忽略该条动弹之后，直接gone草稿箱view
                        for (TweetPubNotify notify : notifies) {
                            notify.onTweetPubDelete();
                        }
                        break;
                    case TweetPublishService.ACTION_RECEIVER_SEARCH_FAILED:
                        //动态发送失败缓存查询
                        String[] pubFailedCacheIds = intent.getStringArrayExtra(TweetPublishService.EXTRA_IDS);

                        for (TweetPubNotify notify : notifies) {
                            notify.pnTweetReceiverSearchFailed(pubFailedCacheIds);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private static class ToastNotify implements TweetPubNotify {

        @Override
        public void onTweetPubSuccess() {
            AppContext.showToastShort(R.string.tweet_publish_success);
        }

        @Override
        public void onTweetPubFailed() {
            AppContext.showToastShort(R.string.tweet_publish_failed_hint);
        }

        @Override
        public void onTweetPubProgress(String progressContent) {
            AppContext.showToast(progressContent);
        }

        @Override
        public void onTweetPubContinue() {
            AppContext.showToastShort(R.string.tweet_retry_publishing_hint);
        }

        @Override
        public void onTweetPubDelete() {

        }

        @Override
        public void pnTweetReceiverSearchFailed(String[] pubFailedCacheIds) {

        }
    }

    public interface TweetPubNotify {

        void onTweetPubSuccess();

        void onTweetPubFailed();

        void onTweetPubProgress(String progressContent);

        void onTweetPubContinue();

        void onTweetPubDelete();

        void pnTweetReceiverSearchFailed(String[] pubFailedCacheIds);
    }

}
