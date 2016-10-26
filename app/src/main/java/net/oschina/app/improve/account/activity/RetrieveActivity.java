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

public class RetrieveActivity extends BaseActivity implements View.OnClickListener, View.OnFocusChangeListener {

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
        mEtRetrieveCodeInput.setOnFocusChangeListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
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
                break;
            case R.id.retrieve_sms_call:
                break;
            case R.id.bt_retrieve_submit:
                ResetPwdActivity.show(this);
                break;
            case R.id.tv_retrieve_label:
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
