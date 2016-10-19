package net.oschina.app.improve.account.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseActivity;
import net.oschina.app.improve.share.constant.OpenConstant;
import net.oschina.open.constants.OpenConstants;
import net.oschina.open.factory.OpenLogin;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by fei on 2016/10/14.
 * desc:
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener, WeiboAuthListener, IUiListener {

    @Bind(R.id.et_login_username)
    EditText mEtLoginUsername;
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
    }

    @Override
    protected void initData() {
        super.initData();
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

                break;
            case R.id.iv_login_hold_pwd:

                break;
            case R.id.bt_login_register:
                RegisterStepOneActivity.show(LoginActivity.this);
                break;
            case R.id.ib_login_weibo:
                //新浪微博登录

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
                OpenLogin<IWXAPI> iwxapiOpenLogin = new OpenLogin<>();
                try {
                    iwxapiOpenLogin.addAppId(OpenConstant.QQ_APP_ID)
                            .addAppKey(OpenConstant.QQ_APP_KEY)
                            .addIUiListener(this)
                            .toLogin(getApplicationContext(), LoginActivity.this, OpenConstants.TENCENT);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.ib_login_qq:

                break;
            default:
                break;
        }

    }

    @Override
    public void onComplete(Bundle bundle) {

    }

    @Override
    public void onWeiboException(WeiboException e) {

    }

    @Override
    public void onComplete(Object o) {

    }

    @Override
    public void onError(UiError uiError) {

    }

    @Override
    public void onCancel() {

    }
}
