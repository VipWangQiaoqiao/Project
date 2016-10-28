package net.oschina.app.improve.account.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
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
import net.oschina.app.improve.account.constants.UserConstants;
import net.oschina.app.improve.base.activities.BaseActivity;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.utils.AssimilateUtils;
import net.oschina.app.util.TDevice;

import java.lang.reflect.Type;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by fei
 * on 2016/10/14.
 * desc:
 */

public class RetrieveActivity extends BaseActivity implements View.OnClickListener, View.OnFocusChangeListener {

    private static final String TAG = "RetrieveActivity";

    @Bind(R.id.ly_retrieve_bar)
    LinearLayout mLlRetrieveBar;

    @Bind(R.id.ll_retrieve_tel)
    LinearLayout mLlRetrieveTel;
    @Bind(R.id.et_retrieve_tel)
    EditText mEtRetrieveTel;
    @Bind(R.id.iv_retrieve_tel_del)
    ImageView mIvRetrieveTelDel;

    @Bind(R.id.ll_retrieve_code)
    LinearLayout mLlRetrieveCode;
    @Bind(R.id.et_retrieve_code_input)
    EditText mEtRetrieveCodeInput;
    @Bind(R.id.retrieve_sms_call)
    TextView mTvRetrieveSmsCall;

    @Bind(R.id.bt_retrieve_submit)
    Button mBtRetrieveSubmit;
    @Bind(R.id.tv_retrieve_label)
    TextView mTvRetrieveLabel;
    private boolean mMachPhoneNum;

    private int mRequestType;
    private String mPhoneNumber;
    private String mSmsCode;
    private String mAppToken;
    private TextHttpResponseHandler mHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            mRequestType = 2;
            if (mRequestType == 2) {

                PhoneToken phoneToken = new PhoneToken();
                phoneToken.setPhone("15111225406");
                phoneToken.setToken("abc");
                phoneToken.setExpireDate("30");
                ResetPwdActivity.show(RetrieveActivity.this, phoneToken);

            }

        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {

            try {
                switch (mRequestType) {
                    //第一步请求发送验证码
                    case 1:

                        Log.e(TAG, "onSuccess: ------>收到手机验证码");

                        Type type = new TypeToken<ResultBean>() {
                        }.getType();
                        ResultBean resultBean = AppContext.createGson().fromJson(responseString, type);
                        if (resultBean.isSuccess()) {
                            mRequestType = 2;
                            //发送验证码成功
                        } else {
                            AppContext.showToast(resultBean.getMessage());
                        }

                        break;
                    //第二步请求进行注册
                    case 2:

                        Type phoneType = new TypeToken<ResultBean<PhoneToken>>() {
                        }.getType();

                        ResultBean<PhoneToken> phoneTokenResultBean = AppContext.createGson().fromJson(responseString, phoneType);

                        if (phoneTokenResultBean.isSuccess()) {

                            PhoneToken phoneToken = phoneTokenResultBean.getResult();
                            if (phoneToken != null) {
                                mTvRetrieveSmsCall.setAlpha(1.0f);
                                ResetPwdActivity.show(RetrieveActivity.this, phoneToken);
                            }
                        } else {
                            AppContext.showToast(phoneTokenResultBean.getMessage());
                        }

                        break;
                    default:
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
                onFailure(statusCode, headers, responseString, e);
            }

        }
    };


    /**
     * show the retrieve activity
     *
     * @param context context
     */
    public static void show(Context context) {
        Intent intent = new Intent(context, RetrieveActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main_retrieve_pwd;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        TextView tvLabel = (TextView) mLlRetrieveBar.findViewById(R.id.tv_navigation_label);
        tvLabel.setText(R.string.retrieve_pwd_label);
        mEtRetrieveTel.setOnFocusChangeListener(this);
        mEtRetrieveTel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = s.length();
                if (length > 0) {
                    mIvRetrieveTelDel.setVisibility(View.VISIBLE);
                } else {
                    mIvRetrieveTelDel.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

                int length = s.length();

                if (length > 0 && length < 11) {
                    mLlRetrieveTel.setBackgroundResource(R.drawable.bg_login_input_error);
                    mTvRetrieveSmsCall.setAlpha(0.4f);
                } else if (length == 11) {
                    String input = s.toString();
                    mMachPhoneNum = AssimilateUtils.MachPhoneNum(input);
                    if (mMachPhoneNum) {
                        mLlRetrieveTel.setBackgroundResource(R.drawable.bg_login_input_ok);
                        mTvRetrieveSmsCall.setAlpha(1.0f);
                    } else {
                        mLlRetrieveTel.setBackgroundResource(R.drawable.bg_login_input_error);
                        AppContext.showToast(getResources().getString(R.string.hint_username_ok), Toast.LENGTH_SHORT);
                        mTvRetrieveSmsCall.setAlpha(0.4f);
                    }
                } else if (length > 11) {
                    mTvRetrieveSmsCall.setAlpha(0.4f);
                    mLlRetrieveTel.setBackgroundResource(R.drawable.bg_login_input_error);
                } else if (length <= 0) {
                    mTvRetrieveSmsCall.setAlpha(0.4f);
                    mLlRetrieveTel.setBackgroundResource(R.drawable.bg_login_input_ok);
                }

            }
        });
        mBtRetrieveSubmit.setAlpha(0.6f);
        mEtRetrieveCodeInput.setOnFocusChangeListener(this);
        mEtRetrieveCodeInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int length = s.length();
                if (length > 0) {
                    mBtRetrieveSubmit.setAlpha(1.0f);
                } else {
                    mBtRetrieveSubmit.setAlpha(0.6f);
                }

            }
        });
    }

    @Override
    protected void initData() {
        super.initData();

        String phone = mEtRetrieveTel.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            mTvRetrieveSmsCall.setAlpha(0.4f);
        }
        String smsCode = mEtRetrieveCodeInput.getText().toString().trim();
        if (TextUtils.isEmpty(smsCode)) {
            mBtRetrieveSubmit.setAlpha(0.8f);
        }
    }

    @OnClick({R.id.ib_navigation_back, R.id.iv_retrieve_tel_del, R.id.retrieve_sms_call,
            R.id.bt_retrieve_submit, R.id.tv_retrieve_label})
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.ib_navigation_back:
                finish();
                break;
            case R.id.iv_retrieve_tel_del:
                mEtRetrieveTel.setText(null);
                break;
            case R.id.retrieve_sms_call:

                if (!mMachPhoneNum) {
                    AppContext.showToast(getString(R.string.retrieve_pwd_sms_coe_error), Toast.LENGTH_SHORT);
                    return;
                }

                if (!TDevice.hasInternet()) {
                    AppContext.showToast(getResources().getString(R.string.tip_network_error), Toast.LENGTH_SHORT);
                    return;
                }

                if (mTvRetrieveSmsCall.getTag() == null) {
                    mRequestType = 1;
                    mTvRetrieveSmsCall.setAlpha(0.6f);
                    mTvRetrieveSmsCall.setTag(true);
                    new CountDownTimer(60 * 1000, 1000) {

                        @SuppressLint("DefaultLocale")
                        @Override
                        public void onTick(long millisUntilFinished) {
                            mTvRetrieveSmsCall.setText(String.format("%s%s%d%s",
                                    getResources().getString(R.string.register_sms_hint), "(", millisUntilFinished / 1000, ")"));
                        }

                        @Override
                        public void onFinish() {
                            mTvRetrieveSmsCall.setTag(null);
                            mTvRetrieveSmsCall.setText(getResources().getString(R.string.register_sms_hint));
                            mTvRetrieveSmsCall.setAlpha(1.0f);
                        }
                    }.start();
                    mPhoneNumber = mEtRetrieveTel.getText().toString().trim();
                    mAppToken = "123";//Verifier.getPrivateToken(getApplication());
                    OSChinaApi.sendSmsCode(mPhoneNumber, mAppToken, OSChinaApi.REGISTER_INTENT, mHandler);
                } else {
                    AppContext.showToast(getResources().getString(R.string.register_sms_wait_hint), Toast.LENGTH_SHORT);
                }

                break;
            case R.id.bt_retrieve_submit:

                mSmsCode = mEtRetrieveCodeInput.getText().toString().trim();
                if (TextUtils.isEmpty(mSmsCode)) {
                    AppContext.showToast(getString(R.string.retrieve_pwd_sms_coe_error));
                    return;
                }

                mPhoneNumber = mEtRetrieveTel.getText().toString().trim();

                if (TextUtils.isEmpty(mPhoneNumber)) {
                    AppContext.showToast(getString(R.string.hint_username_ok));
                    return;
                }
                if (!TDevice.hasInternet()) {
                    AppContext.showToast(getString(R.string.tip_network_error));
                    return;
                }
                mRequestType = 2;
                mAppToken = "123";//Verifier.getPrivateToken(getApplication());
                OSChinaApi.validateRegisterInfo(mPhoneNumber, mSmsCode, mAppToken, mHandler);

                break;
            case R.id.tv_retrieve_label:

                //打开web进入邮箱找回密码

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                // intent.setAction(Intent.CATEGORY_BROWSABLE);
                Uri content_url = Uri.parse(UserConstants.RETRIEVE_PWD_URL);
                intent.setData(content_url);
                startActivity(intent);
                break;
            default:
                break;
        }

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int id = v.getId();
        switch (id) {
            case R.id.et_retrieve_tel:
                if (hasFocus) {
                    mLlRetrieveTel.setActivated(true);
                    mLlRetrieveCode.setActivated(false);
                }
                break;
            case R.id.et_retrieve_code_input:
                if (hasFocus) {
                    mLlRetrieveCode.setActivated(true);
                    mLlRetrieveTel.setActivated(false);
                }
                break;
            default:
                break;
        }
    }
}
