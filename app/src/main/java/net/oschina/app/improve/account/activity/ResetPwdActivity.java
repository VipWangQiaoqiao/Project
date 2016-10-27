package net.oschina.app.improve.account.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class ResetPwdActivity extends BaseActivity implements View.OnClickListener, View.OnFocusChangeListener {

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


    /**
     * show the resetPwdActivity
     *
     * @param context context
     */
    public static void show(Context context) {
        Intent intent = new Intent(context, ResetPwdActivity.class);
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
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @OnClick({R.id.ib_navigation_back, R.id.iv_reset_pwd_del, R.id.bt_reset_submit})
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_reset_pwd_del:
                break;
            case R.id.bt_reset_submit:

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
}
