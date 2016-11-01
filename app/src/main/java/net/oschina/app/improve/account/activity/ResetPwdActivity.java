package net.oschina.app.improve.account.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import net.oschina.app.improve.account.base.AccountBaseActivity;
import net.oschina.app.improve.account.bean.PhoneToken;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.util.TDevice;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by fei
 * on 2016/10/14.
 * desc:
 */

public class ResetPwdActivity extends AccountBaseActivity implements View.OnClickListener, View.OnFocusChangeListener {

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
        public void onStart() {
            super.onStart();
            showWaitDialog();
        }

        @Override
        public void onFinish() {
            super.onFinish();
            hideWaitDialog();
        }

        @Override
        public void onCancel() {
            super.onCancel();
            hideWaitDialog();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            requestFailureHint(throwable);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {

            Type type = new TypeToken<ResultBean>() {
            }.getType();
            ResultBean resultBean = AppOperator.createGson().fromJson(responseString, type);
            int code = resultBean.getCode();

            switch (code) {
                case 1:
                    LoginActivity.show(ResetPwdActivity.this);
                    finish();
                    break;
                case 216:
                    finish();
                    break;
                case 219:
                    mLlResetPwd.setBackgroundResource(R.drawable.bg_login_input_error);
                    break;
                default:
                    break;
            }
            AppContext.showToast(resultBean.getMessage());
        }
    };


    /**
     * show the resetPwdActivity
     *
     * @param context context
     */
    public static void show(Context context, PhoneToken phoneToken) {
        Intent intent = new Intent(context, ResetPwdActivity.class);
        intent.putExtra(RegisterStepTwoActivity.PHONE_TOKEN_KEY, phoneToken);
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

            @SuppressWarnings("deprecation")
            @Override
            public void afterTextChanged(Editable s) {
                int length = s.length();
                if (length >= 6) {
                    mIvResetPwdDel.setVisibility(View.VISIBLE);
                    mLlResetPwd.setBackgroundResource(R.drawable.bg_login_input_ok);
                    mBtResetSubmit.setBackgroundResource(R.drawable.bg_login_submit);
                    mBtResetSubmit.setTextColor(getResources().getColor(R.color.white));
                } else {
                    if (length <= 0) {
                        mIvResetPwdDel.setVisibility(View.GONE);
                        mLlResetPwd.setBackgroundResource(R.drawable.bg_login_input_ok);
                    } else {
                        mIvResetPwdDel.setVisibility(View.VISIBLE);
                        mLlResetPwd.setBackgroundResource(R.drawable.bg_login_input_error);
                    }
                    mBtResetSubmit.setBackgroundResource(R.drawable.bg_login_submit_lock);
                    mBtResetSubmit.setTextColor(getResources().getColor(R.color.account_lock_font_color));
                }

            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        Intent intent = getIntent();
        mPhoneToken = (PhoneToken) intent.getSerializableExtra(RegisterStepTwoActivity.PHONE_TOKEN_KEY);
    }

    @OnClick({R.id.ib_navigation_back, R.id.iv_reset_pwd_del, R.id.bt_reset_submit})
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.ib_navigation_back:
                finish();
                break;
            case R.id.iv_reset_pwd_del:
                mEtResetPwd.setText(null);
                break;
            case R.id.bt_reset_submit:

                requestResetPwd();

                break;
            default:
                break;
        }

    }

    private void requestResetPwd() {
        String tempPwd = mEtResetPwd.getText().toString().trim();
        if (TextUtils.isEmpty(tempPwd) || tempPwd.length() < 6) {
            AppContext.showToast(getString(R.string.reset_pwd_hint), Toast.LENGTH_SHORT);
            return;
        }
        if (!TDevice.hasInternet()) {
            AppContext.showToast(getString(R.string.tip_network_error), Toast.LENGTH_SHORT);
            return;
        }
        String appToken = "765e06cc569b5b8ed41a4a8c979338c888d644f4";// Verifier.getPrivateToken(getApplication());

        OSChinaApi.resetPwd(Sha1toHex(tempPwd), mPhoneToken.getToken(), appToken, mHandler);
    }

    @NonNull
    private String Sha1toHex(String tempPwd) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.update(tempPwd.getBytes("utf-8"));
            byte[] bytes = messageDigest.digest();

            StringBuilder tempHex = new StringBuilder();
            // 字节数组转换为 十六进制数
            for (byte aByte : bytes) {
                String shaHex = Integer.toHexString(aByte & 0xff);
                if (shaHex.length() < 2) {
                    tempHex.append(0);
                }
                tempHex.append(shaHex);
            }
            return tempHex.toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return tempPwd;
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
