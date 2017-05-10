package net.oschina.app.improve.git.gist;

/**
 * 代码片段
 * Created by haibin on 2017/5/10.
 */

class GistPresenter implements GistContract.Presenter {
    private final GistContract.View mView;

    GistPresenter(GistContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
    }

    @Override
    public void onRefreshing() {

    }

    @Override
    public void onLoadMore() {

    }
}
