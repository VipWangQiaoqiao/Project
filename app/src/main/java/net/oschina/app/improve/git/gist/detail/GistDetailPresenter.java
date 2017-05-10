package net.oschina.app.improve.git.gist.detail;

/**
 * 代码片段详情
 * Created by haibin on 2017/5/10.
 */

class GistDetailPresenter implements GistDetailContract.Presenter {
    private final GistDetailContract.View mView;
    private final GistDetailContract.EmptyView mEmptyView;

    GistDetailPresenter(GistDetailContract.View mView, GistDetailContract.EmptyView mEmptyView) {
        this.mView = mView;
        this.mEmptyView = mEmptyView;
        this.mView.setPresenter(this);
    }

    @Override
    public void getGistDetail(String id) {

    }
}
