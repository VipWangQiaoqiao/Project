package net.oschina.app.improve.account.activities;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseActivity;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by fei on 2016/10/14.
 * desc:
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {

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
    @Bind(R.id.gv_login_footer)
    GridView mGvLoginFooter;


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


    @OnClick({R.id.tv_login_forget_pwd, R.id.iv_login_hold_pwd, R.id.bt_login_submit, R.id.bt_login_register})
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
            default:
                break;
        }

    }
}
