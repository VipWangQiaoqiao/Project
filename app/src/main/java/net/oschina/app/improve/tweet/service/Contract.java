package net.oschina.app.improve.tweet.service;

/**
 * Created by JuQiu
 * on 16/7/21.
 */

interface Contract {
    interface IService {
        String getCachePath(String id);

        void start(String modelId, IOperator operator);

        void stop(String id, int startId);

        void notifyMsg(int notifyId, String modelId, boolean haveReDo, boolean haveDelete, int resId, Object... values);

        void notifyCancel(int notifyId);

        void updateModelCache(String id, TweetPublishModel model);
    }

    interface IOperator extends Runnable {
        void stop();
    }
}
