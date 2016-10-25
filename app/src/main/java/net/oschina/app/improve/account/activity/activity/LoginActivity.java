package net.oschina.app.improve.account.activity.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.ApiHttpClient;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.account.activity.manager.UserCacheManager;
import net.oschina.app.improve.base.activities.BaseActivity;
import net.oschina.app.improve.bean.UserV2;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.main.MainActivity;
import net.oschina.app.improve.share.constant.OpenConstant;
import net.oschina.app.improve.utils.AssimilateUtils;
import net.oschina.open.constants.OpenConstants;
import net.oschina.open.factory.OpenLogin;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;


/**
 * Created by fei on 2016/10/14.
 * desc:
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener, WeiboAuthListener, IUiListener, View.OnFocusChangeListener {

    private static final String TAG = "LoginActivity";

    private static final String HOLD_PWD_KEY = "holdPwdKey";
    private static final String HOLD_PWD_STATUS = "holdPwdStatus";

    @Bind(R.id.ll_login_username)
    LinearLayout mLlLoginUsername;
    @Bind(R.id.et_login_username)
    EditText mEtLoginUsername;

    @Bind(R.id.ll_login_pwd)
    LinearLayout mLlLoginPwd;
    @Bind(R.id.et_login_pwd)
    EditText mEtLoginPwd;

    @Bind(R.id.iv_login_hold_pwd)
    ImageView mIvHoldPwd;
    @Bind(R.id.tv_login_forget_pwd)
    TextView mTvLoginForgetPwd;

    @Bind(R.id.bt_login_submit)
    Button mTvLoginSubmit;
    @Bind(R.id.bt_login_register)
    Button mTvLoginRegister;

    @Bind(R.id.ll_login_layer)
    LinearLayout mLlLoginLayer;
    @Bind(R.id.bt_login_pull)
    Button mBtLoginPull;

    @Bind(R.id.ll_login_options)
    LinearLayout mLlLoginOptions;

    @Bind(R.id.ib_login_weibo)
    ImageButton mIbLoginWeiBo;
    @Bind(R.id.ib_login_wx)
    ImageButton mIbLoginWx;
    @Bind(R.id.ib_login_qq)
    ImageButton mImLoginQq;

    private int openType;
    private TextHttpResponseHandler mHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {

            Type type = new TypeToken<ResultBean<UserV2>>() {
            }.getType();

            GsonBuilder gsonBuilder = new GsonBuilder();
            ResultBean<UserV2> resultBean = gsonBuilder.create().fromJson(responseString, type);
            if (resultBean != null && resultBean.isSuccess()) {

                // 更新相关Cookie信息
                ApiHttpClient.updateCookie(ApiHttpClient.getHttpClient(), headers);

                UserV2 userV2 = resultBean.getResult();

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("userInfo", userV2);
                startActivity(intent);
                finish();

            }

        }
    };
    private boolean mHoldStatus;


    /**
     * show the login activity
     *
     * @param context context
     */
    public static void show(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main_login;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        mEtLoginUsername.setOnFocusChangeListener(this);
        mEtLoginUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                boolean b = AssimilateUtils.MachPhoneNum(s);
                boolean email = AssimilateUtils.machEmail(s);
                Log.e(TAG, "onTextChanged: ------------->phone=" + b + "  email=" + email);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mEtLoginPwd.setOnFocusChangeListener(this);
        mEtLoginPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void initData() {
        super.initData();

        String holdPwd = AppContext.get(HOLD_PWD_KEY, null);

        if (!TextUtils.isEmpty(holdPwd)) {
            byte[] decode = Base64.decode(holdPwd, Base64.DEFAULT);
            try {
                String tempPwd = new String(decode, 0, decode.length, "utf-8");

                Log.e(TAG, "initData: --------------->tempPwd=" + tempPwd);
                mEtLoginPwd.setText(tempPwd);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            mEtLoginPwd.setText(null);
        }
        mHoldStatus = AppContext.get(HOLD_PWD_STATUS, false);
        Log.e(TAG, "initData: --------------->" + mHoldStatus);
        updateHoldPwd(mHoldStatus);

    }

    private void updateHoldPwd(boolean holdStatus) {
        if (holdStatus) {
            mIvHoldPwd.setImageResource(R.mipmap.checkbox_checked);
        } else {
            mIvHoldPwd.setImageResource(R.mipmap.checkbox_normal);
        }
    }


    @OnClick({R.id.tv_login_forget_pwd, R.id.iv_login_hold_pwd, R.id.bt_login_submit, R.id.bt_login_register,
            R.id.ib_login_weibo, R.id.ib_login_wx, R.id.ib_login_qq})
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_login_forget_pwd:
                RegisterStepOneActivity.show(LoginActivity.this);
                break;
            case R.id.bt_login_submit:

                String tempUsername = "771297972@qq.com";//mEtLoginUsername.getText().toString().trim();
                String tempPwd = "123456";//mEtLoginPwd.getText().toString().trim();


                if (!TextUtils.isEmpty(tempUsername) && !TextUtils.isEmpty(tempPwd)) {

                    boolean machPhoneNum = AssimilateUtils.MachPhoneNum(tempUsername);
                    boolean machEmail = AssimilateUtils.machEmail(tempUsername);

                    if (machPhoneNum || machEmail) {
                        //登录成功,请求数据进入用户个人中心页面

                        String appToken = "123";//"765e06cc569b5b8ed41a4a8c979338c888d644f4";//Verifier.getPrivateToken(getApplication());

                        OSChinaApi.login(tempUsername, Sha1toHex(tempPwd), appToken, new TextHttpResponseHandler() {
                            @Override
                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                                Type type = new TypeToken<ResultBean<UserV2>>() {
                                }.getType();

                                Log.e(TAG, "onSuccess: --------->" + responseString);
                                ResultBean<UserV2> resultBean = AppContext.createGson().fromJson(responseString, type);
                                if (resultBean.isSuccess()) {

                                    //1. 更新相关Cookie信息
                                    ApiHttpClient.updateCookie(ApiHttpClient.getHttpClient(), headers);
                                    //2. 更新本地用户缓存信息
                                    UserV2 userV2 = resultBean.getResult();
                                    Log.e(TAG, "onSuccess: -------------->" + userV2.toString());
                                    boolean saveUserCache = UserCacheManager.initUserManager().saveUserCache(LoginActivity.this, userV2);
                                    if (saveUserCache) {
                                        //3. finish  进入用户中心页
                                        finish();
                                    }

                                } else {
                                    //更新失败因该是不进行任何的本地操作
                                    // AppContext.getInstance().cleanLoginInfo();
                                    // AppContext.getInstance().cleanLoginInfoV2();
                                }

                            }
                        });


                    } else {
                        AppContext.showToast(getString(R.string.hint_username_ok), Toast.LENGTH_SHORT);
                    }

                } else {
                    AppContext.showToast(getString(R.string.hint_pwd_null), Toast.LENGTH_SHORT);
                }

                break;
            case R.id.iv_login_hold_pwd:
                //记住密码
                String inputPwd = mEtLoginPwd.getText().toString().trim();

                if (!TextUtils.isEmpty(inputPwd)) {

                    try {
                        byte[] bytes = inputPwd.getBytes("utf-8");
                        byte[] encode = Base64.encode(bytes, Base64.DEFAULT);

                        inputPwd = new String(encode, 0, encode.length, "utf-8");

                        AppContext.set(HOLD_PWD_KEY, inputPwd);

                        boolean holdStatus = this.mHoldStatus;
                        if (holdStatus) {
                            holdStatus = false;
                        } else {
                            holdStatus = true;
                        }

                        updateHoldPwd(holdStatus);
                        this.mHoldStatus = holdStatus;

                        AppContext.set(HOLD_PWD_STATUS, mHoldStatus);

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } else {
                    AppContext.showToast(getString(R.string.hint_pwd_null), Toast.LENGTH_SHORT);
                }

                break;
            case R.id.bt_login_register:
                RegisterStepOneActivity.show(LoginActivity.this);
                break;
            case R.id.ib_login_weibo:
                //新浪微博登录

                openType = OpenConstants.SINA;

                OpenLogin<SsoHandler> ssoHandlerOpenLogin = new OpenLogin<>();
                try {
                    ssoHandlerOpenLogin.addAppKey(OpenConstant.WB_APP_KEY)
                            .addRedirectUrl(OpenConstant.REDIRECT_URL)
                            .addWeiboAuthListener(this)
                            .toLogin(getApplicationContext(), LoginActivity.this, OpenConstants.SINA);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.ib_login_wx:
                //微信登录

                openType = OpenConstants.WECHAT;

                OpenLogin<IWXAPI> iwxapiOpenLogin = new OpenLogin<>();
                try {
                    iwxapiOpenLogin.addAppId(OpenConstant.WECHAT_APP_ID)
                            .toLogin(getApplicationContext(), LoginActivity.this, OpenConstants.WECHAT);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.ib_login_qq:

                openType = OpenConstants.TENCENT;

                OpenLogin<Tencent> tencentOpenLogin = new OpenLogin<>();
                try {
                    tencentOpenLogin.addAppId(OpenConstant.QQ_APP_ID)
                            .addIUiListener(this)
                            .toLogin(getApplicationContext(), LoginActivity.this, OpenConstants.TENCENT);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }

                break;
            default:
                break;
        }

    }

    @NonNull
    private String Sha1toHex(String tempPwd) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.update(tempPwd.getBytes());
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
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return tempPwd;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (openType == OpenConstants.TENCENT) {
            // 对于tencent
            // 注：在某些低端机上调用登录后，由于内存紧张导致APP被系统回收，登录成功后无法成功回传数据。
            OpenLogin<Tencent> tencentOpenLogin = new OpenLogin<>();
            tencentOpenLogin.addAppId(OpenConstant.QQ_APP_ID)
                    .addAppKey(OpenConstant.QQ_APP_KEY);
            try {
                Tencent tencent = tencentOpenLogin.createOpen(this, OpenConstants.TENCENT);
                tencent.handleLoginData(data, this);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);

        if (openType == OpenConstants.SINA) {
            // SSO 授权回调
            // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
            AuthInfo authInfo = new AuthInfo(getApplicationContext(), OpenConstant.WB_APP_KEY, OpenConstant.REDIRECT_URL, OpenConstant.SCOPE);
            SsoHandler ssoHandler = new SsoHandler(LoginActivity.this, authInfo);
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }

    }

    /**
     * sina  callback
     *
     * @param bundle bundle
     */
    @Override
    public void onComplete(Bundle bundle) {
        Log.e(TAG, "onComplete: -----------sina------->");

        Oauth2AccessToken oauth2AccessToken = Oauth2AccessToken.parseAccessToken(bundle);

        if (oauth2AccessToken.isSessionValid()) {
            Gson gson = AppContext.createGson();
            String openInfo = gson.toJson(oauth2AccessToken);
            OSChinaApi.openLogin(3, openInfo, mHandler);

        }

    }

    /**
     * sina callback
     */
    @Override
    public void onWeiboException(WeiboException e) {

        Log.e(TAG, "onWeiboException: ---------------->");

    }

    /**
     * tencent callback
     *
     * @param o json
     */
    @Override
    public void onComplete(Object o) {

        Log.e(TAG, "onComplete: -------->tencent------>" + o);

        JSONObject jsonObject = (JSONObject) o;
        OSChinaApi.openLogin(1, jsonObject.toString(), mHandler);
    }

    /**
     * tencent callback
     *
     * @param uiError uiError
     */
    @Override
    public void onError(UiError uiError) {

    }


    /**
     * tencent / sina callback
     */
    @Override
    public void onCancel() {

        Log.e(TAG, "onCancel: ------------->");

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        int id = v.getId();
        if (id == R.id.et_login_username) {
            if (hasFocus) {
                mLlLoginUsername.setActivated(true);
                mLlLoginPwd.setActivated(false);
            }

            Log.e(TAG, "onFocusChange: -----username->" + hasFocus);
        } else {
            if (hasFocus) {
                mLlLoginPwd.setActivated(true);
                mLlLoginUsername.setActivated(false);
            }

            Log.e(TAG, "onFocusChange: ---pawd--->" + hasFocus);
        }
    }
}
