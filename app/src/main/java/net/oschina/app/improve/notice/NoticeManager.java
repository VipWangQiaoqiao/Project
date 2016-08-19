package net.oschina.app.improve.notice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JuQiu on 16/8/18.
 */
public final class NoticeManager {
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



    public interface NoticeNotify {
        void onNoticeArrived(NoticeBean bean);
    }
}
