package net.oschina.app.improve.detail.fragments;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.view.ViewGroup;

import net.oschina.app.R;
import net.oschina.app.improve.base.fragments.BaseFragment;
import net.oschina.app.improve.detail.contract.DetailContract;
import net.oschina.app.improve.widget.OWebView;

/**
 * Created by JuQiu
 * on 16/6/20.
 */

public abstract class DetailFragment<Data, DataView extends DetailContract.View, Operator extends DetailContract.Operator<Data, DataView>> extends BaseFragment implements DetailContract.View {
    Operator mOperator;
    OWebView mWebView;
    private NestedScrollView mScrollView;
    private View mScrollTargetView;

    public Operator getOperator() {
        return mOperator;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onAttach(Context context) {
        this.mOperator = (Operator) context;
        this.mOperator.setDataView((DataView) this);
        super.onAttach(context);
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        initWebView(R.id.lay_webview);
    }

    void initWebView(@IdRes int layId) {
        OWebView webView = new OWebView(getActivity());
        ((ViewGroup) mRoot.findViewById(layId)).addView(webView);
        mWebView = webView;
    }

    void setBodyContent(String body) {
        mWebView.loadDetailDataAsync(body, new Runnable() {
            @Override
            public void run() {
                Operator operator = mOperator;
                if (operator != null) {
                    operator.hideLoading();
                }
            }
        });
    }

    void setCommentCount(int count) {
        if (mOperator != null) {
            mOperator.setCommentCount(count);
        }
    }

    void registerScroller(NestedScrollView nestedScrollView, View target) {
        mScrollView = nestedScrollView;
        mScrollTargetView = target;
    }

    private int mScrollYPoint = -1;

    @Override
    public void scrollToComment() {
        NestedScrollView nestedScrollView = mScrollView;
        View target = mScrollTargetView;
        if (nestedScrollView != null && target != null) {
            int curY = nestedScrollView.getScrollY();
            int targetY = target.getTop();
            if (targetY > 0) {
                if (curY == targetY && targetY == mScrollYPoint) {
                    nestedScrollView.fullScroll(View.FOCUS_UP);
                } else {
                    if (mScrollYPoint == -1) {
                        nestedScrollView.smoothScrollTo(0, targetY);
                        mScrollYPoint = curY;
                        return;
                    }
                    if (curY > targetY) {
                        // 当前在评论之后
                        if (mScrollYPoint < targetY) {
                            nestedScrollView.smoothScrollTo(0, mScrollYPoint);
                        } else {
                            nestedScrollView.fullScroll(View.FOCUS_UP);
                        }
                        mScrollYPoint = curY;
                    } else {
                        // 当前在评论之前
                        nestedScrollView.smoothScrollTo(0, mScrollYPoint);
                        if (mScrollYPoint < curY) {
                            mScrollYPoint = -1;
                        } else {
                            mScrollYPoint = 0;
                        }
                    }
                }
            } else {
                if (mScrollYPoint == -1) {
                    nestedScrollView.fullScroll(View.FOCUS_DOWN);
                    mScrollYPoint = curY;
                } else {
                    nestedScrollView.smoothScrollTo(0, mScrollYPoint);
                    mScrollYPoint = -1;
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        OWebView webView = mWebView;
        if (webView != null) {
            webView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        OWebView webView = mWebView;
        if (webView != null) {
            webView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        OWebView view = mWebView;
        if (view != null) {
            mWebView = null;
            view.destroy();
        }

        mScrollTargetView = null;
        NestedScrollView nestedScrollView = mScrollView;
        if (nestedScrollView != null) {
            mScrollView = null;
            nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) null);
        }

        mOperator = null;

        super.onDestroy();
    }
}
