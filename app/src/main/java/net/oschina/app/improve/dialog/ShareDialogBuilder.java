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
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.util.TDevice;
import net.oschina.open.bean.Share;
import net.oschina.open.constants.OpenConstant;
import net.oschina.open.factory.OpenBuilder;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 分享弹出框辅助类
 */
public class ShareDialogBuilder extends AlertDialog.Builder implements
        DialogInterface.OnCancelListener, DialogInterface.OnDismissListener,
        OpenBuilder.Callback {
    private Share mShare;
    private Activity mActivity;
    private AlertDialog mAlertDialog;
    private ProgressDialog mDialog;

    private ShareDialogBuilder(@NonNull Activity context, @StyleRes int themeResId) {
        super(context, themeResId);
        mActivity = context;
        setTitle(null);
        setView(R.layout.dialog_share_main);
    }

    public static ShareBuilder with(Activity activity) {
        return with(activity, R.style.share_dialog);
    }

    public static ShareBuilder with(@NonNull Activity activity, @StyleRes int themeResId) {
        return new ShareDialogBuilder(activity, themeResId).createBuilder();
    }

    private ShareItem[] initAdapterData() {

        ShareItem[] shareActions = new ShareItem[7];

        //0.新浪微博
        ShareItem shareAction0 = new ShareItem();
        shareAction0.iconId = R.mipmap.ic_login_3party_weibo;
        shareAction0.nameId = R.string.platform_sina;

        shareActions[0] = shareAction0;

        //1.朋友圈
        ShareItem shareAction1 = new ShareItem();
        shareAction1.iconId = R.mipmap.ic_action_moments;
        shareAction1.nameId = R.string.platform_wechat_circle;

        shareActions[1] = shareAction1;

        //2.微信
        ShareItem shareAction2 = new ShareItem();
        shareAction2.iconId = R.mipmap.ic_login_3party_wechat;
        shareAction2.nameId = R.string.platform_wechat;

        shareActions[2] = shareAction2;

        //3.QQ
        ShareItem shareAction3 = new ShareItem();
        shareAction3.iconId = R.mipmap.ic_login_3party_qq;
        shareAction3.nameId = R.string.platform_qq;

        shareActions[3] = shareAction3;

//        //4.动弹
//        ShareItem shareAction4 = new ShareItem();
//        shareAction4.iconId = R.mipmap.ic_action_tweet;
//        shareAction4.nameId = R.string.platform_tweet;
//
//        shareActions[4] = shareAction4;

        //5.browser
        ShareItem shareAction5 = new ShareItem();
        shareAction5.iconId = R.mipmap.ic_action_browser;
        shareAction5.nameId = R.string.platform_browser;

        shareActions[4] = shareAction5;

        //6.复制链接
        ShareItem shareAction6 = new ShareItem();
        shareAction6.iconId = R.mipmap.ic_action_url;
        shareAction6.nameId = R.string.platform_copy_link;

        shareActions[5] = shareAction6;

        //7.更多
        ShareItem shareAction7 = new ShareItem();
        shareAction7.iconId = R.mipmap.ic_action_more;
        shareAction7.nameId = R.string.platform_more_option;
        shareActions[6] = shareAction7;

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
            shareRecycle.setAdapter(new ShareActionAdapter(initAdapterData()));
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

    public void onItemClick(int position, long itemId) {
        Share share = getShare();
        switch (position) {
            //新浪微博
            case 0:
                showWaitDialog(R.string.login_webo_hint);
                OpenBuilder.with(mActivity)
                        .useWeibo(OpenConstant.WB_APP_KEY)
                        .share(share, this);
                break;
            //朋友圈
            case 1:
                showWaitDialog(R.string.login_wechat_hint);
                OpenBuilder.with(mActivity)
                        .useWechat(OpenConstant.WECHAT_APP_ID)
                        .shareTimeLine(share, this);
                break;
            //微信会话
            case 2:
                showWaitDialog(R.string.login_wechat_hint);
                OpenBuilder.with(mActivity)
                        .useWechat(OpenConstant.WECHAT_APP_ID)
                        .shareSession(share, this);
                break;
            //QQ
            case 3:
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
            // case 4:
            // cancelLoading();
            //  break;
            //在浏览器中打开
            case 4:

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                // intent.setAction(Intent.CATEGORY_BROWSABLE);
                Uri content_url = Uri.parse(share.getUrl());
                intent.setData(content_url);
                mActivity.startActivity(intent);
                cancelLoading();
                break;
            //复制链接
            case 5:
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
            if (messageId <= 0) {
                mDialog = DialogHelper.getProgressDialog(mActivity, true);
            } else {
                String message = mActivity.getResources().getString(messageId);
                mDialog = DialogHelper.getProgressDialog(mActivity, message, true);
            }
        }
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

    private ShareBuilder createBuilder() {
        return new ShareBuilder();
    }

    public class ShareBuilder {
        private String title;
        private String summary;
        private String content;
        private String url;
        private int bitmapResID = R.mipmap.ic_share;

        public ShareBuilder title(String title) {
            this.title = title;
            return this;
        }

        public ShareBuilder summary(String summary) {
            this.summary = summary;
            return this;
        }

        public ShareBuilder content(String content) {
            this.content = content;
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
            share.setUrl(url);
            share.setBitmapResID(bitmapResID);

            share.setAppName("开源中国");
            share.setAppShareIcon(R.mipmap.ic_share);

            ShareDialogBuilder builder = ShareDialogBuilder.this;
            builder.mShare = share;
            return builder;
        }
    }

    private class ShareActionAdapter extends RecyclerView.Adapter<ViewHolder>
            implements View.OnClickListener {
        private ShareItem[] mShareActions;

        ShareActionAdapter(ShareItem[] shareActions) {
            this.mShareActions = shareActions;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            View rootView = LayoutInflater.from(context).inflate(R.layout.dialog_share_item, parent,
                    false);
            ViewHolder viewHolder = new ViewHolder(rootView);
            rootView.setTag(viewHolder);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ShareItem shareAction = mShareActions[position];
            holder.mIvIcon.setTag(holder);
            holder.mIvIcon.setImageResource(shareAction.iconId);
            holder.mIvIcon.setOnClickListener(this);
            holder.mTvName.setText(shareAction.nameId);
        }

        @Override
        public int getItemCount() {
            return mShareActions == null ? 0 : mShareActions.length;
        }

        @Override
        public void onClick(View v) {
            ViewHolder viewHolder = (ViewHolder) v.getTag();
            int position = viewHolder.getAdapterPosition();
            long itemId = viewHolder.getItemId();
            onItemClick(position, itemId);
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
    }
}
