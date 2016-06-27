package net.oschina.app.improve.detail.contract;

/**
 * Created by JuQiu
 * on 16/6/20.
 */

public interface DetailContract {
    interface Operator<Data, DataView extends View> {
        // 获取当前数据
        Data getData();

        void hideLoading();

        // 回写布局View
        void setDataView(DataView view);

        // 回写顶部评论数
        void setCommentCount(int count);
    }

    interface View {
        // 滚动到评论区域
        void scrollToComment();
    }

}
