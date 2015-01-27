package net.oschina.app.base;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;

import net.oschina.app.cache.CacheManager;

import org.apache.http.Header;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import butterknife.ButterKnife;

import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * 需要加入header的BaseListFragment
 * 
 * @desc 应用场景：如动弹详情、团队任务详情这些， 即是头部显示详情，然后下面显示评论列表的
 * 
 *       BeseHaveHeaderListFragment.java
 * 
 * @author 火蚁(http://my.oschina.net/u/253900)
 * 
 * @data 2015-1-27 下午3:02:42
 */
public abstract class BeseHaveHeaderListFragment<T extends Serializable>
	extends BaseListFragment {

    protected T detailBean;// list 头部的详情实体类

    protected final AsyncHttpResponseHandler mDetailHandler = new AsyncHttpResponseHandler() {

	@Override
	public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
	    try {
		if (arg2 != null) {
		    T detail = getDetailBean(new ByteArrayInputStream(arg2));
		    if (detail != null) {
			requstListData();
			executeOnLoadDetailSuccess(detail);
			new SaveCacheTask(getActivity(), detail,
				getDetailCacheKey()).execute();
		    } else {
			onFailure(arg0, arg1, arg2, null);
		    }
		} else {
		    throw new RuntimeException("load detail error");
		}
	    } catch (Exception e) {
		e.printStackTrace();
		onFailure(arg0, arg1, arg2, e);
	    }
	}

	@Override
	public void onFailure(int arg0, Header[] arg1, byte[] arg2,
		Throwable arg3) {
	    readDetailCacheData(getDetailCacheKey());
	}
    };

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
	// 通过注解绑定控件
	ButterKnife.inject(this, view);
	mListView.addHeaderView(initHeaderView());
	super.initView(view);
	requestDetailData(isRefresh());
    }
    
    protected boolean isRefresh() {
	return false;
    }

    protected abstract void requestDetailData(boolean isRefresh);

    protected abstract View initHeaderView();

    protected abstract String getDetailCacheKey();

    protected abstract void executeOnLoadDetailSuccess(T detailBean);

    protected abstract T getDetailBean(ByteArrayInputStream is);

    @Override
    protected boolean requestDataIfViewCreated() {
	return false;
    }
    
    private void requstListData() {
	mState = STATE_REFRESH;
	sendRequestData();
    }
    
    /***
     * 带有header view的listfragment不需要显示是否数据为空
     */
    protected boolean needShowEmptyNoData() {
	return false;
    }
    
    protected void readDetailCacheData(String cacheKey) {
        new ReadCacheTask(getActivity()).execute(cacheKey);
    }

    private class SaveCacheTask extends AsyncTask<Void, Void, Void> {
	private final WeakReference<Context> mContext;
	private final Serializable seri;
	private final String key;

	private SaveCacheTask(Context context, Serializable seri, String key) {
	    mContext = new WeakReference<Context>(context);
	    this.seri = seri;
	    this.key = key;
	}

	@Override
	protected Void doInBackground(Void... params) {
	    CacheManager.saveObject(mContext.get(), seri, key);
	    return null;
	}
    }
    
    private class ReadCacheTask extends AsyncTask<String, Void, T> {
        private final WeakReference<Context> mContext;

        private ReadCacheTask(Context context) {
            mContext = new WeakReference<Context>(context);
        }

        @Override
        protected T doInBackground(String... params) {
            if (mContext.get() != null) {
                Serializable seri = CacheManager.readObject(mContext.get(),
                        params[0]);
                if (seri == null) {
                    return null;
                } else {
                    return (T) seri;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(T t) {
            super.onPostExecute(t);
            if (t != null) {
        	requstListData();
                executeOnLoadDetailSuccess(t);
            }
        }
    }
}
