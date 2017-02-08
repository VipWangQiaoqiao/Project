package net.oschina.app.improve.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

import net.oschina.app.util.TDevice;

/**
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */

public class SoftKeyboardUtil {

    public static void showKeyboard(final View view) {
        view.requestFocus();
        InputMethodManager inputManager =
                (InputMethodManager) view.getContext().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(view, 0);
    }

    public static void hideKeyboard(final View view) {
        InputMethodManager imm =
                (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    private static int LAST_SAVE_KEYBOARD_HEIGHT = 0;

    private static boolean saveKeyboardHeight(final Context context, int keyboardHeight) {
        if (LAST_SAVE_KEYBOARD_HEIGHT == keyboardHeight) {
            return false;
        }

        if (keyboardHeight < 0) {
            return false;
        }

        LAST_SAVE_KEYBOARD_HEIGHT = keyboardHeight;
        Log.d("KeyBordUtil", String.format("save keyboard: %d", keyboardHeight));

        return true;
    }


    public static int getKeyboardHeight(final Context context) {
        if (LAST_SAVE_KEYBOARD_HEIGHT == 0) {
            LAST_SAVE_KEYBOARD_HEIGHT = getMinPanelHeight(context.getResources());
        }

        return LAST_SAVE_KEYBOARD_HEIGHT;
    }

    public static int getValidPanelHeight(final Context context) {
        final int maxPanelHeight = getMaxPanelHeight(context.getResources());
        final int minPanelHeight = getMinPanelHeight(context.getResources());

        int validPanelHeight = getKeyboardHeight(context);

        validPanelHeight = Math.max(minPanelHeight, validPanelHeight);
        validPanelHeight = Math.min(maxPanelHeight, validPanelHeight);
        return validPanelHeight;
    }


    private static int MAX_PANEL_HEIGHT = 0;
    private static int MIN_PANEL_HEIGHT = 0;
    private static int MIN_KEYBOARD_HEIGHT = 0;

    public static int getMaxPanelHeight(final Resources res) {
        if (MAX_PANEL_HEIGHT == 0) {
            MAX_PANEL_HEIGHT = (int) TDevice.dipToPx(res, 280);
        }

        return MAX_PANEL_HEIGHT;
    }

    public static int getMinPanelHeight(final Resources res) {
        if (MIN_PANEL_HEIGHT == 0) {
            MIN_PANEL_HEIGHT = (int) TDevice.dipToPx(res, 92);
        }

        return MIN_PANEL_HEIGHT;
    }

    public static int getMinKeyboardHeight(Context context) {
        if (MIN_KEYBOARD_HEIGHT == 0) {
            MIN_KEYBOARD_HEIGHT = (int) TDevice.dipToPx(context.getResources(), 92);
        }
        return MIN_KEYBOARD_HEIGHT;
    }


    /**
     * Recommend invoked by {@link Activity#onCreate(Bundle)}
     * For align the height of the keyboard to {@code target} as much as possible.
     * For save the refresh the keyboard height to shared-preferences.
     *
     * @param activity contain the view
     * @param target   whose height will be align to the keyboard height.
     * @param listener the listener to listen in: keyboard is showing or not.
     * @see #saveKeyboardHeight(Context, int)
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static ViewTreeObserver.OnGlobalLayoutListener attach(final Activity activity, IPanelHeightTarget target,
                                                                 /** Nullable **/OnKeyboardShowingListener listener) {
        final ViewGroup contentView = (ViewGroup) activity.findViewById(android.R.id.content);
        final boolean isFullScreen = UiUtil.isFullScreen(activity);
        final boolean isTranslucentStatus = UiUtil.isTranslucentStatus(activity);
        final boolean isFitSystemWindows = UiUtil.isFitsSystemWindows(activity);

        // get the screen height.
        final Display display = activity.getWindowManager().getDefaultDisplay();
        final int screenHeight;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            final Point screenSize = new Point();
            display.getSize(screenSize);
            screenHeight = screenSize.y;
        } else {
            //noinspection deprecation
            screenHeight = display.getHeight();
        }

        ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = new KeyboardStatusListener(
                isFullScreen,
                isTranslucentStatus,
                isFitSystemWindows,
                contentView,
                target,
                listener,
                screenHeight);

        contentView.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
        return globalLayoutListener;
    }

    /**
     * @see #attach(Activity, IPanelHeightTarget, OnKeyboardShowingListener)
     */
    public static ViewTreeObserver.OnGlobalLayoutListener attach(final Activity activity, IPanelHeightTarget target) {
        return attach(activity, target, null);
    }

    /**
     * Remove the OnGlobalLayoutListener from the activity root view
     *
     * @param activity same activity used in {@link #attach} method
     * @param l        ViewTreeObserver.OnGlobalLayoutListener returned by {@link #attach} method
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void detach(Activity activity, ViewTreeObserver.OnGlobalLayoutListener l) {
        ViewGroup contentView = (ViewGroup) activity.findViewById(android.R.id.content);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            contentView.getViewTreeObserver().removeOnGlobalLayoutListener(l);
        } else {
            //noinspection deprecation
            contentView.getViewTreeObserver().removeGlobalOnLayoutListener(l);
        }
    }

    private static class KeyboardStatusListener implements ViewTreeObserver.OnGlobalLayoutListener {
        private final static String TAG = "KeyboardStatusListener";

        private int previousDisplayHeight = 0;
        private final ViewGroup contentView;
        private final IPanelHeightTarget panelHeightTarget;
        private final boolean isFullScreen;
        private final boolean isTranslucentStatus;
        private final boolean isFitSystemWindows;
        private final int statusBarHeight;
        private boolean lastKeyboardShowing;
        private final OnKeyboardShowingListener keyboardShowingListener;
        private final int screenHeight;

        private boolean isOverlayLayoutDisplayHContainStatusBar = false;

        KeyboardStatusListener(boolean isFullScreen, boolean isTranslucentStatus,
                               boolean isFitSystemWindows,
                               ViewGroup contentView, IPanelHeightTarget panelHeightTarget,
                               OnKeyboardShowingListener listener, int screenHeight) {
            this.contentView = contentView;
            this.panelHeightTarget = panelHeightTarget;
            this.isFullScreen = isFullScreen;
            this.isTranslucentStatus = isTranslucentStatus;
            this.isFitSystemWindows = isFitSystemWindows;
            this.statusBarHeight = net.oschina.app.improve.utils.UiUtil.getStatusBarHeight(contentView.getContext());
            this.keyboardShowingListener = listener;
            this.screenHeight = screenHeight;
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
        @Override
        public void onGlobalLayout() {
            final View userRootView = contentView.getChildAt(0);
            final View actionBarOverlayLayout = (View) contentView.getParent();

            // Step 1. calculate the current display frame's height.
            Rect r = new Rect();

            final int displayHeight;
            if (isTranslucentStatus) {
                // status bar translucent.

                // In the case of the Theme is Status-Bar-Translucent, we calculate the keyboard
                // state(showing/hiding) and the keyboard height based on assuming that the
                // displayHeight includes the height of the status bar.

                actionBarOverlayLayout.getWindowVisibleDisplayFrame(r);

                final int overlayLayoutDisplayHeight = (r.bottom - r.top);

                if (!isOverlayLayoutDisplayHContainStatusBar) {
                    // in case of the keyboard is hiding, the display height of the
                    // action-bar-overlay-layout would be possible equal to the screen height.

                    // and if isOverlayLayoutDisplayHContainStatusBar has already been true, the
                    // display height of action-bar-overlay-layout must include the height of the
                    // status bar always.
                    isOverlayLayoutDisplayHContainStatusBar = overlayLayoutDisplayHeight == screenHeight;
                }

                if (!isOverlayLayoutDisplayHContainStatusBar) {
                    // In normal case, we need to plus the status bar height manually.
                    displayHeight = overlayLayoutDisplayHeight + statusBarHeight;
                } else {
                    // In some case(such as Samsung S7 edge), the height of the action-bar-overlay-layout
                    // display bound already included the height of the status bar, in this case we
                    // doesn't need to plus the status bar height manually.
                    displayHeight = overlayLayoutDisplayHeight;
                }

            } else {
                userRootView.getWindowVisibleDisplayFrame(r);
                displayHeight = (r.bottom - r.top);
            }

            calculateKeyboardHeight(displayHeight);
            calculateKeyboardShowing(displayHeight);

            previousDisplayHeight = displayHeight;
        }

        private void calculateKeyboardHeight(final int displayHeight) {
            // first result.
            if (previousDisplayHeight == 0) {
                previousDisplayHeight = displayHeight;

                // init the panel height for target.
                panelHeightTarget.refreshHeight(SoftKeyboardUtil.getValidPanelHeight(getContext()));
                return;
            }

            int keyboardHeight;
            if (isHandleByPlaceholder(isFullScreen, isTranslucentStatus,
                    isFitSystemWindows)) {
                // the height of content parent = contentView.height + actionBar.height
                final View actionBarOverlayLayout = (View) contentView.getParent();

                keyboardHeight = actionBarOverlayLayout.getHeight() - displayHeight;

                Log.d(TAG, String.format("action bar over layout %d display height: %d",
                        ((View) contentView.getParent()).getHeight(), displayHeight));

            } else {
                keyboardHeight = Math.abs(displayHeight - previousDisplayHeight);
            }
            // no change.
            if (keyboardHeight <= getMinKeyboardHeight(getContext())) {
                return;
            }

            Log.d(TAG, String.format("pre display height: %d display height: %d keyboard: %d ",
                    previousDisplayHeight, displayHeight, keyboardHeight));

            // influence from the layout of the Status-bar.
            if (keyboardHeight == this.statusBarHeight) {
                Log.w(TAG, String.format("On global layout change get keyboard height just equal" +
                        " statusBar height %d", keyboardHeight));
                return;
            }

            // save the keyboardHeight
            boolean changed = SoftKeyboardUtil.saveKeyboardHeight(getContext(), keyboardHeight);
            if (changed) {
                final int validPanelHeight = SoftKeyboardUtil.getValidPanelHeight(getContext());
                if (this.panelHeightTarget.getHeight() != validPanelHeight) {
                    // Step3. refresh the panel's height with valid-panel-height which refer to
                    // the last keyboard height
                    this.panelHeightTarget.refreshHeight(validPanelHeight);
                }
            }
        }

        private int maxOverlayLayoutHeight;

        private void calculateKeyboardShowing(final int displayHeight) {

            boolean isKeyboardShowing;

            // the height of content parent = contentView.height + actionBar.height
            final View actionBarOverlayLayout = (View) contentView.getParent();
            // in the case of FragmentLayout, this is not real ActionBarOverlayLayout, it is
            // LinearLayout, and is a child of DecorView, and in this case, its top-padding would be
            // equal to the height of status bar, and its height would equal to DecorViewHeight -
            // NavigationBarHeight.
            final int actionBarOverlayLayoutHeight = actionBarOverlayLayout.getHeight() -
                    actionBarOverlayLayout.getPaddingTop();

            if (isHandleByPlaceholder(isFullScreen, isTranslucentStatus,
                    isFitSystemWindows)) {
                if (!isTranslucentStatus &&
                        actionBarOverlayLayoutHeight - displayHeight == this.statusBarHeight) {
                    // handle the case of status bar layout, not keyboard active.
                    isKeyboardShowing = lastKeyboardShowing;
                } else {
                    isKeyboardShowing = actionBarOverlayLayoutHeight > displayHeight;
                }

            } else {

                final int phoneDisplayHeight = contentView.getResources().getDisplayMetrics().heightPixels;
                if (!isTranslucentStatus &&
                        phoneDisplayHeight == actionBarOverlayLayoutHeight) {
                    // no space to settle down the status bar, switch to fullscreen,
                    // only in the case of paused and opened the fullscreen page.
                    Log.w(TAG, String.format("skip the keyboard status calculate, the current" +
                                    " activity is paused. and phone-display-height %d," +
                                    " root-height+actionbar-height %d", phoneDisplayHeight,
                            actionBarOverlayLayoutHeight));
                    return;

                }

                if (maxOverlayLayoutHeight == 0) {
                    // non-used.
                    isKeyboardShowing = lastKeyboardShowing;
                } else {
                    isKeyboardShowing = displayHeight < maxOverlayLayoutHeight - getMinKeyboardHeight(getContext());
                }

                maxOverlayLayoutHeight = Math.max(maxOverlayLayoutHeight, actionBarOverlayLayoutHeight);
            }

            if (lastKeyboardShowing != isKeyboardShowing) {
                Log.d(TAG, String.format("displayHeight %d actionBarOverlayLayoutHeight %d " +
                                "keyboard status change: %B",
                        displayHeight, actionBarOverlayLayoutHeight, isKeyboardShowing));
                this.panelHeightTarget.onKeyboardShowing(isKeyboardShowing);
                if (keyboardShowingListener != null) {
                    keyboardShowingListener.onKeyboardShowing(isKeyboardShowing);
                }
            }

            lastKeyboardShowing = isKeyboardShowing;

        }

        private Context getContext() {
            return contentView.getContext();
        }
    }

    /**
     * The interface is used to listen the keyboard showing state.
     *
     * @see #attach(Activity, IPanelHeightTarget, OnKeyboardShowingListener)
     * @see KeyboardStatusListener#calculateKeyboardShowing(int)
     */
    public interface OnKeyboardShowingListener {

        /**
         * Keyboard showing state callback method.
         * <p>
         * This method is invoked in {@link ViewTreeObserver.OnGlobalLayoutListener#onGlobalLayout()} which is one of the
         * ViewTree lifecycle callback methods. So deprecating those time-consuming operation(I/O, complex calculation,
         * alloc objects, etc.) here from blocking main ui thread is recommended.
         * </p>
         *
         * @param isShowing Indicate whether keyboard is showing or not.
         */
        void onKeyboardShowing(boolean isShowing);

    }

    public static boolean isHandleByPlaceholder(boolean isFullScreen, boolean isTranslucentStatus,
                                                boolean isFitsSystem) {
        return isFullScreen || (isTranslucentStatus && !isFitsSystem);
    }

    public interface IPanelHeightTarget {

        /**
         * for handle the panel's height, will be equal to the keyboard height which had saved last time.
         */
        void refreshHeight(final int panelHeight);

        /**
         * @return get the height of target-view.
         */
        int getHeight();

        /**
         * Be invoked by onGlobalLayoutListener call-back.
         *
         * @param showing whether the keyboard is showing or not.
         */
        void onKeyboardShowing(boolean showing);
    }

}