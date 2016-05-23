package net.oschina.app.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.oschina.app.R;

/**
 * Created by huanghaibin
 * on 2016/5/23.
 */
@SuppressWarnings("unused")
public class SuperRefreshLayout extends SwipeRefreshLayout implements AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener {
    private ListView mListView;

    private View mFooterView;

    private ProgressBar mProgressBar;
    private TextView mTextView;
    private int mTouchSlop;


    private SuperRefreshLayoutListener mListener;

    private boolean mIsOnLoading = false;

    private boolean mCanLoadMore = false;

    private int mYDown;

    private int mLastY;

    private int mTextColor;
    private int mFooterBackground;

    private boolean mIsNeedFooter;

    public SuperRefreshLayout(Context context) {
        this(context, null);
    }

    @SuppressLint("InflateParams")
    public SuperRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        setOnRefreshListener(this);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RefreshLayout);
        mTextColor = array.getColor(R.styleable.RefreshLayout_footer_view_text_color, 0xff6c6c6c);
        mFooterBackground = array.getColor(R.styleable.RefreshLayout_footer_view_background, 0xfff0f0f0);
        mIsNeedFooter = array.getBoolean(R.styleable.RefreshLayout_is_need_footer, true);
        array.recycle();

        mFooterView = LayoutInflater.from(getContext()).inflate(R.layout.layout_list_view_footer, null);
        mTextView = (TextView) mFooterView.findViewById(R.id.tv_footer);
        mProgressBar = (ProgressBar) mFooterView.findViewById(R.id.pb_footer);
        mTextView.setTextColor(mTextColor);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // 滚动时到了最底部也可以加载更多
        if (canLoad()) {
            loadData();
        }
    }

    @Override
    public void onRefresh() {
        if (mListener != null) {
            mListener.onRefreshing();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // 初始化ListView对象
        if (mListView == null) {
            getListView();
        }
    }

    /**
     * 获取ListView并添加Footer
     */
    private void getListView() {
        int child = getChildCount();
        if (child > 0) {
            View childView = getChildAt(0);
            if (childView instanceof ListView) {
                mListView = (ListView) childView;
                // 设置滚动监听器给ListView, 使得滚动的情况下也可以自动加载
                mListView.setOnScrollListener(this);
                mFooterView.setBackgroundColor(mFooterBackground);
                if (mIsNeedFooter) {
                    mListView.addFooterView(mFooterView);
                }
            }
        }
    }

    public void onLoadMoreFinish() {
        this.mProgressBar.setVisibility(GONE);
        this.mTextView.setText("点击加载更多");
    }

    public void removeFooterView() {
        this.mListView.removeFooterView(this.mFooterView);
    }

    public void setCanLoadMore() {
        this.mProgressBar.setVisibility(VISIBLE);
        this.mTextView.setText("正在加载...");
        this.mCanLoadMore = true;
    }

    public void setLoadingText(String paramString) {
        this.mTextView.setText(paramString);
    }

    public void setNoMoreData() {
        this.mProgressBar.setVisibility(GONE);
        this.mTextView.setText("没有更多数据了");
        this.mCanLoadMore = false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // 按下
                mYDown = (int) event.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:
                // 移动
                mLastY = (int) event.getRawY();
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * 是否可以加载更多, 条件是到了最底部, listview不在加载中, 且为上拉操作.
     *
     * @return 是否可以加载更多
     */
    private boolean canLoad() {
        return isInBottom() && !mIsOnLoading && isPullUp();
    }

    /**
     * 如果到了最底部,而且是上拉操作.那么执行onLoad方法
     */
    private void loadData() {
        if (mListener != null) {
            setIsOnLoading(true);
            mListener.onLoadMore();
        }
    }

    /**
     * 是否是上拉操作
     *
     * @return 是否是上拉操作
     */
    private boolean isPullUp() {
        return (mYDown - mLastY) >= mTouchSlop;
    }

    /**
     * 设置正在加载
     *
     * @param loading loading
     */
    public void setIsOnLoading(boolean loading) {
        mIsOnLoading = loading;
        if (!mIsOnLoading) {
            mYDown = 0;
            mLastY = 0;
        }
    }


    /**
     * 判断是否到了最底部
     */
    private boolean isInBottom() {
        return (mListView != null && mListView.getAdapter() != null)
                && mListView.getLastVisiblePosition() == (mListView.getAdapter().getCount() - 1);
    }


    public interface SuperRefreshLayoutListener {
        void onRefreshing();

        void onLoadMore();
    }


    /**
     * 加载结束记得调用
     */
    public void onLoadComplete() {
        setIsOnLoading(false);
        setRefreshing(false);
    }

    /**
     * set
     *
     * @param loadListener loadListener
     */
    public void setSuperRefreshLayoutListener(SuperRefreshLayoutListener loadListener) {
        mListener = loadListener;
    }
}
