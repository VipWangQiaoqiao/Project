package net.oschina.app.improve.notice;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JuQiu on 16/8/18.
 */
public final class NoticeManager {
    public static final int FLAG_CLEAR_MENTION = 0x1;
    public static final int FLAG_CLEAR_LETTER = 0x2;
    public static final int FLAG_CLEAR_REVIEW = 0x3;
    public static final int FLAG_CLEAR_FANS = 0x4;
    public static final int FLAG_CLEAR_LIKE = 0x5;

    private static NoticeManager INSTANCE;

    static {
        INSTANCE = new NoticeManager();
    }

    private List<NoticeNotify> notifies = new ArrayList<>();

    public static NoticeBean getNotice() {
        return new NoticeBean();
    }

    public static void bindNotify(NoticeNotify noticeNotify) {
        INSTANCE.notifies.add(noticeNotify);
        INSTANCE.check();
    }

    public static void unBindNotify(NoticeNotify noticeNotify) {
        INSTANCE.notifies.remove(noticeNotify);
    }

    private void check() {

    }

    public static void start(Context context) {
        NoticeService.startAction(context);
    }

    public static void stop(Context context) {
        NoticeService.startActionExit(context);
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
        NoticeService.startActionClear(context, type);
    }

    public interface NoticeNotify {
        void onNoticeArrived(NoticeBean bean);
    }
}
