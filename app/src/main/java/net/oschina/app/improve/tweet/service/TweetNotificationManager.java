package net.oschina.app.improve.tweet.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import net.oschina.app.improve.account.AccountHelper;

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
    private boolean mIsRegister;

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

    public static void bindTweetPubNotify(Context context, TweetPubNotify tweetPubNotify) {
        registerBroadcastReceiver(context);

        //检查是否已经存在
        boolean contains = instance().mNotifies.contains(tweetPubNotify);
        if (!contains)
            instance().mNotifies.add(tweetPubNotify);
    }


    public static void registerBroadcastReceiver(Context context) {

        // 未登陆时不进行注册
        if (!AccountHelper.isLogin()) {
            return;
        }

        //register broadcastReceiver
        if (!instance().mIsRegister && instance().mPubTweetReceiver == null) {

            PubTweetReceiver pubTweetReceiver = new PubTweetReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(TweetPublishService.ACTION_SUCCESS);
            filter.addAction(TweetPublishService.ACTION_FAILED);
            filter.addAction(TweetPublishService.ACTION_PROGRESS);
            filter.addAction(TweetPublishService.ACTION_PUBLISH);
            filter.addAction(TweetPublishService.ACTION_CONTINUE);
            filter.addAction(TweetPublishService.ACTION_DELETE);
            filter.addAction(TweetPublishService.ACTION_RECEIVER_SEARCH_FAILED);
            context.registerReceiver(pubTweetReceiver, filter);

            instance().mPubTweetReceiver = pubTweetReceiver;

            instance().mIsRegister = true;
        }
    }

    public static void unBoundTweetPubNotify(TweetPubNotify tweetPubNotify) {
        instance().mNotifies.remove(tweetPubNotify);
    }

    public static void stopTweetPubNotify(Context context) {

        PubTweetReceiver pubTweetReceiver = instance().mPubTweetReceiver;
        if (instance().mIsRegister && pubTweetReceiver != null) {
            context.unregisterReceiver(pubTweetReceiver);
            instance().mIsRegister = false;
        }

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


    public interface TweetPubNotify {

        void onTweetPubSuccess();

        void onTweetPubFailed();

        void onTweetPubProgress(String progressContent);

        void onTweetPubContinue();

        void onTweetPubDelete();

        void pnTweetReceiverSearchFailed(String[] pubFailedCacheIds);
    }

}
