package net.oschina.app.improve.share.widget;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import net.oschina.app.R;
import net.oschina.app.improve.share.adapter.ShareActionAdapter;
import net.oschina.app.improve.share.bean.Share;
import net.oschina.app.improve.share.bean.ShareItem;
import net.oschina.app.improve.share.manager.ShareManager;
import net.oschina.app.util.TDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fei on 2016/10/10.
 * desc:
 */

public class ShareDialogBuilder extends AlertDialog.Builder implements DialogInterface
                                                                               .OnCancelListener,
                                                                       DialogInterface
                                                                               .OnDismissListener,
                                                                       ShareActionAdapter
                                                                               .OnItemClickListener {

    private static final String TAG = "ShareDialogBuilder";

    private Share mShare;
    private Activity mActivity;
    private AlertDialog mAlertDialog;


    public ShareDialogBuilder(@NonNull Context context) {
        super(context);
        // initListener();
    }

    public ShareDialogBuilder(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        // initListener();
    }


//    private void initListener() {
//        setOnCancelListener(this);
//        setOnDismissListener(this);
//    }

    private List<ShareItem> initAdapterData() {
        List<ShareItem> shareActions = new ArrayList<>(6);

        //1.朋友圈
        ShareItem shareAction0 = new ShareItem();
        shareAction0.setIconId(R.drawable.share_icon_wechatfriends_selector);
        shareAction0.setNameId(R.string.platform_weichat_circle);

        shareActions.add(shareAction0);

        //2.微信
        ShareItem shareAction1 = new ShareItem();
        shareAction1.setIconId(R.drawable.share_icon_wechat_selector);
        shareAction1.setNameId(R.string.platform_wechat);

        shareActions.add(shareAction1);

        //3.新浪微博
        ShareItem shareAction2 = new ShareItem();
        shareAction2.setIconId(R.drawable.share_icon_sinaweibo_selector);
        shareAction2.setNameId(R.string.platform_sina);

        shareActions.add(shareAction2);

        //4.QQ
        ShareItem shareAction3 = new ShareItem();
        shareAction3.setIconId(R.drawable.share_icon_qq_selector);
        shareAction3.setNameId(R.string.platform_qq);

        shareActions.add(shareAction3);

        //5.复制链接
        ShareItem shareAction4 = new ShareItem();
        shareAction4.setIconId(R.drawable.share_icon_copy_link_selector);
        shareAction4.setNameId(R.string.platform_copy_link);

        shareActions.add(shareAction4);

        //6.更多
        ShareItem shareAction5 = new ShareItem();
        shareAction5.setIconId(R.drawable.share_icon_more_selector);
        shareAction5.setNameId(R.string.platform_more_option);

        shareActions.add(shareAction5);

        return shareActions;
    }

    private ShareActionAdapter initAdapter() {
        ShareActionAdapter shareActionAdapter = new ShareActionAdapter(initAdapterData());
        shareActionAdapter.addOnItemClickListener(this);

        return shareActionAdapter;
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

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View contentView = inflater.inflate(layoutResId, null, false);
        RecyclerView shareRecycle = (RecyclerView) contentView.findViewById(R.id.share_recycler);
        shareRecycle.setAdapter(initAdapter());
        shareRecycle.setItemAnimator(new DefaultItemAnimator());
        shareRecycle.setLayoutManager(new GridLayoutManager(getContext(), 3));
        builder.setView(contentView);

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

    }

    @Override
    public void onDismiss(DialogInterface dialog) {

    }


    @Override
    public void onItemClick(int position, long itemId) {

        Share share = getShare();
        ShareManager shareManager = ShareManager.initShareManager();

        switch (position) {
            //朋友圈
            case 0:
                shareManager.registerWeChatShare(getContext());
                share.setShareScene(Share.SHARE_TIMELINE);
                shareManager.shareWechatWeb(getContext(), share);
                break;
            //微信会话
            case 1:
                shareManager.registerWeChatShare(getContext());
                share.setShareScene(Share.SHARE_SESSION);
                shareManager.shareWechatWeb(getContext(), share);
                break;
            //新浪微博
            case 2:
                shareManager.registerSinaShare(getContext().getApplicationContext(), mActivity, share);
                break;
            //QQ
            case 3:
                shareManager.registerQQShare(getContext().getApplicationContext());
                shareManager.shareQQWeb(mActivity, share);
                break;
            //复制链接
            case 4:
                TDevice.copyTextToBoard(share.getUrl());
                break;
            //更多(调用系统分享)
            case 5:
                showSystemShareOption(share.getTitle(), share.getUrl());
                break;
            default:
                showSystemShareOption(share.getTitle(), share.getUrl());
                break;
        }

        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
            mAlertDialog.cancel();
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
}
