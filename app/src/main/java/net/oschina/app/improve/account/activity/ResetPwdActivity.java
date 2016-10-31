package net.oschina.app.improve.account.activity;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.account.bean.PhoneToken;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.base.activities.BaseActivity;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.util.TDevice;
import net.oschina.common.verify.Verifier;

import java.lang.reflect.Type;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by fei
 * on 2016/10/14.
 * desc:
 */

public class ResetPwdActivity extends BaseActivity implements View.OnClickListener, View.OnFocusChangeListener {

    private static final String TAG = "ResetPwdActivity";

    @Bind(R.id.ly_reset_bar)
    LinearLayout mLlResetBar;

    @Bind(R.id.ll_reset_pwd)
    LinearLayout mLlResetPwd;
    @Bind(R.id.et_reset_pwd)
    EditText mEtResetPwd;
    @Bind(R.id.iv_reset_pwd_del)
    ImageView mIvResetPwdDel;
    @Bind(R.id.bt_reset_submit)
    Button mBtResetSubmit;
    private PhoneToken mPhoneToken;
    private TextHttpResponseHandler mHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {

            Type type = new TypeToken<ResultBean>() {
            }.getType();
            ResultBean resultBean = AppOperator.createGson().fromJson(responseString, type);
            if (resultBean.isSuccess()) {
                LoginActivity.show(ResetPwdActivity.this);
                finish();
            } else {
                AppContext.showToast(resultBean.getMessage());
            }

        }
    };


    /**
     * show the resetPwdActivity
     *
     * @param context context
     */
    public static void show(Context context, PhoneToken phoneToken) {
        Intent intent = new Intent(context, ResetPwdActivity.class);
        intent.putExtra(RegisterStepTwoActivity.PHONETOKEN_KEY, phoneToken);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main_reset_pwd;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        TextView tvLabel = (TextView) mLlResetBar.findViewById(R.id.tv_navigation_label);
        tvLabel.setText(R.string.reset_pwd_label);
        mEtResetPwd.setOnFocusChangeListener(this);
        mEtResetPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int length = s.length();
                if (length > 6) {
                    mIvResetPwdDel.setVisibility(View.VISIBLE);
                    mLlResetPwd.setBackgroundResource(R.drawable.bg_login_input_ok);
                    mBtResetSubmit.setAlpha(1.0f);
                } else {
                    mLlResetPwd.setBackgroundResource(R.drawable.bg_login_input_error);
                    mBtResetSubmit.setAlpha(0.6f);
                }

            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        Intent intent = getIntent();
        mPhoneToken = (PhoneToken) intent.getSerializableExtra(RegisterStepTwoActivity.PHONETOKEN_KEY);
        Log.e(TAG, "initData: ------------>" + mPhoneToken.toString());
    }

    @OnClick({R.id.ib_navigation_back, R.id.iv_reset_pwd_del, R.id.bt_reset_submit})
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_reset_pwd_del:
                mEtResetPwd.setText(null);
                break;
            case R.id.bt_reset_submit:

                String tempPwd = mEtResetPwd.getText().toString().trim();
                if (TextUtils.isEmpty(tempPwd)) {
                    AppContext.showToast(getString(R.string.reset_pwd_hint), Toast.LENGTH_SHORT);
                    return;
                }
                if (tempPwd.length() > 6) {
                    AppContext.showToast(getString(R.string.reset_pwd_hint), Toast.LENGTH_SHORT);
                    return;
                }
                if (!TDevice.hasInternet()) {
                    AppContext.showToast(getString(R.string.tip_network_error), Toast.LENGTH_SHORT);
                    return;
                }


                String appToken = Verifier.getPrivateToken(getApplication());
                OSChinaApi.resetPwd(tempPwd, mPhoneToken.getToken(), appToken, mHandler);

                break;
            default:
                break;
        }

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            mLlResetPwd.setActivated(true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
