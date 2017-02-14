package net.oschina.app.improve.notice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import net.oschina.app.OSCApplication;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.util.TLog;
import net.oschina.common.BuildConfig;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JuQiu
 * on 16/8/18.
 */
public final class NoticeManager {
    public static final int FLAG_CLEAR_MENTION = 0x00000001;
    public static final int FLAG_CLEAR_LETTER = 0x00000010;
    public static final int FLAG_CLEAR_REVIEW = 0x00000100;
    public static final int FLAG_CLEAR_FANS = 0x00001000;
    public static final int FLAG_CLEAR_LIKE = 0x00010000;
    public static final int FLAG_CLEAR_ALL = 0x00011111;

    private static NoticeManager INSTANCE;

    private NoticeManager() {
    }

    private static synchronized NoticeManager instance() {
        if (INSTANCE == null)
            INSTANCE = new NoticeManager();
        return INSTANCE;
    }

    private final List<NoticeNotify> mNotifies = new ArrayList<>();
    private NoticeBean mNotice;


    /**
     * 服务初始化方法，可以启动服务
     *
     * @param context Context
     */
    public static void init(Context context) {
        // 未登陆时不启动服务
        if (!AccountHelper.isLogin()) {
            return;
        }
        // 启动服务
        NoticeServer.startAction(context);
        // 注册广播
        IntentFilter filter = new IntentFilter(NoticeServer.FLAG_BROADCAST_REFRESH);
        context.registerReceiver(instance().mReceiver, filter);
    }

    public static void exitServer(Context context) {
        NoticeServer.exitAction(context);
    }

    public static void stopListen(Context context) {
        try {
            context.unregisterReceiver(instance().mReceiver);
        } catch (IllegalArgumentException e) {
            if (BuildConfig.DEBUG)
                e.printStackTrace();
        }
    }

    /**
     * 发布一个新的消息，通过Result节点
     *
     * @param resultBean ResultBean
     * @param newNotice  NoticeBean
     */
    public static void publish(ResultBean resultBean, NoticeBean newNotice) {
        if (resultBean != null && resultBean.isOk() && newNotice != null) {
            TLog.d("NoticeManager", "publish:" + newNotice.toString());
            NoticeServer.arrivedMsgAction(OSCApplication.getInstance(), newNotice);
        }
    }


    /**
     * 直接得到当前的消息
     *
     * @return NoticeBean
     */
    public static NoticeBean getNotice() {
        final NoticeBean bean = instance().mNotice;
        if (bean == null) {
            return new NoticeBean();
        } else {
            return bean;
        }
    }

    /**
     * 添加消息变化监听
     *
     * @param noticeNotify NoticeNotify
     */
    public static void bindNotify(NoticeNotify noticeNotify) {
        instance().mNotifies.add(noticeNotify);
        instance().check(noticeNotify);
    }

    /***
     * 取消消息变化监听
     * @param noticeNotify NoticeNotify
     */
    public static void unBindNotify(NoticeNotify noticeNotify) {
        instance().mNotifies.remove(noticeNotify);
    }

    /**
     * 已读清理Context
     *
     * @param context Context
     * @param type    {@link #FLAG_CLEAR_MENTION}, {@link #FLAG_CLEAR_LETTER},
     *                {@link #FLAG_CLEAR_REVIEW},{@link #FLAG_CLEAR_FANS},
     *                {@link #FLAG_CLEAR_LIKE}
     */
    public static void clear(Context context, int type) {
        if (getNotice().getAllCount() > 0)
            NoticeServer.clearAction(context, type);
    }

    /**
     * 绑定消息变化接口时进行一次检查，直接通知一次最新状态
     *
     * @param noticeNotify NoticeNotify
     */
    private void check(NoticeNotify noticeNotify) {
        if (mNotice != null)
            noticeNotify.onNoticeArrived(mNotice);
    }

    /**
     * 当消息改变时通知
     *
     * @param bean NoticeBean
     */
    private void onNoticeChanged(NoticeBean bean) {
        mNotice = bean;
        //  Notify all
        for (NoticeNotify notify : mNotifies) {
            notify.onNoticeArrived(mNotice);
        }
    }

    /**
     * 用于接收服务的消息
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                if (NoticeServer.FLAG_BROADCAST_REFRESH.equals(intent.getAction())) {
                    Serializable serializable = intent.getSerializableExtra(NoticeServer.EXTRA_BEAN);
                    if (serializable != null) {
                        try {
                            onNoticeChanged((NoticeBean) serializable);
                        } catch (Exception e) {
                            e.fillInStackTrace();
                        }
                    }
                } else if (NoticeServer.FLAG_BROADCAST_REQUEST.equals(intent.getAction())) {
                    // Do...
                }
            }
        }
    };

    /**
     * 消息变化时通知接口
     */
    public interface NoticeNotify {
        void onNoticeArrived(NoticeBean bean);
    }
}
