package net.oschina.app.fragment;

import net.oschina.app.AppConfig;
import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.base.BaseActivity;
import net.oschina.app.base.BaseFragment;
import net.oschina.app.bean.Constants;
import net.oschina.app.ui.ShareDialog;
import net.oschina.app.ui.ShareDialog.OnSharePlatformClick;
import net.oschina.app.ui.SimpleBackActivity;
import net.oschina.app.util.TDevice;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.utils.OauthHelper;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

/**
 * 浏览器界面
 * 
 * @author kymjs(kymjs123@gmail.com)
 */
@SuppressLint("NewApi")
public class BrowserFragment extends BaseFragment {
    @InjectView(R.id.webview)
    WebView mWebView;
    @InjectView(R.id.browser_back)
    ImageView mImgBack;
    @InjectView(R.id.browser_forward)
    ImageView mImgForward;
    @InjectView(R.id.browser_refresh)
    ImageView mImgRefresh;
    @InjectView(R.id.browser_system_browser)
    ImageView mImgSystemBrowser;
    @InjectView(R.id.browser_bottom)
    LinearLayout mLayoutBottom;
    @InjectView(R.id.progress)
    ProgressBar mProgress;

    public static final String BROWSER_KEY = "browser_url";
    public static final String DEFAULT = "http://www.oschina.net/";

    private int TAG = 1; // 双击事件需要
    private Activity aty;
    private String mCurrentUrl = DEFAULT;

    final UMSocialService mController = UMServiceFactory
            .getUMSocialService("com.umeng.share");

    private Animation animBottomIn, animBottomOut;
    private GestureDetector mGestureDetector;
    private CookieManager cookie;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.browser_back:
            mWebView.goBack();
            break;
        case R.id.browser_forward:
            mWebView.goForward();
            break;
        case R.id.browser_refresh:
            mWebView.loadUrl(mWebView.getUrl());
            break;
        case R.id.browser_system_browser:
            try {
                // 启用外部浏览器
                Uri uri = Uri.parse(mCurrentUrl);
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                aty.startActivity(it);
            } catch (Exception e) {
                AppContext.showToast("网页地址错误");
            }
            break;
        }
    }

    @Override
    public void initView(View view) {
        initWebView();
        initBarAnim();
        mImgBack.setOnClickListener(this);
        mImgForward.setOnClickListener(this);
        mImgRefresh.setOnClickListener(this);
        mImgSystemBrowser.setOnClickListener(this);

        mGestureDetector = new GestureDetector(aty, new MyGestureListener());
        mWebView.loadUrl(mCurrentUrl);
        mWebView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWebView.destroy();
    }

    @Override
    public void initData() {
        Bundle bundle = getActivity().getIntent().getBundleExtra(
                SimpleBackActivity.BUNDLE_KEY_ARGS);
        if (bundle != null) {
            mCurrentUrl = bundle.getString(BROWSER_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_browser, container,
                false);
        aty = getActivity();
        ButterKnife.inject(this, rootView);
        initData();
        initView(rootView);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.browser_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.public_menu_shared:
            showSharedDialog();
            break;
        }
        return true;
    }

    /**
     * 初始化上下栏的动画并设置结束监听事件
     */
    private void initBarAnim() {
        animBottomIn = AnimationUtils.loadAnimation(aty, R.anim.anim_bottom_in);
        animBottomOut = AnimationUtils.loadAnimation(aty,
                R.anim.anim_bottom_out);
        animBottomIn.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                mLayoutBottom.setVisibility(View.VISIBLE);
            }
        });
        animBottomOut.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                mLayoutBottom.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 打开分享dialog
     */
    private void showSharedDialog() {
        final ShareDialog dialog = new ShareDialog(getActivity());
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setTitle(R.string.share_to);
        dialog.setOnPlatformClickListener(new OnSharePlatformClick() {
            @Override
            public void onPlatformClick(int id) {

                switch (id) {
                case R.id.ly_share_weichat_circle:
                    shareToWeiChatCircle();
                    break;
                case R.id.ly_share_weichat:
                    shareToWeiChat();
                    break;
                case R.id.ly_share_sina_weibo:
                    shareToSinaWeibo();
                    break;
                case R.id.ly_share_qq:
                    shareToQQ(SHARE_MEDIA.QQ);
                    break;
                case R.id.ly_share_copy_link:
                    TDevice.copyTextToBoard(mCurrentUrl);
                    break;
                case R.id.ly_share_more_option:
                    TDevice.showSystemShareOption(getActivity(),
                            mWebView.getTitle(), mCurrentUrl);
                    break;
                default:
                    break;
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    protected void shareToQQ(SHARE_MEDIA media) {
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(getActivity(),
                Constants.QQ_APPID, Constants.QQ_APPKEY);
        qqSsoHandler.setTargetUrl(mCurrentUrl);
        qqSsoHandler.setTitle(getShareTitle());
        qqSsoHandler.addToSocialSDK();
        mController.setShareImage(getShareImg());
        mController.setShareContent(getShareContent());
        mController.postShare(getActivity(), media, null);
    }

    @SuppressWarnings("deprecation")
    private void shareToWeiChatCircle() {
        // 支持微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(getActivity(),
                Constants.WEICHAT_APPID);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
        // 设置微信朋友圈分享内容
        CircleShareContent circleMedia = new CircleShareContent();
        // 设置朋友圈title
        circleMedia.setTitle(getShareTitle());
        circleMedia.setShareContent(getShareContent());
        circleMedia.setShareImage(getShareImg());
        circleMedia.setTargetUrl(mCurrentUrl);
        mController.setShareMedia(circleMedia);
        mController.postShare(getActivity(), SHARE_MEDIA.WEIXIN_CIRCLE, null);
    }

    @SuppressWarnings("deprecation")
    private void shareToWeiChat() {
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(getActivity(),
                Constants.WEICHAT_APPID);
        wxHandler.addToSocialSDK();
        // 设置微信好友分享内容
        WeiXinShareContent weixinContent = new WeiXinShareContent();
        // 设置分享文字
        weixinContent.setShareContent(getShareContent());
        // 设置title
        weixinContent.setTitle(getShareTitle());
        // 设置分享内容跳转URL
        weixinContent.setTargetUrl(mCurrentUrl);
        // 设置分享图片
        weixinContent.setShareImage(getShareImg());
        mController.setShareMedia(weixinContent);
        mController.postShare(getActivity(), SHARE_MEDIA.WEIXIN, null);
    }

    private void shareToSinaWeibo() {
        // 设置新浪微博SSO handler
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        if (OauthHelper.isAuthenticated(getActivity(), SHARE_MEDIA.SINA)) {
            shareContent(SHARE_MEDIA.SINA);
        } else {
            mController.doOauthVerify(getActivity(), SHARE_MEDIA.SINA,
                    new UMAuthListener() {

                        @Override
                        public void onStart(SHARE_MEDIA arg0) {}

                        @Override
                        public void onError(SocializeException arg0,
                                SHARE_MEDIA arg1) {}

                        @Override
                        public void onComplete(Bundle arg0, SHARE_MEDIA arg1) {
                            shareContent(SHARE_MEDIA.SINA);
                        }

                        @Override
                        public void onCancel(SHARE_MEDIA arg0) {}
                    });
        }
    }

    /**
     * 载入链接之前会被调用
     * 
     * @param view
     *            WebView
     * @param url
     *            链接地址
     */
    protected void onUrlLoading(WebView view, String url) {
        mProgress.setVisibility(View.VISIBLE);
        cookie.setCookie(url,
                AppContext.getInstance().getProperty(AppConfig.CONF_COOKIE));
    }

    /**
     * 链接载入成功后会被调用
     * 
     * @param view
     *            WebView
     * @param url
     *            链接地址
     */
    protected void onUrlFinished(WebView view, String url) {
        mCurrentUrl = url;
        mProgress.setVisibility(View.GONE);
    }

    /**
     * 当前WebView显示页面的标题
     * 
     * @param view
     *            WebView
     * @param title
     *            web页面标题
     */
    protected void onWebTitle(WebView view, String title) {
        if (aty != null && mWebView != null) { // 必须做判断，由于webview加载属于耗时操作，可能会本Activity已经关闭了才被调用
            ((BaseActivity) aty).setActionBarTitle(mWebView.getTitle());
        }
    }

    /**
     * 当前WebView显示页面的图标
     * 
     * @param view
     *            WebView
     * @param icon
     *            web页面图标
     */
    protected void onWebIcon(WebView view, Bitmap icon) {}

    /**
     * 初始化浏览器设置信息
     */
    private void initWebView() {
        cookie = CookieManager.getInstance();
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true); // 启用支持javascript
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);// 优先使用缓存
        webSettings.setAllowFileAccess(true);// 可以访问文件
        webSettings.setBuiltInZoomControls(true);// 支持缩放
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            webSettings.setPluginState(PluginState.ON);
            webSettings.setDisplayZoomControls(false);// 支持缩放
        }
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setWebChromeClient(new MyWebChromeClient());
    }

    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            onWebTitle(view, title);
        }

        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            super.onReceivedIcon(view, icon);
            onWebIcon(view, icon);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) { // 进度
            super.onProgressChanged(view, newProgress);
            // if (newProgress == 100) {
            // mProgress.setVisibility(View.GONE);
            // } else {
            // mProgress.setVisibility(View.VISIBLE);
            // mProgress.setProgress(newProgress);
            // }
            if (newProgress > 90) {
                mProgress.setVisibility(View.GONE);
            }
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            onUrlLoading(view, url);
            boolean flag = super.shouldOverrideUrlLoading(view, url);
            mCurrentUrl = url;
            return flag;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            onUrlFinished(view, url);
        }
    }

    private class MyGestureListener extends SimpleOnGestureListener {

        @Override
        public boolean onDoubleTap(MotionEvent e) {// webview的双击事件
            if (TAG % 2 == 0) {
                TAG++;
                mLayoutBottom.startAnimation(animBottomIn);
            } else {
                TAG++;
                mLayoutBottom.startAnimation(animBottomOut);
            }
            return super.onDoubleTap(e);
        }
    }

    private void shareContent(SHARE_MEDIA media) {
        mController.setShareContent(getShareContent() + mCurrentUrl);
        mController.directShare(getActivity(), media, null);
    }

    protected UMImage getShareImg() {
        UMImage img = new UMImage(getActivity(), R.drawable.ic_launcher);
        return img;
    }

    private String getShareTitle() {
        return mWebView.getTitle();
    }

    private String getShareContent() {
        return mWebView.getTitle();
    }
}
