package net.oschina.app.improve.base;

/**
 * Created by haibin
 * on 2016/11/30.
 */

public interface BaseListPresenter extends BasePresenter {
    void onRefreshing();

    void onLoadMore();
}
