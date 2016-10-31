package net.oschina.app.improve.account.base;

import android.app.ProgressDialog;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseActivity;
import net.oschina.app.util.DialogHelp;

/**
 * Created by fei
 * on 2016/10/31.
 * desc:
 */

public class AccountBaseActivity extends BaseActivity {

    private ProgressDialog mDialog;

    @Override
    protected int getContentView() {
        return 0;
    }


    /**
     * show WaitDialog
     *
     * @return progressDialog
     */
    protected ProgressDialog showWaitDialog() {
        String message = getResources().getString(R.string.progress_submit);
        if (mDialog == null) {
            mDialog = DialogHelp.getWaitDialog(this, message);
        }
        mDialog.setMessage(message);
        mDialog.show();

        return mDialog;
    }

    /**
     * show FocusWaitDialog
     *
     * @return progressDialog
     */
    protected ProgressDialog showFocusWaitDialog() {

        String message = getResources().getString(R.string.progress_submit);
        if (mDialog == null) {
            mDialog = DialogHelp.getWaitDialog(this, message);
        }
        mDialog.setMessage(message);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        return mDialog;
    }

    /**
     * hide waitDialog
     */
    protected void hideWaitDialog() {
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

    /**
     * request network error
     *
     * @param throwable throwable
     */
    protected void requestFailureHint(Throwable throwable) {
        if (throwable != null) {
            throwable.printStackTrace();
        }
        AppContext.showToast(getResources().getString(R.string.request_error_hint));
    }

}
