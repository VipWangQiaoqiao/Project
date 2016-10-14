package net.oschina.app.improve.account.activities;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseActivity;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by fei
 * on 2016/10/14.
 * desc:
 */

public class RegisterStepOneActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.et_register_username)
    EditText mEtRegisterUsername;
    @Bind(R.id.iv_register_username_del)
    ImageView mIvRegisterDel;
    @Bind(R.id.et_register_auth_code)
    EditText mEtRegisterAuthCode;
    @Bind(R.id.tv_register_sms_call)
    TextView mTvRegisterSmsCall;
    @Bind(R.id.bt_register_submit)
    Button mBtRegisterSubmit;
    @Bind(R.id.bt_register_login)
    Button mBtRegisterLogin;

    /**
     * show the register activity
     *
     * @param context context
     */
    public static void show(Context context) {
        Intent intent = new Intent(context, RegisterStepOneActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main_register_step_one;
    }


    @OnClick({R.id.iv_register_username_del, R.id.tv_register_sms_call,
            R.id.bt_register_submit, R.id.bt_register_login})
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_register_username_del:

                break;
            case R.id.tv_register_sms_call:

                break;
            case R.id.bt_register_submit:

                RegisterStepTwoActivity.show(RegisterStepOneActivity.this);

                break;
            case R.id.bt_register_login:
                LoginActivity.show(RegisterStepOneActivity.this);
                break;
            default:
                break;
        }

    }
}
