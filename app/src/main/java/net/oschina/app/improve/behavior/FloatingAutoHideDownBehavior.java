package net.oschina.app.improve.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import net.oschina.app.R;
import net.oschina.app.util.TDevice;

/**
 * 滚动时隐藏的Behavior
 * Created by thanatos on 16/2/17.
 */
public class FloatingAutoHideDownBehavior extends CoordinatorLayout.Behavior<View> {
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();
    private boolean mIsAnimatingOut = false;
    private boolean mIsScrollToBottom = false;

    public FloatingAutoHideDownBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FloatingAutoHideDownBehavior() {
        super();
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dx, int dy, int[] consumed) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);

//        if (!mIsScrollToBottom) {
//            float mPreTranslationY = dy + child.getTranslationY();
//            if (mPreTranslationY <= 0) {
//                child.setTranslationY(0);
//                mIsAnimatingOut = true;
//            }
//            if (mPreTranslationY >= child.getHeight()) {
//                child.setTranslationY(child.getHeight());
//                mIsAnimatingOut = false;
//            }
//            if (mPreTranslationY > 0 && mPreTranslationY < child.getHeight()) {
//                child.setTranslationY(mPreTranslationY);
//                mIsAnimatingOut = dy > 0;
//            }
//        }
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, final View child, View dependency) {
        if (child != null && dependency != null && dependency instanceof NestedScrollView) {
            NestedScrollView s = (NestedScrollView) dependency;

            if (s.getChildCount() > 0 && child.getHeight() > 0) {
                View view = s.getChildAt(s.getChildCount() - 1);
                if (view.getTag(R.id.detail_behavior_content_padding_done) == null) {
                    int paddingBottom = view.getPaddingBottom() + child.getHeight();
                    view.setTag(R.id.detail_behavior_content_padding_done, paddingBottom);
                    view.setPadding(view.getPaddingLeft(),
                            view.getPaddingTop(),
                            view.getPaddingRight(),
                            paddingBottom);
                }
            }

            s.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (v.getChildCount() > 0) {
                        // Grab the last child placed in the ScrollView, we need it to determinate the bottom position.
                        View view = v.getChildAt(v.getChildCount() - 1);
                        // Calculate the scrolldiff
                        int diff = (view.getBottom() - (v.getHeight() + scrollY));
                        // if diff is zero, then the bottom has been reached
                        if (diff == 0) {
                            // notify that we have reached the bottom
                            animateIn(child);
                            mIsScrollToBottom = true;
                        } else {
                            mIsScrollToBottom = false;
                        }
                    }
                }
            });
        }
        return super.layoutDependsOn(parent, child, dependency);
    }

    @Override
    public boolean onStartNestedScroll(final CoordinatorLayout coordinatorLayout, final View child,
                                       final View directTargetChild, final View target, final int nestedScrollAxes) {
        // 滑动时隐藏软键盘
        TDevice.hideSoftKeyboard(coordinatorLayout);

        // Ensure we react to vertical scrolling
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL
                || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target) {
        super.onStopNestedScroll(coordinatorLayout, child, target);

        if (child.getTranslationY() == 0 || child.getTranslationY() == child.getHeight()) return;

        if (mIsAnimatingOut) {
            animateOut(child);
        } else {
            animateIn(child);
        }

    }

    private void animateOut(final View button) {
//        button.animate()
//                .translationY(button.getHeight())
//                .setInterpolator(INTERPOLATOR)
//                .setDuration(200)
//                .setListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        button.setTranslationY(button.getHeight());
//                    }
//                });
    }

    private void animateIn(final View button) {
//        button.animate()
//                .translationY(0)
//                .setInterpolator(INTERPOLATOR)
//                .setDuration(200)
//                .setListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        button.setTranslationY(0);
//                    }
//                });
    }


    /**
     * 点击内容栏唤起底部操作区域
     *
     * @param coordinatorLayout 外部CoordinatorLayout
     * @param contentView       滚动区域
     * @param bottomView        滚动时隐藏底部区域
     */
    public static void showBottomLayout(CoordinatorLayout coordinatorLayout, View contentView, final View bottomView) {
        //coordinatorLayout.onStartNestedScroll(contentView, null, ViewCompat.SCROLL_AXIS_VERTICAL);
        //coordinatorLayout.onNestedPreScroll(bottomView, 0, -1, new int[2]);
        //coordinatorLayout.onStopNestedScroll(null);
//        bottomView.animate()
//                .translationY(0)
//                .setInterpolator(INTERPOLATOR)
//                .setDuration(200)
//                .setListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        bottomView.setTranslationY(0);
//                    }
//                });
    }
}
