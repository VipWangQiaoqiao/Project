package net.oschina.app.improve.user.collection;

import android.content.DialogInterface;

import net.oschina.app.improve.base.BaseRecyclerFragment;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.Collection;
import net.oschina.app.improve.bean.News;
import net.oschina.app.improve.detail.general.BlogDetailActivity;
import net.oschina.app.improve.detail.general.EventDetailActivity;
import net.oschina.app.improve.detail.general.NewsDetailActivity;
import net.oschina.app.improve.detail.general.QuestionDetailActivity;
import net.oschina.app.improve.detail.general.SoftwareDetailActivity;
import net.oschina.app.improve.user.adapter.CollectionAdapter;
import net.oschina.app.improve.utils.CacheManager;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.util.UIHelper;

import java.util.List;

/**
 * Created by haibin
 * on 2016/12/30.
 */

public class UserCollectionFragment extends BaseRecyclerFragment<UserCollectionContract.Presenter, Collection> implements
        UserCollectionContract.View, BaseRecyclerAdapter.OnItemLongClickListener {

    public static UserCollectionFragment newInstance() {
        return new UserCollectionFragment();
    }

    @Override
    protected void initData() {
        mPresenter.getCache(mContext);
        super.initData();
        mAdapter.setOnItemLongClickListener(this);
    }

    @Override
    protected void onItemClick(Collection item, int position) {
        switch (item.getType()) {
            case News.TYPE_SOFTWARE:
                SoftwareDetailActivity.show(mContext, item.getId(), true);
                break;
            case News.TYPE_QUESTION:
                QuestionDetailActivity.show(mContext, item.getId(), true);
                break;
            case News.TYPE_BLOG:
                BlogDetailActivity.show(mContext, item.getId(), true);
                break;
            case News.TYPE_TRANSLATE:
                NewsDetailActivity.show(mContext, item.getId(), item.getType());
                break;
            case News.TYPE_EVENT:
                EventDetailActivity.show(mContext, item.getId(), true);
                break;
            case News.TYPE_NEWS:
                NewsDetailActivity.show(mContext, item.getId(), true);
                break;
            default:
                UIHelper.showUrlRedirect(mContext, item.getHref());
                break;
        }
    }

    @Override
    public void onLongClick(final int position, long itemId) {
        final Collection collection = mAdapter.getItem(position);
        if (collection == null)
            return;
        DialogHelper.getConfirmDialog(mContext, "删除收藏", "是否确认删除该内容吗？", "确认", "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPresenter.getFavReverse(collection, position);
            }
        }).show();
    }

    @Override
    public void showGetFavSuccess(int position) {
        mAdapter.removeItem(position);
    }

    @Override
    public void onRefreshSuccess(List<Collection> data) {
        super.onRefreshSuccess(data);
        CacheManager.saveToJson(mContext, UserCollectionPresenter.CACHE_NAME, data);
    }

    @Override
    protected BaseRecyclerAdapter<Collection> getAdapter() {
        return new CollectionAdapter(mContext);
    }
}
