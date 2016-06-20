package net.oschina.app.improve.detail.contract;

/**
 * Created by JuQiu
 * on 16/6/20.
 */

public interface DetailContract {
    interface Operator<Data, DataView extends View> {
        // 获取当前数据
        Data getData();

        // 举报
        void toReport();

        void setDataView(DataView view);
    }

    interface View {
        // 滚动到评论区域
        void scrollToComment();
    }

}
