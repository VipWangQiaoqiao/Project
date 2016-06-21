package net.oschina.app.improve.detail.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.socialize.sso.UMSsoHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.Report;
import net.oschina.app.improve.activities.BaseBackActivity;
import net.oschina.app.improve.detail.contract.DetailContract;
import net.oschina.app.ui.ReportDialog;
import net.oschina.app.ui.ShareDialog;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.DialogHelp;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;

import cz.msebera.android.httpclient.Header;

/**
 * Created by JuQiu
 * on 16/6/20.
 */

public abstract class DetailActivity<Data, DataView extends DetailContract.View> extends BaseBackActivity implements DetailContract.Operator<Data, DataView> {
    protected long mDataId;
    protected Data mData;
    protected DataView mView;
    protected EmptyLayout mEmptyLayout;
    private ProgressDialog mDialog;
    private ShareDialog mShareDialog;

    @Override
    public void setDataView(DataView view) {
        mView = view;
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        super.initBundle(bundle);
        mDataId = bundle.getLong("id", 0);
        return mDataId != 0;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mEmptyLayout = (EmptyLayout) findViewById(R.id.lay_error);
        mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                requestData();
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        requestData();
    }

    protected abstract void requestData();

    protected abstract void showView();

    public long getDataId() {
        return mDataId;
    }

    public Data getData() {
        return mData;
    }

    public void handleData(Data data) {
        showError(View.INVISIBLE);
        mData = data;
        showView();
    }

    void showError(int type) {
        EmptyLayout layout = mEmptyLayout;
        if (layout != null) {
            layout.setErrorType(type);
            layout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_report) {
            toReport();
        } else if (id == R.id.menu_scroll_comment) {
            DataView view = mView;
            if (view != null) {
                view.scrollToComment();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public ProgressDialog showWaitDialog(int messageId) {
        String message = getResources().getString(messageId);
        if (mDialog == null) {
            mDialog = DialogHelp.getWaitDialog(this, message);
        }

        mDialog.setMessage(message);
        mDialog.show();

        return mDialog;
    }

    public void hideWaitDialog() {
        ProgressDialog dialog = mDialog;
        if (dialog != null) {
            mDialog = null;
            try {
                dialog.dismiss();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    protected void toShare(String title, String content, String url) {
        ShareDialog dialog = mShareDialog;
        if (dialog == null) {
            dialog = new ShareDialog(this);
            mShareDialog = dialog;
        }
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setTitle(R.string.share_to);
        dialog.setShareInfo(title, content, url);
        dialog.show();
    }

    public void toReport(long id, String href, byte reportType) {
        final ReportDialog dialog = new ReportDialog(this, href, id, reportType);
        dialog.setCancelable(true);
        dialog.setTitle(R.string.report);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setNegativeButton(R.string.cancle, null);
        final TextHttpResponseHandler handler = new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int arg0, Header[] arg1, String arg2) {
                if (TextUtils.isEmpty(arg2)) {
                    AppContext.showToastShort(R.string.tip_report_success);
                } else {
                    AppContext.showToastShort(arg2);
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

            @Override
            public void onStart() {
                showWaitDialog(R.string.progress_submit);
            }
        };
        dialog.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface d, int which) {
                        Report report = null;
                        if ((report = dialog.getReport()) != null) {
                            OSChinaApi.report(report, handler);
                        }
                        d.dismiss();
                    }
                });
        dialog.show();
    }


    protected void hideShareDialog() {
        ShareDialog dialog = mShareDialog;
        if (dialog != null) {
            mShareDialog = null;
            try {
                dialog.dismiss();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final ShareDialog shareDialog = mShareDialog;
        if (shareDialog != null) {
            UMSsoHandler ssoHandler = shareDialog.getController().getConfig().getSsoHandler(requestCode);
            if (ssoHandler != null) {
                ssoHandler.authorizeCallBack(requestCode, resultCode, data);
            }
        }
    }


    /**
     * 检查当前数据,并检查网络状况
     *
     * @return 返回当前登录用户, 未登录或者未通过检查返回0
     */
    public int requestCheck() {
        if (mDataId == 0 || mData == null) {
            AppContext.showToast("数据加载中...");
            return 0;
        }
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.tip_no_internet);
            return 0;
        }
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.showLoginActivity(this);
            return 0;
        }
        // 返回当前登录用户ID
        return AppContext.getInstance().getLoginUid();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideWaitDialog();
        hideShareDialog();
        mEmptyLayout = null;
        mData = null;
    }
}
