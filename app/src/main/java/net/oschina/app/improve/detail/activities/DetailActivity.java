package net.oschina.app.improve.detail.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.socialize.sso.UMSsoHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.Report;
import net.oschina.app.improve.base.activities.BaseBackActivity;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.detail.contract.DetailContract;
import net.oschina.app.improve.detail.fragments.DetailFragment;
import net.oschina.app.ui.ReportDialog;
import net.oschina.app.ui.ShareDialog;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.DialogHelp;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

/**
 * Created by JuQiu
 * on 16/6/20.
 */

public abstract class DetailActivity<Data, DataView extends DetailContract.View> extends BaseBackActivity implements DetailContract.Operator<Data, DataView> {

    long mDataId;
    Data mData;
    DataView mView;
    EmptyLayout mEmptyLayout;
    TextView mCommentCountView;

    private ProgressDialog mDialog;
    private ShareDialog mShareDialog;

    public long getDataId() {
        return mDataId;
    }

    public Data getData() {
        return mData;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_blog_detail;
    }

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
                EmptyLayout emptyLayout = mEmptyLayout;
                if (emptyLayout != null && emptyLayout.getErrorState() != EmptyLayout.HIDE_LAYOUT) {
                    emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                    requestData();
                }
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        requestData();
    }

    @Override
    public void hideLoading() {
        final EmptyLayout emptyLayout = mEmptyLayout;
        if (emptyLayout == null)
            return;
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_alpha_to_hide);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        emptyLayout.startAnimation(animation);
    }

    /**
     * 请求数据
     */
    abstract void requestData();

    /**
     * 获取显示界面的Fragment
     *
     * @return 继承自DetailFragment
     */
    abstract Class<? extends DetailFragment> getDataViewFragment();

    /**
     * 得到JSON解析的数据Type
     *
     * @return Type
     */
    abstract Type getDataType();

    AsyncHttpResponseHandler getRequestHandler() {
        return new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
                if (isDestroy())
                    return;
                showError(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (isDestroy())
                    return;
                if (!handleData(responseString))
                    showError(EmptyLayout.NODATA);
            }
        };
    }

    @SuppressWarnings("TryWithIdenticalCatches")
    void handleView() {
        try {
            Fragment fragment = getDataViewFragment().newInstance();
            FragmentTransaction trans = getSupportFragmentManager()
                    .beginTransaction();
            trans.replace(R.id.lay_container, fragment);
            trans.commit();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    boolean handleData(String responseString) {
        ResultBean<Data> result;
        try {
            Type type = getDataType();
            result = AppContext.createGson().fromJson(responseString, type);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        if (result.isSuccess()) {
            mData = result.getResult();
            handleView();
            return true;
        }

        return false;

    }

    void showError(int type) {
        EmptyLayout layout = mEmptyLayout;
        if (layout != null) {
            layout.setErrorType(type);
        }
    }

    @Override
    public void setCommentCount(int count) {
        final TextView view = mCommentCountView;
        if (view != null) {
            String str;
            if (count < 1000)
                str = String.valueOf(count);
            else if (count < 10000) {
                str = String.format("%sK", (Math.round(count * 0.01f) * 0.1f));
            } else {
                str = String.format("%sW", (Math.round(count * 0.001f) * 0.1f));
            }
            view.setText(str);
        }
    }

    int getOptionsMenuId() {
        return R.menu.menu_detail;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int menuId = getOptionsMenuId();
        if (menuId <= 0)
            return false;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(menuId, menu);
        MenuItem item = menu.findItem(R.id.menu_scroll_comment);
        if (item != null) {
            View action = item.getActionView();
            if (action != null) {
                action.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DataView view = mView;
                        if (view != null) {
                            view.scrollToComment();
                        }
                    }
                });
                View tv = action.findViewById(R.id.tv_comment_count);
                if (tv != null)
                    mCommentCountView = (TextView) tv;
            }
        }
        return true;
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

    protected void toReport(long id, String href, byte reportType) {

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
