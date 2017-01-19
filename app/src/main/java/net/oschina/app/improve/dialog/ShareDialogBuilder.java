package net.oschina.app.improve.dialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.improve.bean.simple.About;
import net.oschina.app.improve.tweet.activities.TweetPublishActivity;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.util.TDevice;
import net.oschina.open.bean.Share;
import net.oschina.open.constants.OpenConstant;
import net.oschina.open.factory.OpenBuilder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 分享弹出框辅助类
 */
public class ShareDialogBuilder extends AlertDialog.Builder implements
        DialogInterface.OnCancelListener, DialogInterface.OnDismissListener,
        OpenBuilder.Callback {
    private Share mShare;
    private About.Share mAboutShare;
    private Activity mActivity;
    private AlertDialog mAlertDialog;
    private ProgressDialog mDialog;

    private ShareDialogBuilder(@NonNull Activity context, @StyleRes int themeResId) {
        super(context, themeResId);
        mActivity = context;
        setTitle(null);
    }

    public static ShareBuilder with(Activity activity) {
        return with(activity, R.style.share_dialog);
    }

    public static ShareBuilder with(@NonNull Activity activity, @StyleRes int themeResId) {
        return new ShareBuilder(activity, themeResId);
    }

    private List<ShareItem> getAdapterData() {
        List<ShareItem> shareActions = new ArrayList<>();

        //0.新浪微博
        shareActions.add(new ShareItem(R.mipmap.ic_login_3party_weibo, R.string.platform_sina));

        //1.朋友圈
        shareActions.add(new ShareItem(R.mipmap.ic_action_moments, R.string.platform_wechat_circle));

        //2.微信
        shareActions.add(new ShareItem(R.mipmap.ic_login_3party_wechat, R.string.platform_wechat));

        //3.QQ
        shareActions.add(new ShareItem(R.mipmap.ic_login_3party_qq, R.string.platform_qq));

        //4.动弹
        if (About.check(mAboutShare)) {
            shareActions.add(new ShareItem(R.mipmap.ic_action_tweet, R.string.platform_tweet));
        }

        //5.browser
        shareActions.add(new ShareItem(R.mipmap.ic_action_browser, R.string.platform_browser));

        //6.复制链接
        shareActions.add(new ShareItem(R.mipmap.ic_action_url, R.string.platform_copy_link));

        //7.更多
        shareActions.add(new ShareItem(R.mipmap.ic_action_more, R.string.platform_more_option));

        return shareActions;
    }

    @SuppressWarnings("deprecation")
    @Override
    public AlertDialog create() {

        AlertDialog alertDialog = super.create();
        Window window = alertDialog.getWindow();

        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            WindowManager m = window.getWindowManager();
            Display d = m.getDefaultDisplay();
            WindowManager.LayoutParams p = window.getAttributes();
            p.width = d.getWidth();
            window.setAttributes(p);
        }
        this.mAlertDialog = alertDialog;
        return alertDialog;
    }

    @Override
    public AlertDialog.Builder setView(int layoutResId) {
        AlertDialog.Builder builder = super.setView(layoutResId);
        if (layoutResId == R.layout.dialog_share_main) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View contentView = inflater.inflate(layoutResId, null, false);
            RecyclerView shareRecycle = (RecyclerView) contentView.findViewById(R.id.share_recycler);
            shareRecycle.setAdapter(new ShareActionAdapter(getAdapterData()));
            shareRecycle.setItemAnimator(new DefaultItemAnimator());
            shareRecycle.setLayoutManager(new GridLayoutManager(getContext(), 4));
            builder.setView(contentView);
            builder.setOnCancelListener(this);
            builder.setOnDismissListener(this);
        }
        return builder;
    }

    public ShareDialogBuilder addShare(Share share) {
        this.mShare = share;
        return this;
    }

    public ShareDialogBuilder boundActivity(Activity activity) {
        this.mActivity = activity;
        return this;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        hideWaitDialog();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        hideWaitDialog();
    }

    public void onItemClick(int position, ShareItem item) {
        Share share = getShare();
        switch (item.iconId) {
            //新浪微博
            case R.mipmap.ic_login_3party_weibo:
                showWaitDialog(R.string.login_weibo_hint);
                OpenBuilder.with(mActivity)
                        .useWeibo(OpenConstant.WB_APP_KEY)
                        .share(share, this);
                break;
            //朋友圈
            case R.mipmap.ic_action_moments:
                showWaitDialog(R.string.login_wechat_hint);
                OpenBuilder.with(mActivity)
                        .useWechat(OpenConstant.WECHAT_APP_ID)
                        .shareTimeLine(share, this);
                break;
            //微信会话
            case R.mipmap.ic_login_3party_wechat:
                showWaitDialog(R.string.login_wechat_hint);
                OpenBuilder.with(mActivity)
                        .useWechat(OpenConstant.WECHAT_APP_ID)
                        .shareSession(share, this);
                break;
            //QQ
            case R.mipmap.ic_login_3party_qq:
                showWaitDialog(R.string.login_tencent_hint);
                OpenBuilder.with(mActivity)
                        .useTencent(OpenConstant.QQ_APP_ID)
                        .share(share, new IUiListener() {
                            @Override
                            public void onComplete(Object o) {
                                hideWaitDialog();
                            }

                            @Override
                            public void onError(UiError uiError) {
                                hideWaitDialog();
                                AppContext.showToast(R.string.share_hint, Toast.LENGTH_SHORT);
                            }

                            @Override
                            public void onCancel() {
                                hideWaitDialog();
                            }
                        }, this);
                break;
            //转发到动弹
            case R.mipmap.ic_action_tweet:
                if (About.check(mAboutShare))
                    TweetPublishActivity.show(getContext(), null, null, mAboutShare);
                cancelLoading();
                break;
            //在浏览器中打开
            case R.mipmap.ic_action_browser:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                // intent.setAction(Intent.CATEGORY_BROWSABLE);
                Uri content_url = Uri.parse(share.getUrl());
                intent.setData(content_url);
                mActivity.startActivity(intent);
                cancelLoading();
                break;
            //复制链接
            case R.mipmap.ic_action_url:
                TDevice.copyTextToBoard(share.getUrl());
                cancelLoading();
                break;
            //更多(调用系统分享)
            default:
                showSystemShareOption(share.getTitle(), share.getUrl());
                cancelLoading();
                break;
        }

    }

    /**
     * show WaitDialog
     *
     * @return progressDialog
     */
    private ProgressDialog showWaitDialog(@StringRes int messageId) {
        if (mDialog == null) {
            mDialog = DialogHelper.getProgressDialog(mActivity, true);
        }
        mDialog.setMessage(mActivity.getResources().getString(messageId));
        mDialog.show();
        return mDialog;
    }

    /**
     * hide waitDialog
     */
    private void hideWaitDialog() {
        ProgressDialog dialog = mDialog;
        if (dialog != null) {
            mDialog = null;
            try {
                dialog.cancel();
                // dialog.dismiss();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public Share getShare() {
        return mShare;
    }

    /**
     * 调用系统安装的应用分享
     *
     * @param title title
     * @param url   url
     */
    private void showSystemShareOption(final String title, final String url) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享：" + title);
        intent.putExtra(Intent.EXTRA_TEXT, title + " " + url);
        getContext().startActivity(Intent.createChooser(intent, "选择分享"));
    }

    @Override
    public void onFailed() {
        hideWaitDialog();
        AppContext.showToast(R.string.share_hint, Toast.LENGTH_SHORT);
    }

    @Override
    public void onSuccess() {
        //调起第三方客户端
        //        if (mAlertDialog != null && mAlertDialog.isShowing()) {
        //            mAlertDialog.cancel();
        //            //mAlertDialog.dismiss();
        //        }
    }

    /**
     * cancelLoading
     */
    public void cancelLoading() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.cancel();
            //mAlertDialog.dismiss();
        }
    }

    public static class ShareBuilder {
        private Activity activity;
        private int themeResId;
        private String title;
        private String summary;
        private String content;
        private String description;
        private String url;
        private int bitmapResID = R.mipmap.ic_share;
        private long id;
        private int type;
        private String imageUrl;

        public ShareBuilder(Activity activity, int themeResId) {
            this.activity = activity;
            this.themeResId = themeResId;
        }

        public ShareBuilder id(long id) {
            this.id = id;
            return this;
        }

        public ShareBuilder type(int type) {
            this.type = type;
            return this;
        }

        public ShareBuilder title(String title) {
            this.title = title;
            return this;
        }

        public ShareBuilder content(String content) {
            this.content = content;
            this.summary = content;
            this.description = content;
            return this;
        }

        public ShareBuilder url(String url) {
            this.url = url;
            return this;
        }

        public ShareBuilder bitmapResID(int bitmapResID) {
            this.bitmapResID = bitmapResID;
            return this;
        }

        public ShareDialogBuilder build() {
            Share share = new Share();
            share.setTitle(title);
            share.setSummary(summary);
            share.setContent(content);
            share.setDescription(description);
            share.setUrl(url);
            share.setBitmapResID(bitmapResID);
            share.setImageUrl(imageUrl);

            share.setAppName("开源中国");
            share.setAppShareIcon(R.mipmap.ic_share);

            ShareDialogBuilder builder = new ShareDialogBuilder(activity, themeResId);
            builder.mShare = share;

            if (id > 0 && type >= 0) {
                About.Share aboutShare = About.buildShare(id, type);
                aboutShare.title = title;
                aboutShare.content = content;
                builder.mAboutShare = aboutShare;
            }

            builder.setView(R.layout.dialog_share_main);
            return builder;
        }

        public ShareBuilder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }
    }

    private class ShareActionAdapter extends RecyclerView.Adapter<ViewHolder>
            implements View.OnClickListener {
        private List<ShareItem> mShareActions;

        ShareActionAdapter(List<ShareItem> shareActions) {
            this.mShareActions = shareActions;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            View rootView = LayoutInflater.from(context).inflate(R.layout.dialog_share_item,
                    parent, false);
            ViewHolder viewHolder = new ViewHolder(rootView);
            viewHolder.mIvIcon.setOnClickListener(this);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ShareItem shareAction = mShareActions.get(position);
            holder.itemView.setTag(shareAction);
            holder.mIvIcon.setImageResource(shareAction.iconId);
            holder.mTvName.setText(shareAction.nameId);
            holder.mIvIcon.setTag(holder);
        }

        @Override
        public int getItemCount() {
            return mShareActions == null ? 0 : mShareActions.size();
        }

        @Override
        public void onClick(View v) {
            try {
                ViewHolder viewHolder = (ViewHolder) v.getTag();
                int position = viewHolder.getAdapterPosition();
                ShareItem item = (ShareItem) viewHolder.itemView.getTag();
                onItemClick(position, item);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.share_icon)
        ImageView mIvIcon;
        @Bind(R.id.share_name)
        TextView mTvName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private static class ShareItem {
        int iconId;
        int nameId;

        ShareItem(int iconId, int nameId) {
            this.iconId = iconId;
            this.nameId = nameId;
        }
    }
}
