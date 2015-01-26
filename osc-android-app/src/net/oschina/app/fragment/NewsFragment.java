package net.oschina.app.fragment;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.adapter.NewsAdapter;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseListFragment;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.bean.ListEntity;
import net.oschina.app.bean.News;
import net.oschina.app.bean.NewsList;
import net.oschina.app.interf.OnTabReselectListener;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.UIHelper;
import net.oschina.app.util.XmlUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

/**
 * 新闻资讯
 * 
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @created 2014年11月12日 下午4:17:45
 * 
 */
public class NewsFragment extends BaseListFragment implements
        OnTabReselectListener {

    protected static final String TAG = NewsFragment.class.getSimpleName();
    private static final String CACHE_KEY_PREFIX = "newslist_";

    @Override
    protected ListBaseAdapter getListAdapter() {
        return new NewsAdapter();
    }

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + "_" + mCatalog;
    }

    @Override
    protected ListEntity parseList(InputStream is) throws Exception {
        NewsList list = XmlUtils.toBean(NewsList.class, is);
        return list;
    }

    @Override
    protected ListEntity readList(Serializable seri) {
        return ((NewsList) seri);
    }

    @Override
    protected void sendRequestData() {
        OSChinaApi.getNewsList(mCatalog, mCurrentPage, mHandler);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        News news = (News) mAdapter.getItem(position);
        if (news != null) {
        	UIHelper.showNewsRedirect(view.getContext(), news);
        	
        	// 放入已读列表
        	saveToReadedList(view, NewsList.PREF_READED_NEWS_LIST, news.getId() + "");
        }
    }

    @Override
    protected void executeOnLoadDataSuccess(List data) {
        if (mCatalog == NewsList.CATALOG_WEEK
                || mCatalog == NewsList.CATALOG_MONTH) {
            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            if (mState == STATE_REFRESH)
                mAdapter.clear();
            mAdapter.addData(data);
            mState = STATE_NOMORE;
            mAdapter.setState(ListBaseAdapter.STATE_NO_MORE);
            return;
        }
        super.executeOnLoadDataSuccess(data);
    }

    @Override
    public void onTabReselect() {
        onRefresh();
    }
}
