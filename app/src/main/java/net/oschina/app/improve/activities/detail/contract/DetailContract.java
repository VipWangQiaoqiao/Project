package net.oschina.app.improve.activities.detail.contract;

/**
 * Created by JuQiu
 * on 16/6/20.
 */

public interface DetailContract {
    interface Operator<Data> {
        // 获取当前数据
        Data getData();

        // 举报
        void toReport();
    }

    interface View {
        // 滚动到评论区域
        void scrollToComment();
    }

}
