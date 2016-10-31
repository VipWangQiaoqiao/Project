package net.oschina.app.improve.account.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.SharedPreferencesCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.account.base.AccountBaseActivity;
import net.oschina.app.improve.account.constants.UserConstants;
<<<<<<< HEAD
=======
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.base.activities.BaseActivity;
>>>>>>> a7a9a61ba3c2add989efb9022fad793183819957
import net.oschina.app.improve.bean.User;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.share.constant.OpenConstant;
import net.oschina.app.improve.utils.AssimilateUtils;
import net.oschina.app.util.TDevice;
import net.oschina.open.constants.OpenConstants;
import net.oschina.open.factory.OpenLogin;

import org.json.JSONException;
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

public class LoginActivity extends AccountBaseActivity implements View.OnClickListener, IUiListener, View.OnFocusChangeListener {

    private static final String TAG = "LoginActivity";

    private static final String HOLD_PWD_KEY = "holdPwdKey";
    public static final String HOLD_USERNAME_KEY = "holdUsernameKey";
    private static final String HOLD_PWD_STATUS_KEY = "holdPwdStatusKey";

    @Bind(R.id.ll_login_username)
    LinearLayout mLlLoginUsername;
    @Bind(R.id.et_login_username)
    EditText mEtLoginUsername;
    @Bind(R.id.iv_login_username_del)
    ImageView mIvLoginUsernameDel;

    @Bind(R.id.ll_login_pwd)
    LinearLayout mLlLoginPwd;
    @Bind(R.id.et_login_pwd)
    EditText mEtLoginPwd;
    @Bind(R.id.iv_login_pwd_del)
    ImageView mIvLoginPwdDel;

    @Bind(R.id.iv_login_hold_pwd)
    ImageView mIvHoldPwd;
    @Bind(R.id.tv_login_forget_pwd)
    TextView mTvLoginForgetPwd;

    @Bind(R.id.bt_login_submit)
    Button mTvLoginSubmit;
    @Bind(R.id.bt_login_register)
    Button mTvLoginRegister;

    @Bind(R.id.ll_login_layer)
    View mLlLoginLayer;
    @Bind(R.id.ll_login_pull)
    LinearLayout mLlLoginPull;

    @Bind(R.id.ll_login_options)
    LinearLayout mLlLoginOptions;

    @Bind(R.id.ib_login_weibo)
    ImageView mIbLoginWeiBo;
    @Bind(R.id.ib_login_wx)
    ImageView mIbLoginWx;
    @Bind(R.id.ib_login_qq)
    ImageView mImLoginQq;

    private int openType;
    private String mInputPwd;
    private SsoHandler mSsoHandler;

    private TextHttpResponseHandler mHandler = new TextHttpResponseHandler() {

        @Override
        public void onStart() {
            super.onStart();
            showWaitDialog();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {

            Type type = new TypeToken<ResultBean<User>>() {
            }.getType();

            GsonBuilder gsonBuilder = new GsonBuilder();
            ResultBean<User> resultBean = gsonBuilder.create().fromJson(responseString, type);
            if (resultBean.isSuccess()) {
                User user = resultBean.getResult();
                AccountHelper.login(user, headers);
                finish();
            } else {
                AppContext.showToast(resultBean.getMessage(), Toast.LENGTH_SHORT);
            }

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
    };

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
        mLlLoginLayer.setVisibility(View.GONE);
//        mIvLoginUsernameDel.setVisibility(View.INVISIBLE);
//        mIvLoginPwdDel.setVisibility(View.INVISIBLE);
        mIvHoldPwd.setTag(true);
        mEtLoginUsername.setOnFocusChangeListener(this);
        mEtLoginUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String username = s.toString();
                if (username.length() > 0) {
                    if (AssimilateUtils.MachPhoneNum(username) || AssimilateUtils.machEmail(username)) {
                        mLlLoginUsername.setBackgroundResource(R.drawable.bg_login_input_ok);
                    } else {
                        mLlLoginUsername.setBackgroundResource(R.drawable.bg_login_input_error);
                    }
                    mIvLoginUsernameDel.setVisibility(View.VISIBLE);
                } else {
                    mLlLoginUsername.setBackgroundResource(R.drawable.bg_login_input_ok);
                    mIvLoginUsernameDel.setVisibility(View.INVISIBLE);
                }

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
                int length = s.length();
                if (length > 0) {
                    mLlLoginPwd.setBackgroundResource(R.drawable.bg_login_input_ok);
                    mIvLoginPwdDel.setVisibility(View.VISIBLE);
                } else {
                    mIvLoginPwdDel.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();

        //初始化控件状态数据
        SharedPreferences sp = getSharedPreferences(UserConstants.HOLD_ACCOUNT, Context.MODE_PRIVATE);
        String holdUsername = sp.getString(HOLD_USERNAME_KEY, null);
        String holdPwd = sp.getString(HOLD_PWD_KEY, null);
        boolean holdStatus = sp.getBoolean(HOLD_PWD_STATUS_KEY, false);

        mEtLoginUsername.setText(holdUsername);

        if (!TextUtils.isEmpty(holdPwd)) {
            byte[] decode = Base64.decode(holdPwd, Base64.DEFAULT);
            try {
                String tempPwd = new String(decode, 0, decode.length, "utf-8");

                mEtLoginPwd.setText(tempPwd);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            mEtLoginPwd.setText(null);
        }

        updateHoldPwd(holdStatus);

    }


    @Override
    protected void onResume() {
        super.onResume();
        //initData();
    }

    @Override
    protected void onPause() {
        super.onPause();

        String username = mEtLoginUsername.getText().toString().trim();
        this.mInputPwd = mEtLoginPwd.getText().toString().trim();

        SharedPreferences sp = getSharedPreferences(UserConstants.HOLD_ACCOUNT, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (!TextUtils.isEmpty(username))
            editor.putString(HOLD_USERNAME_KEY, username);

        if (mIvHoldPwd.getTag() != null) {
            if (!TextUtils.isEmpty(mInputPwd))
                editor.putString(HOLD_PWD_KEY, toBase64(mInputPwd));
            editor.putBoolean(HOLD_PWD_STATUS_KEY, true);
        } else {
            editor.putString(HOLD_PWD_KEY, "");
            editor.putBoolean(HOLD_PWD_STATUS_KEY, false);
        }

        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);

    }

    private void updateHoldPwd(boolean holdStatus) {
        if (holdStatus) {
            mIvHoldPwd.setImageResource(R.mipmap.checkbox_checked);
            mIvHoldPwd.setTag(true);
        } else {
            mIvHoldPwd.setImageResource(R.mipmap.checkbox_normal);
            mIvHoldPwd.setTag(null);
        }
    }


    @OnClick({R.id.tv_login_forget_pwd, R.id.iv_login_hold_pwd, R.id.bt_login_submit, R.id.bt_login_register,
            R.id.ll_login_pull, R.id.ib_login_weibo, R.id.ib_login_wx, R.id.ib_login_qq, R.id.ll_login_layer,
            R.id.iv_login_username_del, R.id.iv_login_pwd_del})
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_login_forget_pwd:
                RetrieveActivity.show(LoginActivity.this);
                break;
            case R.id.bt_login_submit:

                String tempUsername = mEtLoginUsername.getText().toString().trim();
                String tempPwd = mEtLoginPwd.getText().toString().trim();


                if (!TextUtils.isEmpty(tempUsername) && !TextUtils.isEmpty(tempPwd)) {

                    boolean machPhoneNum = AssimilateUtils.MachPhoneNum(tempUsername);
                    boolean machEmail = AssimilateUtils.machEmail(tempUsername);

                    if (machPhoneNum || machEmail) {
                        //登录成功,请求数据进入用户个人中心页面

                        String appToken = "123";//"765e06cc569b5b8ed41a4a8c979338c888d644f4";//Verifier.getPrivateToken(getApplication());

                        if (TDevice.hasInternet()) {
                            OSChinaApi.login(tempUsername, Sha1toHex(tempPwd), appToken, new TextHttpResponseHandler() {

                                @Override
                                public void onStart() {
                                    super.onStart();
                                    showWaitDialog();
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                }

                                @Override
                                public void onSuccess(int statusCode, Header[] headers, String responseString) {

                                    try {
                                        Type type = new TypeToken<ResultBean<User>>() {
                                        }.getType();

                                        ResultBean<User> resultBean = AppOperator.createGson().fromJson(responseString, type);
                                        if (resultBean.isSuccess()) {
                                            User user = resultBean.getResult();
                                            AccountHelper.login(user, headers);
                                            finish();
                                        } else {
                                            int code = resultBean.getCode();
                                            if (code == 211) {
                                                mLlLoginUsername.setBackgroundResource(R.drawable.bg_login_input_error);
                                            } else if (code == 212) {
                                                mLlLoginPwd.setBackgroundResource(R.drawable.bg_login_input_error);
                                            }
                                            AppContext.showToast(resultBean.getMessage(), Toast.LENGTH_SHORT);
                                            //更新失败因该是不进行任何的本地操作
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        onFailure(statusCode, headers, responseString, e);
                                    }
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
                            });

                        } else {
                            AppContext.showToast(getResources().getString(R.string.footer_type_net_error), Toast.LENGTH_SHORT);
                        }

                    } else {
                        AppContext.showToast(getString(R.string.login_input_username_hint_error), Toast.LENGTH_SHORT);
                    }
                } else {
                    AppContext.showToast(getString(R.string.hint_pwd_null), Toast.LENGTH_SHORT);
                }

                break;
            case R.id.iv_login_hold_pwd:
                //记住密码
                String inputPwd = mEtLoginPwd.getText().toString().trim();

                if (!TextUtils.isEmpty(inputPwd)) {
                    mInputPwd = toBase64(inputPwd);
                }

                boolean holdPwd;
                holdPwd = mIvHoldPwd.getTag() != null;
                updateHoldPwd(holdPwd);
                break;
            case R.id.bt_login_register:
                RegisterStepOneActivity.show(LoginActivity.this);
                break;
            case R.id.ll_login_layer:
            case R.id.ll_login_pull:
                mLlLoginPull.animate().cancel();
                mLlLoginLayer.animate().cancel();

                int height = mLlLoginOptions.getHeight();
                float progress = (mLlLoginLayer.getTag() != null && mLlLoginLayer.getTag() instanceof Float) ?
                        (float) mLlLoginLayer.getTag() : 1;
                int time = (int) (360 * progress);

                if (mLlLoginPull.getTag() != null) {
                    mLlLoginPull.setTag(null);
                    startAnimator(height, progress, time);
                } else {
                    mLlLoginPull.setTag(true);
                    mLlLoginPull.animate()
                            .translationYBy(height * progress)
                            .translationY(0)
                            .setDuration(time)
                            .start();
                    mLlLoginLayer.animate()
                            .alphaBy(1 - progress)
                            .alpha(1)
                            .setDuration(time)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    mLlLoginLayer.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {
                                    if (animation instanceof ValueAnimator) {
                                        mLlLoginLayer.setTag(((ValueAnimator) animation).getAnimatedValue());
                                    }
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    if (animation instanceof ValueAnimator) {
                                        mLlLoginLayer.setTag(((ValueAnimator) animation).getAnimatedValue());
                                    }
                                }
                            })
                            .start();
                }
                break;
            case R.id.ib_login_weibo:
                openType = OpenConstants.SINA;
                //新浪微博登录
                AuthInfo authInfo = new AuthInfo(this, OpenConstant.WB_APP_KEY, OpenConstant.REDIRECT_URL, null);
                mSsoHandler = new SsoHandler(this, authInfo);
                mSsoHandler.authorize(new WeiboAuthListener() {
                    @Override
                    public void onComplete(Bundle bundle) {

                        Oauth2AccessToken oauth2AccessToken = Oauth2AccessToken.parseAccessToken(bundle);

                        if (oauth2AccessToken.isSessionValid()) {
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("openid", oauth2AccessToken.getUid());
                                jsonObject.put("expires_in", oauth2AccessToken.getExpiresTime());
                                jsonObject.put("refresh_token", oauth2AccessToken.getRefreshToken());
                                jsonObject.put("access_token", oauth2AccessToken.getToken());
                                OSChinaApi.openLogin(OSChinaApi.LOGIN_WEIBO, jsonObject.toString(), mHandler);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    @Override
                    public void onWeiboException(WeiboException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
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
                finish();
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
            case R.id.iv_login_username_del:
                mEtLoginUsername.setText(null);
                break;
            case R.id.iv_login_pwd_del:
                mEtLoginPwd.setText(null);
                break;
            default:
                break;
        }

    }

    private void startAnimator(int height, float progress, int time) {
        mLlLoginPull.animate()
                .translationYBy(height - height * progress)
                .translationY(height)
                .setDuration(time)
                .start();

        mLlLoginLayer.animate()
                .alphaBy(1 * progress)
                .alpha(0)
                .setDuration(time)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationCancel(Animator animation) {
                        if (animation instanceof ValueAnimator) {
                            mLlLoginLayer.setTag(((ValueAnimator) animation).getAnimatedValue());
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (animation instanceof ValueAnimator) {
                            mLlLoginLayer.setTag(((ValueAnimator) animation).getAnimatedValue());
                        }
                        mLlLoginLayer.setVisibility(View.GONE);
                    }
                })
                .start();
    }

    @NonNull
    private String toBase64(String inputPwd) {
        if (!TextUtils.isEmpty(inputPwd)) {
            try {
                byte[] bytes = inputPwd.getBytes("utf-8");
                byte[] encode = Base64.encode(bytes, Base64.DEFAULT);
                inputPwd = new String(encode, 0, encode.length, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return inputPwd;
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
            if (mSsoHandler != null)
                mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /**
     * tencent callback
     *
     * @param o json
     */
    @Override
    public void onComplete(Object o) {
        JSONObject jsonObject = (JSONObject) o;
        OSChinaApi.openLogin(OSChinaApi.LOGIN_QQ, jsonObject.toString(), mHandler);
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
     * tencent callback
     */
    @Override
    public void onCancel() {

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        int id = v.getId();

        if (id == R.id.et_login_username) {
            if (hasFocus) {
                mLlLoginUsername.setActivated(true);
                mLlLoginPwd.setActivated(false);
            }
        } else {
            if (hasFocus) {
                mLlLoginPwd.setActivated(true);
                mLlLoginUsername.setActivated(false);
            }
        }
    }
}
