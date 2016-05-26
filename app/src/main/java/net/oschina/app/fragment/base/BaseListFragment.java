package net.oschina.app.fragment.base;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.adapter.base.BaseListAdapter;
import net.oschina.app.bean.base.PageBean;
import net.oschina.app.bean.base.ResultBean;
import net.oschina.app.cache.CacheManager;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.widget.SuperRefreshLayout;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cz.msebera.android.httpclient.Header;

/**
 * T as the base bean
 * Created by huanghaibin
 * on 16-5-23.
 */
public abstract class BaseListFragment<T> extends BaseFragment implements
        SuperRefreshLayout.SuperRefreshLayoutListener, AdapterView.OnItemClickListener, BaseListAdapter.Callback {

    protected String CACHE_NAME = getClass().getName();
    private String mTime;

    protected static ExecutorService mExeService = Executors.newFixedThreadPool(3);

    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_LOADING = 1;
    public static final int TYPE_NO_MORE = 2;
    public static final int TYPE_ERROR = 3;
    public static final int TYPE_NET_ERROR = 4;

    private View mFooterView;
    private ProgressBar mFooterProgressBar;
    private TextView mFooterText;

    protected ListView mListView;

    protected SuperRefreshLayout mRefreshLayout;

    protected EmptyLayout mErrorLayout;

    protected BaseListAdapter<T> mAdapter;
    protected boolean mIsRefresh;

    protected TextHttpResponseHandler mHandler;

    protected PageBean<T> mBeam;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_base_list;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mListView = (ListView) root.findViewById(R.id.listView);
        mRefreshLayout = (SuperRefreshLayout) root.findViewById(R.id.superRefreshLayout);
        mRefreshLayout.setColorSchemeResources(
                R.color.swiperefresh_color1, R.color.swiperefresh_color2,
                R.color.swiperefresh_color3, R.color.swiperefresh_color4);
        mErrorLayout = (EmptyLayout) root.findViewById(R.id.error_layout);
        mRefreshLayout.setSuperRefreshLayoutListener(this);
        mFooterView = LayoutInflater.from(getContext()).inflate(R.layout.layout_list_view_footer, null);
        mFooterText = (TextView) mFooterView.findViewById(R.id.tv_footer);
        mFooterProgressBar = (ProgressBar) mFooterView.findViewById(R.id.pb_footer);
        mListView.setOnItemClickListener(this);
        setFooterType(TYPE_LOADING);
        if (isNeedFooter())
            mListView.addFooterView(mFooterView);
    }

    @Override
    protected void initData() {
        super.initData();
        //when open this fragment,read the obj

        mAdapter = getListAdapter();
        mListView.setAdapter(mAdapter);

        mHandler = new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                onRequestError(statusCode);
                onRequestFinish();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultBean<PageBean<T>> resultBean = AppContext.createGson().fromJson(responseString, getType());
                    if (resultBean != null && resultBean.isSuccess()) {
                        onRequestSuccess(resultBean.getCode());
                        setListData(resultBean);
                    }
                    //// TODO: 16-5-23
                    onRequestFinish();
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        };


        mExeService.submit(new Runnable() {
            @Override
            public void run() {
                mBeam = (PageBean<T>) CacheManager.readObject(getActivity(), CACHE_NAME);
                //if is the first loading
                if (mBeam == null) {
                    mBeam = new PageBean<>();
                    mBeam.setItems(new ArrayList<T>());
                    onRefreshing();
                } else {
                    mAdapter.addItem(mBeam.getItems());
                    mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                    mRefreshLayout.setVisibility(View.VISIBLE);
                    onRefreshing();
                    // not ExpiryDate
//                    if (!AppConfig.isExpiryDate(AppConfig.getAppConfig(getActivity()).get(CACHE_NAME))) {
//                        onRefreshing();
//                    }
                }
            }
        });
    }

    @Override
    public void onRefreshing() {
        mIsRefresh = true;
        requestData();
    }

    @Override
    public void onLoadMore() {
        requestData();
    }

    /**
     * request network data
     */
    protected void requestData() {
        onRequestStart();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    protected void onRequestStart() {

    }

    protected void onRequestSuccess(int code) {

    }

    protected void onRequestError(int code) {
        setFooterType(TYPE_NET_ERROR);
    }

    protected void onRequestFinish() {
        onComplete();
        if (mAdapter.getDatas().size() < 10)
            mRefreshLayout.setNoMoreData();
    }

    protected void onComplete() {
        mRefreshLayout.onLoadComplete();
        mIsRefresh = false;
    }

    //    protected void setListData(ResultBean<PageBean<T>> resultBean) {
//        //is refresh
//        if (mIsRefresh) {
//            //cache the time
//            mTime = resultBean.getTime();
//            AppConfig.getAppConfig(getActivity()).set(CACHE_NAME, mTime);
//
//            //is ExpiryDate
//            if (AppConfig.isExpiryDate(mTime)) {
//                mBeam.getItems().addAll(0, resultBean.getResult().getItems());
//                mAdapter.addItem(0, resultBean.getResult().getItems());
//            } else {
//                mBeam.setItems(resultBean.getResult().getItems());
//                mAdapter.clear();
//                mAdapter.addItem(mBeam.getItems());
//            }
//            mBeam.setNextPageToken(resultBean.getResult().getNextPageToken());
//            mBeam.setPrevPageToken(resultBean.getResult().getPrevPageToken());
//            mExeService.submit(new Runnable() {
//                @Override
//                public void run() {
//                    CacheManager.saveObject(getActivity(), mBeam, CACHE_NAME);
//                }
//            });
//        } else {
//            mAdapter.addItem(resultBean.getResult().getItems());
//        }
//        mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
//        mRefreshLayout.setVisibility(View.VISIBLE);
//    }
    protected void setListData(ResultBean<PageBean<T>> resultBean) {
        //is refresh
        if (mIsRefresh) {
            //cache the time
            mTime = resultBean.getTime();
            mBeam.setItems(resultBean.getResult().getItems());
            mAdapter.clear();
            mAdapter.addItem(mBeam.getItems());
            mBeam.setNextPageToken(resultBean.getResult().getNextPageToken());
            mBeam.setPrevPageToken(resultBean.getResult().getPrevPageToken());
            mExeService.submit(new Runnable() {
                @Override
                public void run() {
                    CacheManager.saveObject(getActivity(), mBeam, CACHE_NAME);
                }
            });
        } else {
            mAdapter.addItem(resultBean.getResult().getItems());
        }
        if (resultBean.getResult().getItems().size() < 10) {
            setFooterType(TYPE_NO_MORE);
        }
        if (mAdapter.getDatas().size() > 0) {
            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            mRefreshLayout.setVisibility(View.VISIBLE);
        } else {
            mErrorLayout.setErrorType(EmptyLayout.NODATA);
        }
    }

    @Override
    public Date getSystemTime() {
        return new Date();
    }

    protected abstract BaseListAdapter<T> getListAdapter();

    protected abstract Type getType();

    protected boolean isNeedFooter() {
        return true;
    }

    protected void setFooterType(int type) {
        switch (type) {
            case TYPE_NORMAL:
            case TYPE_LOADING:
                mFooterText.setText(getResources().getString(R.string.footer_type_loading));
                mFooterProgressBar.setVisibility(View.VISIBLE);
                break;
            case TYPE_NET_ERROR:
                mFooterText.setText(getResources().getString(R.string.footer_type_net_error));
                mFooterProgressBar.setVisibility(View.GONE);
                break;
            case TYPE_ERROR:
                mFooterText.setText(getResources().getString(R.string.footer_type_error));
                mFooterProgressBar.setVisibility(View.GONE);
                break;
            case TYPE_NO_MORE:
                mFooterText.setText(getResources().getString(R.string.footer_type_not_more));
                mFooterProgressBar.setVisibility(View.GONE);
                break;
        }
    }
}
