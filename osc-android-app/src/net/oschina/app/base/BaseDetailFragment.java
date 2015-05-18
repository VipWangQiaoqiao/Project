package net.oschina.app.base;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.Comment;
import net.oschina.app.bean.Constants;
import net.oschina.app.bean.Entity;
import net.oschina.app.bean.Report;
import net.oschina.app.bean.Result;
import net.oschina.app.bean.ResultBean;
import net.oschina.app.cache.CacheManager;
import net.oschina.app.ui.DetailActivity;
import net.oschina.app.ui.ReportDialog;
import net.oschina.app.ui.ShareDialog;
import net.oschina.app.ui.ShareDialog.OnSharePlatformClick;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.HTMLUtil;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;
import net.oschina.app.util.XmlUtils;

import org.apache.http.Header;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.internal.widget.ListPopupWindow;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
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

public abstract class BaseDetailFragment extends BaseFragment implements
        OnItemClickListener {

    public static final String INTENT_ACTION_COMMENT_CHANGED = "INTENT_ACTION_COMMENT_CHAGED";

    final UMSocialService mController = UMServiceFactory
            .getUMSocialService("com.umeng.share");

    private ListPopupWindow mMenuWindow;
    private MenuAdapter mMenuAdapter;

    protected EmptyLayout mEmptyLayout;

    public int mCommentCount = 0;

    protected WebView mWebView;

    protected AsyncHttpResponseHandler mCommentHandler = new AsyncHttpResponseHandler() {

        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
            try {
                ResultBean rsb = XmlUtils.toBean(ResultBean.class,
                        new ByteArrayInputStream(arg2));
                Result res = rsb.getResult();
                if (res.OK()) {
                    hideWaitDialog();
                    AppContext.showToastShort(R.string.comment_publish_success);

                    commentPubSuccess(rsb.getComment());
                } else {
                    hideWaitDialog();
                    AppContext.showToastShort(res.getErrorMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                onFailure(arg0, arg1, arg2, e);
            }
            ((DetailActivity) getActivity()).emojiFragment.clean();
        }

        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                Throwable arg3) {
            hideWaitDialog();
            AppContext.showToastShort(R.string.comment_publish_faile);
        }

        @Override
        public void onFinish() {
            ((DetailActivity) getActivity()).emojiFragment.hideAllKeyBoard();
        };
    };

    protected void recycleWebView() {
        if (mWebView != null) {
            mWebView.setVisibility(View.GONE);
            mWebView.removeAllViews();
            mWebView.destroy();
            mWebView = null;
        }
    }

    protected void onCommentChanged(int opt, int id, int catalog,
            boolean isBlog, Comment comment) {}

    private AsyncTask<String, Void, Entity> mCacheTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMenuAdapter = new MenuAdapter();
        setHasOptionsMenu(true);

        mController.getConfig().closeToast();
    }

    protected boolean hasReportMenu() {
        return false;
    }

    @Override
    public void onDestroyView() {
        recycleWebView();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        cancelReadCache();
        recycleWebView();
        super.onDestroy();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestData(false);
    }

    protected String getCacheKey() {
        return null;
    }

    protected Entity parseData(InputStream is) throws Exception {
        return null;
    }

    protected Entity readData(Serializable seri) {
        return null;
    }

    protected void sendRequestData() {}

    protected void requestData(boolean refresh) {
        String key = getCacheKey();
        if (TDevice.hasInternet()
                && (!CacheManager.isExistDataCache(getActivity(), key) || refresh)) {
            sendRequestData();
        } else {
            readCacheData(key);
        }
    }

    // 刷新数据
    protected void sendRefresh() {
        sendRequestData();
    }

    private void readCacheData(String cacheKey) {
        cancelReadCache();
        mCacheTask = new CacheTask(getActivity()).execute(cacheKey);
    }

    private void cancelReadCache() {
        if (mCacheTask != null) {
            mCacheTask.cancel(true);
            mCacheTask = null;
        }
    }

    private class CacheTask extends AsyncTask<String, Void, Entity> {
        private final WeakReference<Context> mContext;

        private CacheTask(Context context) {
            mContext = new WeakReference<Context>(context);
        }

        @Override
        protected Entity doInBackground(String... params) {
            if (mContext.get() != null) {
                Serializable seri = CacheManager.readObject(mContext.get(),
                        params[0]);
                if (seri == null) {
                    return null;
                } else {
                    return readData(seri);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Entity entity) {
            super.onPostExecute(entity);
            if (entity != null) {
                executeOnLoadDataSuccess(entity);
            } else {
                executeOnLoadDataError(null);
            }
            executeOnLoadFinish();
        }
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

    protected AsyncHttpResponseHandler mHandler = new AsyncHttpResponseHandler() {

        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
            try {
                Entity entity = parseData(new ByteArrayInputStream(arg2));
                if (entity != null) {
                    mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                    executeOnLoadDataSuccess(entity);
                    saveCache(entity);
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
            // executeOnLoadDataError(arg3.getMessage());
            readCacheData(getCacheKey());
        }
    };

    private boolean mIsFavorited;

    protected void saveCache(Entity entity) {
        new SaveCacheTask(getActivity(), entity, getCacheKey()).execute();
    }

    protected void executeOnLoadDataSuccess(Entity entity) {}

    protected void executeOnLoadDataError(String object) {
        mEmptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
        mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mState = STATE_REFRESH;
                mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                requestData(true);
            }
        });
    }

    protected void executeOnLoadFinish() {}

    protected void onFavoriteChanged(boolean flag) {
        ((DetailActivity) getActivity()).toolFragment.setFavorite(flag);
    }

    protected int getFavoriteTargetId() {
        return -1;
    }

    protected int getFavoriteTargetType() {
        return -1;
    }

    protected String getShareUrl() {
        return "";
    }

    protected String getShareTitle() {
        return getString(R.string.share_title);
    }

    protected String getShareContent() {
        return "";
    }

    /***
     * 获取去除html标签的body
     * 
     * @param body
     * @return
     */
    protected String getFilterHtmlBody(String body) {
        if (body == null)
            return "";
        return HTMLUtil.delHTMLTag(body.trim());
    }

    protected UMImage getShareImg() {
        UMImage img = new UMImage(getActivity(), R.drawable.ic_share);
        return img;
    }

    protected void commentPubSuccess(Comment comment) {}

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        if (position == 0) {
            handleFavoriteOrNot();
            handleShare();
        } else if (position == 1) {
            onReportMenuClick();
        } else if (position == 2) {

        }
        if (mMenuWindow != null) {
            mMenuWindow.dismiss();
            mMenuWindow = null;
        }
    }

    private final AsyncHttpResponseHandler mReportHandler = new TextHttpResponseHandler() {

        @Override
        public void onSuccess(int arg0, Header[] arg1, String arg2) {
            if (TextUtils.isEmpty(arg2)) {
                AppContext.showToastShort(R.string.tip_report_success);
            } else {
                AppContext.showToastShort(new String(arg2));
            }
        }

        @Override
        public void onFailure(int arg0, Header[] arg1, String arg2,
                Throwable arg3) {
            AppContext.showToastShort(R.string.tip_report_faile);
        }

        @Override
        public void onFinish() {
            hideWaitDialog();
        }
    };

    public void onReportMenuClick() {
        if (getRepotrId() == 0) {
            AppContext.showToast("正在加载，请稍等...");
        }
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.showLoginActivity(getActivity());
            return;
        }
        int reportId = getRepotrId();

        final ReportDialog dialog = new ReportDialog(getActivity(),
                getRepotrUrl(), reportId);
        dialog.setCancelable(true);
        dialog.setTitle(R.string.report);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setNegativeButton(R.string.cancle, null);
        dialog.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface d, int which) {
                        Report report = null;
                        if ((report = dialog.getReport()) != null) {
                            showWaitDialog(R.string.progress_submit);
                            OSChinaApi.report(report, mReportHandler);
                        }
                        d.dismiss();
                    }
                });
        dialog.show();
    }

    protected String getRepotrUrl() {
        return "";
    }

    protected int getRepotrId() {
        return 0;
    }

    /**
     * 收藏
     */
    public void handleFavoriteOrNot() {
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.tip_no_internet);
            return;
        }
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.showLoginActivity(getActivity());
            return;
        }
        if (getFavoriteTargetId() == -1 || getFavoriteTargetType() == -1) {
            return;
        }
        int uid = AppContext.getInstance().getLoginUid();
        if (mIsFavorited) {
            OSChinaApi.delFavorite(uid, getFavoriteTargetId(),
                    getFavoriteTargetType(), mDelFavoriteHandler);
        } else {
            OSChinaApi.addFavorite(uid, getFavoriteTargetId(),
                    getFavoriteTargetType(), mAddFavoriteHandler);
        }
    }

    /**
     * 分享
     */
    public void handleShare() {
        if (TextUtils.isEmpty(getShareContent())
                || TextUtils.isEmpty(getShareUrl())) {
            AppContext.showToast("内容加载失败...");
            return;
        }
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
                    TDevice.copyTextToBoard(getShareUrl());
                    break;
                case R.id.ly_share_more_option:
                    TDevice.showSystemShareOption(getActivity(),
                            getShareTitle(), getShareUrl());
                    break;
                default:
                    break;
                }
                dialog.dismiss();
            }
        });
        dialog.show();
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
        circleMedia.setShareContent(getShareContent());
        // 设置朋友圈title
        circleMedia.setTitle(getShareTitle());
        circleMedia.setShareImage(getShareImg());
        circleMedia.setTargetUrl(getShareUrl());
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
        weixinContent.setTargetUrl(getShareUrl());
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

    private void shareContent(SHARE_MEDIA media) {
        mController.setShareContent(getShareContent() + getShareUrl());
        mController.directShare(getActivity(), media, null);
    }

    protected void shareToQQ(SHARE_MEDIA media) {
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(getActivity(),
                Constants.QQ_APPID, Constants.QQ_APPKEY);
        qqSsoHandler.setTargetUrl(getShareUrl());
        qqSsoHandler.setTitle(getShareTitle());
        qqSsoHandler.addToSocialSDK();
        mController.setShareContent(getShareContent());
        mController.setShareImage(getShareImg());
        mController.postShare(getActivity(), media, null);
    }

    protected void notifyFavorite(boolean favorite) {
        mIsFavorited = favorite;
        FragmentActivity aty = getActivity();
        if (aty != null) {
            aty.supportInvalidateOptionsMenu();
        }
        if (mMenuAdapter != null) {
            mMenuAdapter.setFavorite(favorite);
        }
        onFavoriteChanged(favorite);
    }

    public boolean isFavorited() {
        return mIsFavorited;
    }

    @SuppressLint("ViewHolder")
    private static class MenuAdapter extends BaseAdapter {

        public void setFavorite(boolean favorite) {
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.list_cell_popup_menu, null);
            TextView name = (TextView) convertView.findViewById(R.id.tv_name);

            int iconResId = 0;
            // if (position == 0) {
            // name.setText(isFavorite ? R.string.detail_menu_unfavorite
            // : R.string.detail_menu_favorite);
            // iconResId = isFavorite ?
            // R.drawable.actionbar_menu_icn_unfavoirite
            // : R.drawable.actionbar_menu_icn_favoirite;
            // } else
            if (position == 0) {
                name.setText(parent.getResources().getString(
                        R.string.detail_menu_for_share));
                iconResId = R.drawable.abc_ic_menu_moreoverflow_normal_holo_dark;
            } else if (position == 1) {
                name.setText(parent.getResources().getString(
                        R.string.detail_menu_for_report));
                iconResId = R.drawable.abc_ic_menu_moreoverflow_normal_holo_dark;
            }
            Drawable drawable = AppContext.resources().getDrawable(iconResId);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                    drawable.getMinimumHeight());
            name.setCompoundDrawables(drawable, null, null, null);
            return convertView;
        }
    }

    private final AsyncHttpResponseHandler mAddFavoriteHandler = new AsyncHttpResponseHandler() {

        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
            try {
                Result res = XmlUtils.toBean(ResultBean.class,
                        new ByteArrayInputStream(arg2)).getResult();
                if (res.OK()) {
                    AppContext.showToastShort(R.string.add_favorite_success);
                    mMenuAdapter.setFavorite(true);
                    mMenuAdapter.notifyDataSetChanged();
                    mIsFavorited = true;
                    getActivity().supportInvalidateOptionsMenu();
                    onFavoriteChanged(true);
                    ImageView view = (ImageView) getActivity().findViewById(
                            R.id.action_favor);
                    view.setImageResource(R.drawable.ic_action_favor_on_normal);
                } else {
                    AppContext.showToastShort(res.getErrorMessage());
                }

            } catch (Exception e) {
                e.printStackTrace();
                onFailure(arg0, arg1, arg2, e);
            }
        }

        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                Throwable arg3) {
            AppContext.showToastShort(R.string.add_favorite_faile);
        }
    };

    private final AsyncHttpResponseHandler mDelFavoriteHandler = new AsyncHttpResponseHandler() {

        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
            try {
                Result res = XmlUtils.toBean(ResultBean.class,
                        new ByteArrayInputStream(arg2)).getResult();
                if (res.OK()) {
                    AppContext.showToastShort(R.string.del_favorite_success);
                    mMenuAdapter.setFavorite(false);
                    mMenuAdapter.notifyDataSetChanged();
                    mIsFavorited = false;
                    getActivity().supportInvalidateOptionsMenu();
                    onFavoriteChanged(false);
                    ImageView view = (ImageView) getActivity().findViewById(
                            R.id.action_favor);
                    view.setImageResource(R.drawable.ic_action_favor);
                } else {
                    AppContext.showToastShort(res.getErrorMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                onFailure(arg0, arg1, arg2, e);
            }
        }

        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                Throwable arg3) {
            AppContext.showToastShort(R.string.del_favorite_faile);
        }
    };

    public abstract int getCommentCount();

    @Override
    public void onClick(View v) {}

    @Override
    public void initView(View view) {}

    public void onclickWriteComment() {}

    @Override
    public void initData() {}
}